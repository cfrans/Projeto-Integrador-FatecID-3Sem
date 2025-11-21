package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.control.Alert;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Arrays;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import javafx.scene.control.DateCell;

// Imports do banco
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import database.ConexaoDB;

import util.SerieTurmaItem;
import util.TipoResponsavelItem;

public class CadastroController extends BaseController implements Initializable {

    @FXML
    private Button btLimpar;
    @FXML
    private Button btSalvar;
    @FXML
    private Button btVoltar;
    @FXML
    private CheckBox cbTipoNecessidade1;
    @FXML
    private CheckBox cbTipoNecessidade2;
    @FXML
    private CheckBox cbTipoNecessidade3;
    @FXML
    private CheckBox cbTipoNecessidade4;
    @FXML
    private CheckBox cbTipoNecessidade5;
    @FXML
    private Label lbParentesco;
    @FXML
    private DatePicker dpDataLaudo;
    @FXML
    private DatePicker dpDataNascimento;
    @FXML
    private TextArea taObservacoes;
    @FXML
    private TextField tdNomeResponsavel;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfNome;
    @FXML
    private TextField tfNumeroLaudo;
    @FXML
    private TextField tfProfissional;
    @FXML
    private TextField tfTelefone;
    @FXML
    private TextField tfRA;
    @FXML
    private Font x1;
    @FXML
    private Color x2;
    @FXML
    private ChoiceBox<TipoResponsavelItem> chParentesco;
    @FXML
    private ChoiceBox<SerieTurmaItem> chSerieTurma;

    // Listas para guardar os dados do banco
    private final ObservableList<TipoResponsavelItem> listaParentesco = FXCollections.observableArrayList();
    private final ObservableList<SerieTurmaItem> listaSerieTurma = FXCollections.observableArrayList();

    // Lista de Checkboxes para o helper
    private List<CheckBox> listaNecessidades;

    /**
     * 2. Este metodo roda quando o FXML é carregado, para popular os dropdowns.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Vincula as listas (que serão carregadas do banco) aos ChoiceBoxes
        chParentesco.setItems(listaParentesco);
        chSerieTurma.setItems(listaSerieTurma);

        // 2. Chama os métodos para carregar os dados do banco
        carregarParentescos();
        carregarSeriesTurmas();

        // 3. Agrupa os checkboxes para facilitar a leitura
        listaNecessidades = Arrays.asList(cbTipoNecessidade1, cbTipoNecessidade2, cbTipoNecessidade3, cbTipoNecessidade4, cbTipoNecessidade5);

        // --- Bloqueia datas futuras no calendário visualmente ---
        desabilitarDatas(dpDataLaudo, TipoBloqueio.FUTURAS);
        desabilitarDatas(dpDataNascimento, TipoBloqueio.FUTURAS);
    }

    /**
     * Carrega os tipos de parentesco do banco
     */
    private void carregarParentescos() {
        String sql = "SELECT id_tipo_responsavel, nome FROM tipo_responsavel ORDER BY id_tipo_responsavel";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            listaParentesco.clear();
            while (rs.next()) {
                listaParentesco.add(new TipoResponsavelItem(
                        rs.getInt("id_tipo_responsavel"),
                        rs.getString("nome")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar tipos de parentesco!");
            e.printStackTrace();
            exibirAlertaErro("Erro de Banco", "Falha ao carregar lista de parentescos.", e.getMessage());
        }
    }

    /**
     * Carrega as séries/turmas do banco
     */
    private void carregarSeriesTurmas() {
        String sql = "SELECT id_serie_turma, nome FROM serie_turma ORDER BY nome";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            listaSerieTurma.clear();
            while (rs.next()) {
                listaSerieTurma.add(new SerieTurmaItem(
                        rs.getInt("id_serie_turma"),
                        rs.getString("nome")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar séries e turmas!");
            e.printStackTrace();
            exibirAlertaErro("Erro de Banco", "Falha ao carregar lista de séries e turmas.", e.getMessage());
        }
    }

    /**
     * Valida se o aluno completa 6 anos até 31 de março do ano corrente.
     * Baseado no Parecer CEE 137/19. (<a href="https://atendimento.educacao.sp.gov.br/knowledgebase/article/SED-06775/pt-br">...</a>)
     * @return true se a idade for válida, false se for muito novo.
     */
    private boolean validarCorteEtario() {
        LocalDate dataNascimento = dpDataNascimento.getValue();

        // Se não tiver data preenchida, retornamos true para deixar
        // a validação de "campo obrigatório" do onClickSalvar tratar depois,
        // ou retornamos false se quiser barrar aqui mesmo.
        if (dataNascimento == null) {
            return false;
        }

        int anoAtual = LocalDate.now().getYear();

        // Data limite: 31 de Março do ano corrente
        LocalDate dataCorte = LocalDate.of(anoAtual, Month.MARCH, 31);

        // Data em que a criança completa 6 anos
        LocalDate dataQueFaz6Anos = dataNascimento.plusYears(6);

        // Se a data que ela faz 6 anos for DEPOIS da data de corte, ela é muito nova.
        if (dataQueFaz6Anos.isAfter(dataCorte)) {
            return false;
        }

        return true;
    }

    /**
     * Manipula o evento de clique no botão Salvar, realizando o cadastro
     * completo de um aluno, seu responsável e o laudo associado
     * O método executa validações dos campos obrigatórios e inicia uma transação
     * no banco de dados
     * Caso qualquer etapa falhe, a transação é revertida (rollback) para manter
     * a integridade dos dados. Se todas as operações forem concluídas com sucesso,
     * a transação é confirmada e o usuário recebe uma notificação de
     * sucesso com redirecionamento
     *
     * @param event o evento disparado pelo clique no botão Salvar
     */
    @FXML
    void onClickSalvar(ActionEvent event) {
        System.out.println("Salvar clicado. Iniciando transação...");

        // IDs para recuperar do banco
        Long idResponsavel = null;
        Long idAluno = null;

        // Pegar os IDs dos itens selecionados
        TipoResponsavelItem parentescoSel = chParentesco.getValue();
        SerieTurmaItem serieSel = chSerieTurma.getValue();

        // ---  Validação de tela básica ---
        if (parentescoSel == null || serieSel == null || tfNome.getText().isEmpty() || tdNomeResponsavel.getText().isEmpty()) {
            exibirAlertaErro("Campos Obrigatórios", "Nome, Responsável, Parentesco e Série/Turma são obrigatórios.", "");
            return;
        }

        // --- Validação da Data de Nascimento (Obrigatória para o cálculo) ---
        if (dpDataNascimento.getValue() == null) {
            exibirAlertaErro("Campos Obrigatórios", "A Data de Nascimento é obrigatória para verificar a idade.", "");
            return;
        }

        // --- Validação de Corte Etário ---
        if (!validarCorteEtario()) {
            int anoAtual = LocalDate.now().getYear();
            exibirAlertaErro(
                    "Idade Insuficiente",
                    "O aluno não atende ao critério de idade mínima (Corte Etário).",
                    "Segundo a legislação, a criança deve completar 6 anos até 31 de Março de " + anoAtual + ".\n" +
                            "Data de Nascimento informada: " + dpDataNascimento.getValue().format(DATA_FORMATTER)
            );
            return; // Para a execução aqui, não salva no banco.
        }

        // Validação da Data do Laudo (Futuro) ---
        if (dpDataLaudo.getValue() != null) {
            if (dpDataLaudo.getValue().isAfter(LocalDate.now())) {
                exibirAlertaErro(
                        "Data Inválida",
                        "A Data do Laudo não pode ser futura.",
                        "Você selecionou: " + dpDataLaudo.getValue().format(DATA_FORMATTER) + ". Por favor, insira uma data de hoje ou anterior."
                );
                return;
            }
        }

        String sqlResponsavel = "INSERT INTO responsavel (nome, id_tipo_responsavel, email, telefone) VALUES (?, ?, ?, ?) RETURNING id_responsavel";
        String sqlAluno = "INSERT INTO aluno (nome, data_nascimento, id_serie_turma, RA, id_responsavel) VALUES (?, ?, ?, ?, ?) RETURNING id_aluno";
        String sqlLaudo = "INSERT INTO laudo (numero, data, descricao, tipo, id_aluno) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoDB.getConexao()) {

            // Inicia a Transação
            conn.setAutoCommit(false);

            try (PreparedStatement stmtResp = conn.prepareStatement(sqlResponsavel);
                 PreparedStatement stmtAluno = conn.prepareStatement(sqlAluno);
                 PreparedStatement stmtLaudo = conn.prepareStatement(sqlLaudo)) {

                // --- ETAPA 1: Salvar Responsável ---
                stmtResp.setString(1, tdNomeResponsavel.getText());
                stmtResp.setInt(2, parentescoSel.getId());
                stmtResp.setString(3, tfEmail.getText());
                stmtResp.setString(4, tfTelefone.getText());

                ResultSet rsResp = stmtResp.executeQuery();
                if (rsResp.next()) {
                    idResponsavel = rsResp.getLong(1);
                    System.out.println("Responsável salvo com ID: " + idResponsavel);
                } else {
                    throw new SQLException("Falha ao salvar responsável, ID não retornado.");
                }

                // --- ETAPA 2: Salvar Aluno ---
                stmtAluno.setString(1, tfNome.getText());
                stmtAluno.setDate(2, (dpDataNascimento.getValue() != null) ? Date.valueOf(dpDataNascimento.getValue()) : null);
                stmtAluno.setInt(3, serieSel.getId());
                stmtAluno.setString(4, tfRA.getText());
                stmtAluno.setLong(5, idResponsavel);

                ResultSet rsAluno = stmtAluno.executeQuery();
                if (rsAluno.next()) {
                    idAluno = rsAluno.getLong(1);
                    System.out.println("Aluno salvo com ID: " + idAluno);
                } else {
                    throw new SQLException("Falha ao salvar aluno, ID não retornado.");
                }

                // --- ETAPA 3: Salvar Laudo ---
                stmtLaudo.setString(1, tfNumeroLaudo.getText());
                stmtLaudo.setDate(2, (dpDataLaudo.getValue() != null) ? Date.valueOf(dpDataLaudo.getValue()) : null);
                stmtLaudo.setString(3, taObservacoes.getText());
                stmtLaudo.setString(4, getTipoNecessidadeSelecionada());
                stmtLaudo.setLong(5, idAluno);

                stmtLaudo.executeUpdate();
                System.out.println("Laudo salvo com sucesso.");

                // Se tudo deu certo, confirma a transação
                conn.commit();

                System.out.println("Transação concluída com sucesso!");

                NavegadorUtil.exibirSucessoAlerta(
                        "Cadastro",
                        "Aluno, Responsável e Laudo salvos com sucesso!"
                );
                navegarParaHome(); // Método herdado do BaseController

            } catch (Exception e) {
                System.err.println("Erro durante a transação, executando rollback...");
                conn.rollback(); // DESFAZ TUDO
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Erro de SQL ao salvar no banco de dados:");
            e.printStackTrace();
            exibirAlertaErro("Erro de Banco de Dados", "Não foi possível salvar o registro.", "Erro: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro inesperado:");
            e.printStackTrace();
            exibirAlertaErro("Erro Inesperado", "Ocorreu um erro.", "Verifique se todos os campos obrigatórios e datas foram preenchidos. Erro: " + e.getMessage());
        }
    }

    /**
     * Helper para juntar os textos dos CheckBoxes em uma única String.
     */
    private String getTipoNecessidadeSelecionada() {
        return listaNecessidades.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.joining(", ")); // Ex: "TDAH, Dislexia"
    }


    /**
     * Método que controla o funcionamento do botão Limpar, realizando um clear em todos os campos da tela
     *
     * @param event
     */
    @FXML
    void onClickLimpar(ActionEvent event) {
        System.out.println("Limpando formulário de cadastro...");
        tfNome.clear();
        tfNumeroLaudo.clear();
        tfProfissional.clear();
        tdNomeResponsavel.clear();
        tfTelefone.clear();
        tfEmail.clear();
        tfRA.clear();
        dpDataNascimento.setValue(null);
        dpDataLaudo.setValue(null);
        chSerieTurma.getSelectionModel().clearSelection();
        chParentesco.getSelectionModel().clearSelection();
        listaNecessidades.forEach(cb -> cb.setSelected(false));
        taObservacoes.clear();
        tfNome.requestFocus();
    }
}