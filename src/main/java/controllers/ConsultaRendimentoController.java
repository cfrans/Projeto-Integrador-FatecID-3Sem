package controllers;

import database.ConexaoDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import util.AlunoItem;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ConsultaRendimentoController extends BaseController {

    @FXML private Button btImprimir;
    @FXML private Button btPesquisar;
    @FXML private Button btVoltar;

    @FXML private ChoiceBox<AlunoItem> chNome;
    @FXML private DatePicker dpDataFinal;
    @FXML private DatePicker dpDataInicio;

    @FXML private TextField tfEntregaTrabalho;
    @FXML private TextField tfMateria1;
    @FXML private TextField tfMateria2;
    @FXML private TextField tfMateria3;
    @FXML private TextField tfMateria4;
    @FXML private TextField tfMateria5;
    @FXML private TextField tfMateria6;
    @FXML private TextField tfMateria7;
    @FXML private TextField tfMateria8;

    @FXML private TextField tfNota1;
    @FXML private TextField tfNota2;
    @FXML private TextField tfNota3;
    @FXML private TextField tfNota4;
    @FXML private TextField tfNota5;
    @FXML private TextField tfNota6;
    @FXML private TextField tfNota7;
    @FXML private TextField tfNota8;
    @FXML private AnchorPane rootPane;
    @FXML private TextField tfParticipacao;

    @FXML private TextField tfQuantidadeIntervencoes;
    @FXML private TextField tfQuantidadePlanosFinalizados;
    @FXML private TextField tfQunatidadePlanos;

    @FXML private TextField tfRA;
    @FXML private TextField tfResponsavelFrequente;
    @FXML private TextField tfSerieTurma;

    /**
     * Inicializa a tela após o carregamento do FXML
     */
    @FXML
    public void initialize() {
        carregarAlunos();

        chNome.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                carregarDadosAluno(newVal.getId());
            }
        });

        carregarNomesMaterias();
    }

    /**
     * Carrega todos os alunos cadastrados no banco de dados e popula o ChoiceBox
     */
    private void carregarAlunos() {
        ObservableList<AlunoItem> lista = FXCollections.observableArrayList();

        String sql = "SELECT id_aluno, nome FROM aluno ORDER BY nome";

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new AlunoItem(rs.getInt("id_aluno"), rs.getString("nome")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        chNome.setItems(lista);
    }

    /**
     * Carrega informações básicas do aluno selecionado, como RA e série/turma, preenchendo os campos correspondentes
     * na interface.
     *
     * @param idAluno o identificador do aluno selecionado
     */
    private void carregarDadosAluno(int idAluno) {
        String sql = """
                SELECT a.ra, st.nome AS serie
                FROM aluno a
                JOIN serie_turma st ON st.id_serie_turma = a.id_serie_turma
                WHERE a.id_aluno = ?
                """;

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAluno);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                tfRA.setText(rs.getString("ra"));
                tfSerieTurma.setText(rs.getString("serie"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ação executada ao clicar no botão "Pesquisar"
     * Preenche todos os campos da interface com os resultados encontrados
     *
     * @param event o evento de clique do botão
     */
    @FXML
    void onClickPesquisar(ActionEvent event) {

        AlunoItem aluno = chNome.getValue();
        LocalDate inicio = dpDataInicio.getValue();
        LocalDate fim = dpDataFinal.getValue();

        if (aluno == null || inicio == null || fim == null) {
            showAlert("Preencha todos os campos!");
            return;
        }

        int idAluno = aluno.getId();

        Map<String, Double> medias = buscarMediasPorMateria(idAluno, inicio, fim);

        tfNota1.setText(format(medias.get("Português")));
        tfNota2.setText(format(medias.get("Redação")));
        tfNota3.setText(format(medias.get("Matemática")));
        tfNota4.setText(format(medias.get("Ciências")));
        tfNota5.setText(format(medias.get("História")));
        tfNota6.setText(format(medias.get("Geografia")));
        tfNota7.setText(format(medias.get("Educação Física")));
        tfNota8.setText(format(medias.get("Inglês")));


        tfEntregaTrabalho.setText(buscarEntregaMaisFrequente(idAluno, inicio, fim));

        tfParticipacao.setText(buscarTipoParticipacaoMaisFrequente(idAluno, inicio, fim));

        tfQuantidadeIntervencoes.setText(String.valueOf(buscarQuantidadeIntervencoes(idAluno, inicio, fim)));

        tfResponsavelFrequente.setText(buscarResponsavelMaisFrequente(idAluno));

        tfQunatidadePlanos.setText(String.valueOf(buscarQuantidadePlanos(idAluno)));
        tfQuantidadePlanosFinalizados.setText(String.valueOf(buscarQuantidadePlanosFinalizados(idAluno)));
    }

    /**
     * Calcula a média geral das avaliações do aluno em cada matéria dentro do intervalo de datas especificado
     *
     * @param idAluno o identificador do aluno
     * @param inicio  a data inicial do intervalo (inclusive)
     * @param fim a data final do intervalo (inclusive)
     * @return um mapa onde a chave é o nome da matéria e o valor é a média calculada
     */
    private Map<String, Double> buscarMediasPorMateria(int idAluno, LocalDate inicio, LocalDate fim) {

        Map<String, Double> medias = new HashMap<>();

        String sql = """
                SELECT m.nome, AVG((r.avaliacao1 + r.avaliacao2 + r.simulado + r.atitude_academica)/4.0) AS media
                FROM rendimento r
                JOIN materia m ON m.id_materia = r.id_materia
                WHERE r.id_aluno = ?
                AND r.data::date BETWEEN ? AND ?
                GROUP BY m.nome
                """;

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAluno);
            ps.setDate(2, Date.valueOf(inicio));
            ps.setDate(3, Date.valueOf(fim));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                medias.put(rs.getString("nome"), rs.getDouble("media"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return medias;
    }

    /**
     * Busca qual foi o tipo de entrega mais frequente realizada pelo aluno dentro do intervalo de datas informado
     *
     * @param idAluno o identificador do aluno para filtrar os registros
     * @param inicio a data inicial do intervalo de busca (inclusive)
     * @param fim a data final do intervalo de busca (inclusive)
     * @return o valor de entrega mais frequente ou "-" caso não haja dados
     */
    private String buscarEntregaMaisFrequente(int idAluno, LocalDate inicio, LocalDate fim) {
        String sql = """
                SELECT entrega, COUNT(*) AS qtd
                FROM rendimento
                WHERE id_aluno = ?
                AND data::date BETWEEN ? AND ?
                GROUP BY entrega
                ORDER BY qtd DESC
                LIMIT 1
                """;

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAluno);
            ps.setDate(2, Date.valueOf(inicio));
            ps.setDate(3, Date.valueOf(fim));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("entrega");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "-";
    }

    /**
     * Busca o tipo de participação mais frequente registrado para um aluno dentro de um intervalo de datas
     *
     * @param idAluno o identificador do aluno cujo tipo de participação mais frequente será buscado
     * @param inicio  a data inicial do intervalo da busca (inclusiva)
     * @param fim a data final do intervalo da busca (inclusiva)
     * @return o nome do tipo de participação mais frequente, ou {@code "-"} se nenhum for encontrado
     */
    private String buscarTipoParticipacaoMaisFrequente(int idAluno, LocalDate inicio, LocalDate fim) {

        String sql = """
                SELECT tp.nome, COUNT(*) AS qtd
                FROM rendimento r
                JOIN tipo_participacao tp ON tp.id_tipo_participacao = r.id_tipo_participacao
                WHERE r.id_aluno = ?
                AND r.data::date BETWEEN ? AND ?
                GROUP BY tp.nome
                ORDER BY qtd DESC
                LIMIT 1
                """;

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAluno);
            ps.setDate(2, Date.valueOf(inicio));
            ps.setDate(3, Date.valueOf(fim));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("nome");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "-";
    }

    /**
     * Retorna a quantidade de intervenções registradas para um aluno dentro de um intervalo de datas
     *
     * @param idAluno o identificador do aluno cujas intervenções devem ser contabilizadas
     * @param inicio a data inicial do intervalo da busca (inclusiva)
     * @param fim a data final do intervalo da busca (inclusiva)
     * @return a quantidade de intervenções no intervalo informado, ou {@code 0} em caso de falha na consulta
     */
    private int buscarQuantidadeIntervencoes(int idAluno, LocalDate inicio, LocalDate fim) {

        String sql = """
                SELECT COUNT(*) AS qtd
                FROM intervencao
                WHERE id_aluno = ?
                AND data BETWEEN ? AND ?
                """;

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAluno);
            ps.setDate(2, Date.valueOf(inicio));
            ps.setDate(3, Date.valueOf(fim));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("qtd");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Busca o nome do usuário responsável que mais registrou intervenções para um aluno específico.
     *
     * @param idAluno o identificador do aluno para o qual o responsável mais frequente será buscado
     * @return o nome do responsável mais frequente, ou {@code "-"} se nenhum for encontrado
     */
    private String buscarResponsavelMaisFrequente(int idAluno) {
        String sql = """
        SELECT u.nome, COUNT(*) AS total
        FROM intervencao i
        JOIN usuario u ON u.id_usuario = i.id_usuario
        WHERE i.id_aluno = ?
        GROUP BY u.nome
        ORDER BY total DESC
        LIMIT 1
    """;

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAluno);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("nome");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "-";
    }


    /**
     * Busca a quantidade total de PAI cadastrados para um aluno específico.
     *
     * @param idAluno o identificador do aluno cujos planos serão contabilizados
     * @return o número total de planos cadastrados para o aluno, ou {@code 0} em caso de falha na consulta
     */
    private int buscarQuantidadePlanos(int idAluno) {

        String sql = "SELECT COUNT(*) AS qtd FROM pai WHERE id_aluno = ?";

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAluno);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt("qtd");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     /**
     * Busca a quantidade de PAI finalizados para um aluno específico
     *
     * @param idAluno o identificador do aluno cujos planos finalizados serão contabilizados
     * @return o número de planos finalizados do aluno, ou {@code 0} em caso de falha na consulta
     */
    private int buscarQuantidadePlanosFinalizados(int idAluno) {

        String sql = """
                SELECT COUNT(*) AS qtd
                FROM pai
                WHERE id_aluno = ?
                AND status = 'Finalizado'
                """;

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAluno);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt("qtd");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Este método simplesmente solicita a navegação de retorno para a tela inicial,
     * utilizando o mecanismo de navegação fornecido pela classe BaseController
     *
     * @param event o evento de ação disparado pelo clique do botão
     */
    @FXML
    void onClickVoltar(ActionEvent event) {
        navegarParaHome();
    }

    /**
     * Manipula o evento de clique no botão "Imprimir"
     * Oculta temporariamente os botões de ação para não aparecerem na impressão e gera um snapshot da interface contida no {@code rootPane}
     *
     * @param event o evento de ação disparado pelo botão "Imprimir"
     */
    @FXML
    void onClickImprimir(ActionEvent event) {

        try {
            // Ocultar botões antes de imprimir
            btImprimir.setVisible(false);
            btVoltar.setVisible(false);
            btPesquisar.setVisible(false);

            // Criar snapshot do conteúdo principal
            WritableImage snapshot = rootPane.snapshot(new SnapshotParameters(), null);

            // Mostrar os botões novamente
            btImprimir.setVisible(true);
            btVoltar.setVisible(true);
            btPesquisar.setVisible(true);

            // Imprimir imagem
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(rootPane.getScene().getWindow())) {

                ImageView iv = new ImageView(snapshot);
                iv.setPreserveRatio(true);
                iv.setFitWidth(job.getJobSettings().getPageLayout().getPrintableWidth());

                boolean success = job.printPage(iv);
                if (success) job.endJob();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro ao imprimir: " + e.getMessage());
        }
    }


    /**
     * Formata um valor numérico para exibição.
     * @param d o valor numérico a ser formatado
     * @return uma string com o valor formatado ou "-" se o valor for {@code null}
     */
    private String format(Double d) {
        return d == null ? "-" : String.format("%.1f", d);
    }

    /**
     * Carrega nos campos de texto os nomes padrão das disciplinas exibida na interface.
     */
    private void carregarNomesMaterias() {
        tfMateria1.setText("Português");
        tfMateria2.setText("Redação");
        tfMateria3.setText("Matemática");
        tfMateria4.setText("Ciências");
        tfMateria5.setText("História");
        tfMateria6.setText("Geografia");
        tfMateria7.setText("Educação Física");
        tfMateria8.setText("Inglês");
    }

    /**
     * Exibe um alerta do tipo {@code WARNING} com a mensagem informada
     * @param msg a mensagem a ser exibida no alerta
     */
    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}