package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import database.ConexaoDB;
import util.AlunoItem;
import util.UsuarioItem;

public class IntervencoesController implements Initializable {

    @FXML private Button btLimpar;
    @FXML private Button btSalvar;
    @FXML private Button btVoltar;
    @FXML private CheckBox cbTipoIntervencao1;
    @FXML private CheckBox cbTipoIntervencao2;
    @FXML private CheckBox cbTipoIntervencao3;
    @FXML private CheckBox cbTipoIntervencao4;
    @FXML private CheckBox cbTipoIntervencao5;
    @FXML private DatePicker dpDataIntervencao;
    @FXML private TextArea taObservacoes;
    @FXML private TextField tfOutroTipoIntervencao;
    @FXML private TextField tfRA;
    @FXML private TextField tfSerieTurma;
    @FXML private TextField tfTituloIntervencao;
    @FXML private ChoiceBox<AlunoItem> chNome;
    @FXML private TextField tfResponsavelIntervencao;

    private ObservableList<AlunoItem> listaAlunos = FXCollections.observableArrayList();
    private ObservableList<UsuarioItem> listaUsuarios = FXCollections.observableArrayList();

    // Lista de Checkboxes para o helper
    private List<CheckBox> listaTiposIntervencao;

    /**
     * Roda quando a tela é carregada
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Vincular as listas aos ChoiceBoxes
        chNome.setItems(listaAlunos);
        //chResponsavel.setItems(listaUsuarios); // Agora isso funciona

        // 2. Carregar os dados do banco
        carregarAlunos();
        carregarUsuarios();

        // 3. Agrupar checkboxes para facilitar a limpeza e leitura
        listaTiposIntervencao = Arrays.asList(
                cbTipoIntervencao1, cbTipoIntervencao2, cbTipoIntervencao3,
                cbTipoIntervencao4, cbTipoIntervencao5
        );

        // 4. Fazer os campos RA e Turma atualizarem sozinhos
        chNome.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> preencherDadosAluno(newVal)
        );
    }

    /**
     * Carrega os alunos do banco
     */
    private void carregarAlunos() {
        String sql = "SELECT id_aluno, nome FROM aluno ORDER BY nome";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            listaAlunos.clear();
            while (rs.next()) {
                listaAlunos.add(new AlunoItem(rs.getInt("id_aluno"), rs.getString("nome")));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar alunos!");
            e.printStackTrace();
        }
    }

    /**
     * Carrega os usuários (Professores, Coordenadores) do banco
     */
    private void carregarUsuarios() {
        String sql = "SELECT id_usuario, nome FROM usuario ORDER BY nome";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            listaUsuarios.clear();
            while (rs.next()) {
                listaUsuarios.add(new UsuarioItem(
                        rs.getInt("id_usuario"),
                        rs.getString("nome")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar usuários!");
            e.printStackTrace();
        }
    }

    /**
     * Preenche RA e Turma ao selecionar o aluno
     */
    private void preencherDadosAluno(AlunoItem aluno) {
        if (aluno == null) {
            tfRA.clear();
            tfSerieTurma.clear();
            return;
        }

        String sql = "SELECT a.ra, s.nome AS nome_serie FROM aluno a " +
                "JOIN serie_turma s ON a.id_serie_turma = s.id_serie_turma " +
                "WHERE a.id_aluno = ?";

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, aluno.getId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                tfRA.setText(rs.getString("ra"));
                tfSerieTurma.setText(rs.getString("nome_serie"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void onClickSalvar(ActionEvent event) {

        String sql = "INSERT INTO intervencao (observacao, titulo, data, id_aluno, id_usuario) " +
                "VALUES (?, ?, ?, ?, ?)";

        // Pegar os IDs dos itens selecionados
        AlunoItem alunoSel = chNome.getValue();
        //UsuarioItem usuarioSel = tfResponsavel.getValue();

        // Validação
        if (alunoSel == null || tfTituloIntervencao.getText().isEmpty() || dpDataIntervencao.getValue() == null) {
            exibirAlertaErro("Campos obrigatórios", "Aluno, Responsável, Título e Data são obrigatórios.");
            return;
        }

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            System.out.println("--- Salvando Intervenção ---");

            // 1. observacao (Combina os campos de "Tipo" com a observação)
            stmt.setString(1, getObservacoesCompletas());
            // 2. titulo
            stmt.setString(2, tfTituloIntervencao.getText());
            // 3. data
            stmt.setDate(3, Date.valueOf(dpDataIntervencao.getValue()));
            // 4. id_aluno
            stmt.setInt(4, alunoSel.getId());
            // 5. id_usuario (Responsável pela intervenção)
           // stmt.setInt(5, usuarioSel.getId());

            stmt.executeUpdate();

            System.out.println("Intervenção salva com sucesso!");

            // Chama o metodo estatico da NavegadorUtil
            NavegadorUtil.exibirSucessoEVOLTAR(event, "Salvo com sucesso!",
                    "Intervenção salva com sucesso!");

        } catch (SQLException e) {
            exibirAlertaErro("Erro de Banco de Dados", "Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Junta os tipos de intervenção e observações em uma string.
     */
    private String getObservacoesCompletas() {
        // 1. Pega os tipos dos checkboxes
        String tipos = listaTiposIntervencao.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.joining(", "));

        StringBuilder obsCompleta = new StringBuilder();

        if (!tipos.isEmpty()) {
            obsCompleta.append("Tipos: [").append(tipos).append("].\n");
        }

        // 2. Pega o campo "Outro"
        if (!tfOutroTipoIntervencao.getText().isEmpty()) {
            obsCompleta.append("Outro Tipo: [").append(tfOutroTipoIntervencao.getText()).append("].\n");
        }

        // 3. Pega as observações gerais
        if (!taObservacoes.getText().isEmpty()) {
            obsCompleta.append("Observações: [").append(taObservacoes.getText()).append("].");
        }

        return obsCompleta.toString();
    }

    /**
     * Helper para Alertas de Erro
     */
    private void exibirAlertaErro(String cabecalho, String conteudo) {
        Alert alertErro = new Alert(Alert.AlertType.ERROR);
        alertErro.setTitle("Erro");
        alertErro.setHeaderText(cabecalho);
        alertErro.setContentText(conteudo);
        alertErro.showAndWait();
    }

    @FXML
    void onClickLimpar(ActionEvent event) {
        System.out.println("Limpando formulário de Intervenções...");

        //Limpando ChoiceBoxes (Dropdowns)
        chNome.getSelectionModel().clearSelection();
        //chResponsavel.getSelectionModel().clearSelection(); // ATUALIZADO

        //Limpando TextFields
        tfRA.clear();
        tfSerieTurma.clear();
        tfOutroTipoIntervencao.clear();
        tfTituloIntervencao.clear(); // ADICIONADO
        tfResponsavelIntervencao.clear();

        //Limpando o DatePicker
        dpDataIntervencao.setValue(null);

        //Limpando o TextArea
        taObservacoes.clear();

        //Limpando os CheckBoxes
        if (listaTiposIntervencao != null) {
            listaTiposIntervencao.forEach(cb -> cb.setSelected(false));
        }

        //Coloca o foco do cursor de volta no primeiro campo
        chNome.requestFocus();
    }

    @FXML
    void onClickVoltar(ActionEvent event) {
        System.out.println("Clicado em voltar.\nChamando o método estático de voltar ao menu");
        // Chama o metodo estatico da NavegadorUtil
        NavegadorUtil.voltarParaMenu(event);
    }

}