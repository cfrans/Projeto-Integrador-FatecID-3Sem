package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.sql.Connection;
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
import util.SessaoUsuario;
import util.TipoParticipacaoItem;

public class RendimentoController extends BaseController implements Initializable {

    /** Limpa os dados preenchidos da tela. */
    @FXML private Button btLimpar;
    /** Salva os dados preenchidos na tela. */
    @FXML private Button btSalvar;
    /** Volta para a tela anterior. */
    @FXML private Button btVoltar;
    /** Checkbox com o valor 'Totalmente Entregue'. */
    @FXML private CheckBox cbTipoEntrega1;
    /** Checkbox com o valor 'Parcialmente Entregue'. */
    @FXML private CheckBox cbTipoEntrega2;
    /** Checkbox com o valor 'Nenhum Entregue'. */
    @FXML private CheckBox cbTipoEntrega3;
    /** Área de texto para a área 'Justificativa'. */
    @FXML private TextArea taJustificativa;
    /** Campo de texto para a área 'Atitude Acadêmica'. */
    @FXML private TextField tfAtitudeAcademica;
    /** Campo de texto para a área 'Avaliação 1'. */
    @FXML private TextField tfAvaliacao1;
    /** Campo de texto para a área 'Avaliação 2'. */
    @FXML private TextField tfAvaliacao2;
    /** Campo de texto para a área 'Justificativa de Participação'. */
    @FXML private TextField tfJustificativaPartifipacao;
    /** Campo de texto para a área 'RA'. */
    @FXML private TextField tfRA;
    /** Campo de texto para a área 'Série'. */
    @FXML private TextField tfSerieTurma;
    /** Campo de texto para a área 'Simulado'. */
    @FXML private TextField tfSimulado;
    /** Dropdown para a seleção 'Aluno'. */
    @FXML private ChoiceBox<AlunoItem> chNome;
    /** Dropdown para a seleção 'Matéria'. */
    @FXML private ChoiceBox<DisciplinaItem> chNome1;
    /** Dropdown para a seleção 'Participação'. */
    @FXML private ChoiceBox<TipoParticipacaoItem> chNivelParticipacao;

    // Listas para guardar os dados do banco
    private final ObservableList<AlunoItem> listaAlunos = FXCollections.observableArrayList();
    private final ObservableList<DisciplinaItem> listaDisciplinas = FXCollections.observableArrayList();
    private final ObservableList<TipoParticipacaoItem> listaParticipacao = FXCollections.observableArrayList();

    // Lista de Checkboxes para o helper
    private List<CheckBox> listaEntregas;

    /**
     * Construtor padrão da classe.
     */
    public RendimentoController() {
        // Construtor
    }

    /**
     * Método de inicialização do controller.
     * Chamado automaticamente pelo FXML loader após a injeção dos campos.
     * Configura os bindings, listeners e carrega os dados iniciais.
     *
     * @param location  A localização usada para resolver caminhos relativos (pode ser nulo).
     * @param resources Os recursos usados para localizar o objeto (pode ser nulo).
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Vincular as listas aos ChoiceBoxes
        chNome.setItems(listaAlunos);
        chNome1.setItems(listaDisciplinas);
        chNivelParticipacao.setItems(listaParticipacao);

        // 2. Carregar os dados
        carregarAlunos();
        carregarMaterias();
        carregarTiposParticipacao();

        // 3. Agrupar checkboxes
        listaEntregas = Arrays.asList(cbTipoEntrega1, cbTipoEntrega2, cbTipoEntrega3);

        // Permite que apenas um seja selecionado
        for (CheckBox cbAtual : listaEntregas) {
            cbAtual.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    for (CheckBox outroCb : listaEntregas) {
                        if (outroCb != cbAtual) {
                            outroCb.setSelected(false);
                        }
                    }
                }
            });
        }

        // 4. Fazer os campos RA e Turma atualizarem sozinhos
        chNome.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> preencherDadosAluno(newVal)
        );

        // --- Bloqueia tipos não permitidos nos campos digitáveis e ajusta tamanhos ---
        campoSomenteNumeros(tfRA);
        limitarTamanhoCampo(tfRA, 20);
        limitarTamanhoCampo(tfAvaliacao1, 4);
        limitarTamanhoCampo(tfAvaliacao2, 4);
        limitarTamanhoCampo(tfSimulado, 4);
        limitarTamanhoCampo(tfAtitudeAcademica, 4);

        // Bloqueio de notas
        configurarCampoNota(tfAvaliacao1);
        configurarCampoNota(tfAvaliacao2);
        configurarCampoNota(tfSimulado);
        configurarCampoNota(tfAtitudeAcademica);
    }

    /**
     * Busca todos os alunos cadastrados da tabela 'aluno' para preenchimento na tela 'Rendimento'
     * e os carrega na {@link #listaAlunos}.
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
     * Busca todas as matérias da tabela 'materia' para preenchimento na tela 'Rendimento'
     * e as carrega na {@link #listaDisciplinas}.
     */
    private void carregarMaterias() {
        String sql = "SELECT id_materia, nome FROM materia ORDER BY nome";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            listaDisciplinas.clear();
            while (rs.next()) {
                listaDisciplinas.add(new DisciplinaItem(rs.getInt("id_materia"), rs.getString("nome")));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar matérias (antigas disciplinas)!");
            e.printStackTrace();
        }
    }

    /**
     * Carrega os tipos de participação do banco de dados
     * e os carrega na {@link #listaParticipacao}.
     */
    private void carregarTiposParticipacao() {
        String sql = "SELECT id_tipo_participacao, nome FROM tipo_participacao ORDER BY id_tipo_participacao";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            listaParticipacao.clear();
            while (rs.next()) {
                listaParticipacao.add(new TipoParticipacaoItem(
                        rs.getInt("id_tipo_participacao"),
                        rs.getString("nome")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar tipos de participação!");
            e.printStackTrace();
        }
    }

    /**
     * Busca os dados (RA e Turma) do aluno e preenche os campos de texto
     * {@link #tfRA} e {@link #tfSerieTurma} na tela.
     * É chamado automaticamente quando o usuário seleciona um aluno no ChoiceBox.
     *
     * @param aluno O {@link AlunoItem} selecionado no ChoiceBox. Se nulo, limpa os campos.
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
     * Ação do clique no botão 'Salvar'.
     * Valida os dados de entrada e, se válidos, insere um novo registro na tabela 'rendimento'.
     * Utiliza o ID do usuário logado a partir da {@link SessaoUsuario}.
     *
     * @param event O evento de ação que disparou o método (clique no botão).
     */
    @FXML
    void onClickSalvar(ActionEvent event) {

        String sql = "INSERT INTO rendimento (avaliacao1, avaliacao2, simulado, atitude_academica, " +
                "justificativa_participacao, justificativa_entrega, entrega, " +
                "id_materia, id_aluno, id_usuario, id_tipo_participacao) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Pegar os IDs dos itens selecionados
        AlunoItem alunoSel = chNome.getValue();
        DisciplinaItem materiaSel = chNome1.getValue();
        TipoParticipacaoItem participacaoSel = chNivelParticipacao.getValue();

        // Validação
        if (alunoSel == null || materiaSel == null || participacaoSel == null) {
            exibirAlertaErro("Seleção obrigatória", "Você precisa selecionar um Aluno, uma Matéria e um Nível de Participação.");
            return;
        }

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            System.out.println("--- Salvando Rendimento ---");

            // 1. avaliacao1
            stmt.setDouble(1, obterValorNota(tfAvaliacao1));
            // 2. avaliacao2
            stmt.setDouble(2, obterValorNota(tfAvaliacao2));
            // 3. simulado
            stmt.setDouble(3, obterValorNota(tfSimulado));
            // 4. atitude_academica
            stmt.setDouble(4, obterValorNota(tfAtitudeAcademica));
            // 5. justificativa_participacao
            stmt.setString(5, tfJustificativaPartifipacao.getText());
            // 6. justificativa_entrega (usando o <TextArea> genérico)
            stmt.setString(6, taJustificativa.getText());
            // 7. entrega (usando o helper dos checkboxes)
            stmt.setString(7, getEntregaSelecionada());

            // IDs de Foreign Key
            // 8. id_materia
            stmt.setInt(8, materiaSel.getId());
            // 9. id_aluno
            stmt.setInt(9, alunoSel.getId());

            // 10. id_usuario (!!! ATENÇÃO !!!)
            int idUsuarioLogado = SessaoUsuario.getIdUsuario();
            stmt.setInt(10, idUsuarioLogado);

            // 11. id_tipo_participacao
            stmt.setInt(11, participacaoSel.getId());

            stmt.executeUpdate();

            System.out.println("Rendimento salvo com sucesso!");

            // Sucesso
            NavegadorUtil.exibirSucessoAlerta(
                    "Sucesso",
                    "Rendimento salvo com sucesso!!",
                    menuController.getStage()
            );
            onClickLimpar(null);

        } catch (NumberFormatException e) {
            exibirAlertaErro("Erro de Formato", "Todos os campos de nota (Avaliação 1, 2, Simulado, Atitude) devem ser números (use . (ponto) para decimais, ex: 8.5).");
            e.printStackTrace();
        } catch (SQLException e) {
            exibirAlertaErro("Erro de Banco de Dados", "Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método auxiliar para juntar os textos dos CheckBoxes de entrega que estão selecionados.
     *
     * @return Uma String única com os textos dos CheckBoxes selecionados, separados por ", ".
     * (Ex: "Totalmente Entregues, Parcialmente Entregues")
     */
    private String getEntregaSelecionada() {
        return listaEntregas.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.joining(", ")); // Ex: "Totalmente Entregues, Parcialmente Entregues"
    }

    /**
     * Exibe um pop-up de Alerta do tipo ERRO padronizado.
     *
     * @param cabecalho O texto do cabeçalho do alerta.
     * @param conteudo  O texto principal do alerta.
     */
    private void exibirAlertaErro(String cabecalho, String conteudo) {
        Alert alertErro = new Alert(Alert.AlertType.ERROR);
        alertErro.setTitle("Erro");
        alertErro.setHeaderText(cabecalho);
        alertErro.setContentText(conteudo);
        alertErro.showAndWait();
    }

    /**
     * Configura um TextField para aceitar apenas notas (0 a 10) com vírgula.
     * Ex: 8,5 | 10 | 0,5
     */
    protected void configurarCampoNota(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Se apagar tudo, deixa passar
            if (newValue.isEmpty()) return;

            // 1. REGEX: Se tiver qualquer coisa que NÃO seja número ou vírgula, volta
            if (!newValue.matches("[\\d,]*")) {
                textField.setText(oldValue);
                return;
            }

            // 2. CONTAGEM: Se tiver mais de uma vírgula, volta
            long qtdVirgulas = newValue.chars().filter(ch -> ch == ',').count();
            if (qtdVirgulas > 1) {
                textField.setText(oldValue);
                return;
            }

            // 3. LIMITE: Verifica se é maior que 10
            try {
                // Substitui virgula por ponto apenas para verificar o valor numérico
                String valorParaVerificar = newValue.replace(",", ".");

                // Se o usuário digitou apenas "," ou "8," ainda não validamos valor, deixa continuar
                if (valorParaVerificar.endsWith(".")) {
                    return;
                }

                double valor = Double.parseDouble(valorParaVerificar);
                if (valor > 10.0) {
                    textField.setText(oldValue); // Bloqueia se for 10.1 ou 11, etc.
                }
            } catch (NumberFormatException e) {
                // Se der erro de conversão bizarro, restaura o valor antigo por segurança
                textField.setText(oldValue);
            }
        });
    }

    /**
     * Helper para converter o texto da tela (que pode ter vírgula) para Double do banco.
     */
    protected Double obterValorNota(TextField textField) {
        if (textField.getText() == null || textField.getText().isEmpty()) {
            return 0.0;
        }
        // Troca a vírgula visual (PT-BR) por ponto (padrão Java/SQL)
        String valorString = textField.getText().replace(",", ".");
        try {
            return Double.parseDouble(valorString);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Ação do clique no botão 'Limpar'.
     * Reseta todos os campos do formulário para seus estados iniciais.
     * Coloca o foco de volta no ChoiceBox de nome de aluno.
     *
     * @param event O evento de ação que disparou o método (clique no botão).
     */
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
}