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
import util.SessaoUsuario;

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

    // Lista de Checkboxes para o helper
    private List<CheckBox> listaTiposIntervencao;

    /**
     * Inicializa os componentes da interface após o carregamento do FXML
     *
     * @param location  localização do arquivo FXML utilizado.
     * @param resources conjunto de recursos para internacionalização, caso existam.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Vincular a lista de Alunos
        chNome.setItems(listaAlunos);

        // 2. Carregar Alunos
        carregarAlunos();

        // 3. Agrupar checkboxes
        listaTiposIntervencao = Arrays.asList(
                cbTipoIntervencao1, cbTipoIntervencao2, cbTipoIntervencao3,
                cbTipoIntervencao4, cbTipoIntervencao5
        );

        // 4. Fazer os campos RA e Turma atualizarem sozinhos
        chNome.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> preencherDadosAluno(newVal)
        );

        // 5. Preenche o responsável com o usuário logado e desabilita o campo
        if (SessaoUsuario.getNomeUsuario() != null) {
            tfResponsavelIntervencao.setText(SessaoUsuario.getNomeUsuario());
            tfResponsavelIntervencao.setDisable(true); // Deixa o campo cinza/não editável
        } else {
            tfResponsavelIntervencao.setText("Nenhum usuário logado");
            tfResponsavelIntervencao.setDisable(true);
        }

        // 6 "Liga" a propriedade 'disable' do campo de texto ao OPOSTO da caixa de seleção
        tfOutroTipoIntervencao.disableProperty().bind(cbTipoIntervencao5.selectedProperty().not());
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
     * Preenche RA e Turma ao selecionar o aluno
     * @param aluno
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


    /**
     * Manipula o evento do botão **Salvar**, registrando uma nova intervenção para o aluno selecionado
     *
     * Em caso de falha durante a operação com o banco de dados, uma mensagem de
     * erro é apresentada ao usuário
     *
     * @param event o evento disparado pelo clique no botão Salvar
     */
    @FXML
    void onClickSalvar(ActionEvent event) {

        String sql = "INSERT INTO intervencao (observacao, titulo, data, id_aluno, id_usuario) " +
                "VALUES (?, ?, ?, ?, ?)";

        // Pegar os IDs dos itens selecionados
        AlunoItem alunoSel = chNome.getValue();
        // Pega o ID do usuário logado na SESSÃO
        int idUsuarioLogado = SessaoUsuario.getIdUsuario();

        // Validação
        if (alunoSel == null || tfTituloIntervencao.getText().isEmpty() || dpDataIntervencao.getValue() == null) {
            exibirAlertaErro("Campos obrigatórios", "Aluno, Título e Data são obrigatórios.");
            return;
        }

        if (idUsuarioLogado == 0) {
            exibirAlertaErro("Usuário não logado", "Não foi possível identificar o usuário logado. Faça login novamente.");
            return;
        }

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            System.out.println("--- Salvando Intervenção ---");

            // 1. observacao
            stmt.setString(1, getObservacoesCompletas());
            // 2. titulo
            stmt.setString(2, tfTituloIntervencao.getText());
            // 3. data
            stmt.setDate(3, Date.valueOf(dpDataIntervencao.getValue()));
            // 4. id_aluno
            stmt.setInt(4, alunoSel.getId());
            // 5. id_usuario (da SESSÃO)
            stmt.setInt(5, idUsuarioLogado);

            stmt.executeUpdate();

            System.out.println("Intervenção salva com sucesso!");

            NavegadorUtil.exibirSucessoEVOLTAR(event, "Salvo com sucesso!",
                    "Intervenção salva com sucesso!");

        } catch (SQLException e) {
            exibirAlertaErro("Erro de Banco de Dados", "Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Junta os tipos de intervenção e observações em uma string
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
     * @param cabecalho
     * @param conteudo
     */
    private void exibirAlertaErro(String cabecalho, String conteudo) {
        Alert alertErro = new Alert(Alert.AlertType.ERROR);
        alertErro.setTitle("Erro");
        alertErro.setHeaderText(cabecalho);
        alertErro.setContentText(conteudo);
        alertErro.showAndWait();
    }

    /**
     *  Método utilizado para definição do funcionamento do botão Limpar,
     *  fazendo com que todos os dados preenchidos em tela sejam apagados
     * @param event
     */
    @FXML
    void onClickLimpar(ActionEvent event) {
        System.out.println("Limpando formulário de Intervenções...");


        chNome.getSelectionModel().clearSelection();

        tfRA.clear();
        tfSerieTurma.clear();
        tfOutroTipoIntervencao.clear();
        tfTituloIntervencao.clear();

        dpDataIntervencao.setValue(null);

        taObservacoes.clear();

        //Limpando os CheckBoxes
        if (listaTiposIntervencao != null) {
            listaTiposIntervencao.forEach(cb -> cb.setSelected(false));
        }

        //Coloca o foco do cursor de volta no primeiro campo
        chNome.requestFocus();
    }

    /**
     * Manipula o evento de clique no botão Voltar, retornando o usuário
     * para a tela principal do menu
     *
     * @param event o evento gerado pelo clique no botão Voltar
     */
    @FXML
    void onClickVoltar(ActionEvent event) {
        System.out.println("Clicado em voltar.\nChamando o método estático de voltar ao menu");
        NavegadorUtil.voltarParaMenu(event);
    }

}