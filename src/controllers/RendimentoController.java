package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
import util.DisciplinaItem;

public class RendimentoController implements Initializable {

    @FXML private Button btLimpar;
    @FXML private Button btSalvar;
    @FXML private Button btVoltar;
    @FXML private CheckBox cbTipoEntrega1; // [cite: 35]
    @FXML private CheckBox cbTipoEntrega2; // [cite: 35]

    // NOTA: O FXML [cite: 35] chama este item de 'cbTipoEntrega3'
    @FXML private CheckBox cbTipoEntrega3;

    @FXML private TextArea taJustificativa; // [cite: 36]
    @FXML private TextField tfAtitudeAcademica; // [cite: 33]
    @FXML private TextField tfAvaliacao1; // [cite: 33]
    @FXML private TextField tfAvaliacao2; // [cite: 33]
    @FXML private TextField tfJustificativaPartifipacao; // [cite: 38]
    @FXML private TextField tfRA; // [cite: 31]
    @FXML private TextField tfSerieTurma; // [cite: 31]
    @FXML private TextField tfSimulado; // [cite: 33]
    @FXML private ChoiceBox<AlunoItem> chNome; // [cite: 30]
    @FXML private ChoiceBox<DisciplinaItem> chNome1; // [cite: 37]
    @FXML private ChoiceBox<String> chNivelParticipacao; // [cite: 38]

    // Listas para guardar os dados do banco
    private ObservableList<AlunoItem> listaAlunos = FXCollections.observableArrayList();
    private ObservableList<DisciplinaItem> listaDisciplinas = FXCollections.observableArrayList();

    // Lista de Checkboxes para o helper
    private List<CheckBox> listaEntregas;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Vincular as listas aos ChoiceBoxes
        chNome.setItems(listaAlunos);
        chNome1.setItems(listaDisciplinas);

        // 2. Carregar os dados
        carregarAlunos();
        carregarDisciplinas();

        // 3. Popular dados estáticos
        chNivelParticipacao.setItems(FXCollections.observableArrayList(
                "Muito Alta", "Alta", "Média", "Baixa", "Nenhuma"
        ));

        // 4. Agrupar checkboxes
        listaEntregas = Arrays.asList(cbTipoEntrega1, cbTipoEntrega2, cbTipoEntrega3);

        // 5. Fazer os campos RA e Turma atualizarem sozinhos
        chNome.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> preencherDadosAluno(newVal)
        );
    }

    private void carregarAlunos() {
        String sql = "SELECT id_aluno, nome, serie_turma, ra FROM aluno ORDER BY nome";
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

    private void carregarDisciplinas() {
        String sql = "SELECT id_disciplina, nome FROM disciplina ORDER BY nome";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            listaDisciplinas.clear();
            while (rs.next()) {
                listaDisciplinas.add(new DisciplinaItem(rs.getInt("id_disciplina"), rs.getString("nome")));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar disciplinas!");
            e.printStackTrace();
        }
    }

    private void preencherDadosAluno(AlunoItem aluno) {
        if (aluno == null) {
            tfRA.clear();
            tfSerieTurma.clear();
            return;
        }

        String sql = "SELECT ra, serie_turma FROM aluno WHERE id_aluno = ?";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, aluno.getId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                tfRA.setText(String.valueOf(rs.getInt("ra")));
                tfSerieTurma.setText(rs.getString("serie_turma"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onClickSalvar(ActionEvent event) {
        String sql = "INSERT INTO rendimento (avaliacao_1, avaliacao_2, trimestre, consideracoes, " +
                "simulado, atitude_academica, id_disciplina, id_aluno) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // Pegar os IDs dos itens selecionados
        AlunoItem alunoSel = chNome.getValue();
        DisciplinaItem discSel = chNome1.getValue();

        // Validação
        if (alunoSel == null || discSel == null) {
            exibirAlertaErro("Seleção obrigatória", "Você precisa selecionar um Aluno e uma Disciplina.");
            return;
        }

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // --- 🚀 Debug Sysout ---
            System.out.println("---  Salvando Rendimento ---");
            System.out.println("ID Aluno: " + alunoSel.getId());
            System.out.println("ID Disciplina: " + discSel.getId());

            // 1. avaliacao_1
            stmt.setInt(1, Integer.parseInt(tfAvaliacao1.getText()));
            // 2. avaliacao_2
            stmt.setInt(2, Integer.parseInt(tfAvaliacao2.getText()));

            // 3. trimestre (!! ATENÇÃO AQUI !!)
            // O FXML não tem um campo para trimestre, mas o banco exige.
            // Estamos "chumbando" (hardcoding) o valor 1.
            // O ideal é adicionar um ChoiceBox<Integer> para trimestre.
            stmt.setInt(3, 1);
            System.out.println("Trimestre: 1 (Valor fixo! Adicionar campo no FXML)");

            // 4. consideracoes
            String entregas = "Entregas: " + getTipoEntregaSelecionada() + ". Justificativa: " + taJustificativa.getText();
            String participacao = "Participação: " + chNivelParticipacao.getValue() + ". Justificativa: " + tfJustificativaPartifipacao.getText();
            String consideracoes = entregas + " | " + participacao;
            stmt.setString(4, consideracoes);

            // 5. simulado
            stmt.setInt(5, Integer.parseInt(tfSimulado.getText()));
            // 6. atitude_academica
            stmt.setInt(6, Integer.parseInt(tfAtitudeAcademica.getText()));

            // 7. id_disciplina
            stmt.setInt(7, discSel.getId());
            // 8. id_aluno
            stmt.setInt(8, alunoSel.getId());

            stmt.executeUpdate();

            System.out.println("Rendimento salvo com sucesso!");

            // Sucesso
            NavegadorUtil.exibirSucessoEVOLTAR(event, "Salvo com sucesso!",
                    "Rendimento salvo com sucesso!");

        } catch (NumberFormatException e) {
            exibirAlertaErro("Erro de Formato", "Todos os campos de nota (Avaliação 1, 2, Simulado, Atitude) devem ser números.");
            e.printStackTrace();
        } catch (SQLException e) {
            exibirAlertaErro("Erro de Banco de Dados", "Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper para juntar os textos dos CheckBoxes de entrega.
     */
    private String getTipoEntregaSelecionada() {
        return listaEntregas.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.joining(", ")); // Ex: "Totalmente, Parcialmente"
    }

    private void exibirAlertaErro(String cabecalho, String conteudo) {
        Alert alertErro = new Alert(Alert.AlertType.ERROR);
        alertErro.setTitle("Erro");
        alertErro.setHeaderText(cabecalho);
        alertErro.setContentText(conteudo);
        alertErro.showAndWait();
    }

    @FXML
    void onClickLimpar(ActionEvent event) {
        System.out.println("Limpando formulário de Rendimento...");
        chNome.getSelectionModel().clearSelection();
        chNome1.getSelectionModel().clearSelection();
        chNivelParticipacao.getSelectionModel().clearSelection();
        tfSerieTurma.clear();
        tfRA.clear();
        tfAvaliacao1.clear();
        tfAvaliacao2.clear();
        tfAtitudeAcademica.clear();
        tfSimulado.clear();
        tfJustificativaPartifipacao.clear();
        listaEntregas.forEach(cb -> cb.setSelected(false));
        taJustificativa.clear();
        chNome.requestFocus();
    }

    @FXML
    void onClickVoltar(ActionEvent event) {
        System.out.println("Clicado em voltar.\nChamando o método estático de voltar ao menu");
        NavegadorUtil.voltarParaMenu(event);
    }
}