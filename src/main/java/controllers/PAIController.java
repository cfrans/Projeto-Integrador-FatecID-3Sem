package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import java.time.LocalDate;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import database.ConexaoDB;
import util.AlunoItem;
import util.SessaoUsuario;

public class PAIController extends BaseController implements Initializable {

    @FXML private TextField tfMeta2;
    @FXML private TextField tfMeta3;
    @FXML private Button btLimpar;
    @FXML private Button btSalvar;
    @FXML private Button btVoltar;
    @FXML private DatePicker dpRevisaoPlano;
    @FXML private TextArea taDescricaoPlano;
    @FXML private TextField tfMeta;
    @FXML private TextField tfRA;
    @FXML private TextField tfRecursos;
    @FXML private TextField tfSerieTurma;
    @FXML private TextField tfTituloPlano;
    @FXML private ChoiceBox<AlunoItem> chNome;
    @FXML private TextField tfResponsavelPlano;

    // Lista para guardar os dados do banco
    private final ObservableList<AlunoItem> listaAlunos = FXCollections.observableArrayList();

    /**
     * Inicializa o controlador após o carregamento do arquivo FXML
     *
     * @param location   localização do arquivo FXML (não usado diretamente)
     * @param resources  recursos de internacionalização (não utilizado)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Vincular lista de Alunos
        chNome.setItems(listaAlunos);

        // 2. Carregar Alunos
        carregarAlunos();

        // 3. Fazer os campos RA e Turma atualizarem sozinhos
        chNome.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> preencherDadosAluno(newVal)
        );

        // 4. Preenche o responsável com o usuário logado e desabilita o campo
        if (SessaoUsuario.getNomeUsuario() != null) {
            tfResponsavelPlano.setText(SessaoUsuario.getNomeUsuario());
            tfResponsavelPlano.setDisable(true); // Deixa o campo cinza/não editável
        } else {
            // Caso ninguém esteja logado (segurança)
            tfResponsavelPlano.setText("Nenhum usuário logado");
            tfResponsavelPlano.setDisable(true);
        }

        // --- Bloqueia tipos não permitidos nos campos digitáveis e ajusta tamanhos ---
        desabilitarDatas(dpRevisaoPlano, TipoBloqueio.PASSADAS);
        limitarTamanhoCampo(tfTituloPlano, 100);
        limitarTamanhoCampo(tfMeta, 100);
        limitarTamanhoCampo(tfMeta2, 255);
        limitarTamanhoCampo(tfMeta3, 255);
        limitarTamanhoCampo(tfRecursos, 100);
    }

    /**
     * Carrega a lista de alunos do banco de dados e popula a coleção
     *
     * Em caso de falha na conexão ou na execução da consulta, o método exibe
     * um alerta de erro para o usuário, além de registrar o stack trace no console
     *
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
            exibirAlertaErro("Erro de Banco", "Falha Crítica ao Carregar Alunos", "Não foi possível carregar a lista de alunos. " + e.getMessage());
        }
    }

    /**
     * Preenche automaticamente os campos de RA e Série/Turma na interface com base no aluno selecionado
     *
     * Em caso de falha na consulta, o erro é registrado no console.
     *
     * @param aluno o objeto {@link AlunoItem} que representa o aluno
     *              selecionado na interface; pode ser {@code null}
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
     * Manipula o evento do botão **Salvar**, realizando a criação de um novo
     * Plano de Atendimento Individual (PAI) no banco de dados
     * Em caso de falha, mensagens de erro são exibidas com detalhes sobre o problema encontrado
     *
     * @param event o evento gerado pelo clique no botão **Salvar**
     */
    @FXML
    void onClickSalvar(ActionEvent event) {

        String sqlInsertPAI = "INSERT INTO pai (titulo, descricao, meta, meta2, meta3, recurso_necessario, prazo_revisao, status, id_aluno, id_usuario) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Pegar os IDs dos itens selecionados
        AlunoItem alunoSel = chNome.getValue();
        // Pega o ID do usuário logado na SESSÃO
        int idUsuarioLogado = SessaoUsuario.getIdUsuario();

        // Validação
        if (alunoSel == null || tfTituloPlano.getText().isEmpty() || dpRevisaoPlano.getValue() == null) {
            exibirAlertaErro("Erro de Validação", "Campos obrigatórios", "Aluno, Título e Prazo de Revisão são obrigatórios.");
            return;
        }

        // Validação da Data de revisão
        if (dpRevisaoPlano.getValue() != null) {
            if (dpRevisaoPlano.getValue().isBefore(LocalDate.now())) {
                exibirAlertaErro(
                        "Data Inválida",
                        "A Data da Revisão não pode ser no passado.",
                        "Você selecionou: " + dpRevisaoPlano.getValue().format(DATA_FORMATTER) + ". Por favor, insira uma data posterior."
                );
                return;
            }
        }

        if (idUsuarioLogado == 0) {
            exibirAlertaErro("Erro de Sessão", "Usuário não logado", "Não foi possível identificar o usuário logado. Faça login novamente.");
            return;
        }

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmtInsertPAI = conn.prepareStatement(sqlInsertPAI)) {

            System.out.println("---  Inserindo novo PAI ---");
            System.out.println("ID Aluno: " + alunoSel.getId());
            System.out.println("ID Usuário (Responsável): " + idUsuarioLogado);

            // Ordem dos parâmetros

            // 1. titulo
            stmtInsertPAI.setString(1, tfTituloPlano.getText());
            // 2. descricao
            stmtInsertPAI.setString(2, taDescricaoPlano.getText());
            // 3. meta
            stmtInsertPAI.setString(3, tfMeta.getText());
            // 4. meta2 (NOVO)
            stmtInsertPAI.setString(4, tfMeta2.getText());
            // 5. meta3 (NOVO)
            stmtInsertPAI.setString(5, tfMeta3.getText());
            // 6. recurso_necessario
            stmtInsertPAI.setString(6, tfRecursos.getText());
            // 7. prazo_revisao
            stmtInsertPAI.setDate(7, Date.valueOf(dpRevisaoPlano.getValue()));
            // 8. status
            stmtInsertPAI.setString(8, "Em Andamento"); // Status inicial
            // 9. id_aluno
            stmtInsertPAI.setInt(9, alunoSel.getId());
            // 10. id_usuario (da SESSÃO)
            stmtInsertPAI.setInt(10, idUsuarioLogado);

            stmtInsertPAI.executeUpdate();

            NavegadorUtil.exibirSucessoAlerta(
                    "Sucesso",
                    "PAI salvo e associado ao aluno com sucesso!",
                    menuController.getStage()
            );
            onClickLimpar(null);

        } catch (SQLException e) {
            exibirAlertaErro("Erro de Banco de Dados", "Não foi possível salvar o PAI.", "Erro: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            exibirAlertaErro("Erro Inesperado", "Ocorreu um erro.", "Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Manipula o evento de clique no botão Limpar, apagando todos os campos do formulário de criação de PAI
     *
     * @param event o evento de clique do botão associado ao método
     */
    @FXML void onClickLimpar(ActionEvent event) {
        System.out.println("Limpando formulário PAI...");

        //Limpando TextFields
        tfRA.clear();
        tfMeta.clear();
        tfMeta2.clear();
        tfMeta3.clear();
        tfRecursos.clear();
        tfSerieTurma.clear();
        tfTituloPlano.clear();
        // O tfResponsavelPlano não é limpo, pois ele é estático (usuário logado)

        //Limpando ChoiceBoxes (Dropdowns)
        chNome.getSelectionModel().clearSelection();

        //Limpando o DatePicker
        dpRevisaoPlano.setValue(null);

        //Limpando o TextArea
        taDescricaoPlano.clear();

        //Coloca o foco do cursor de volta no primeiro campo
        chNome.requestFocus();
    }

}