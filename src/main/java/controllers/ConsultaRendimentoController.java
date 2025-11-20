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

    // -----------------------------------------------------
    // INITIALIZE
    // -----------------------------------------------------
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

    // -----------------------------------------------------
    // 1) CARREGAR ALUNOS NO CHOICEBOX
    // -----------------------------------------------------
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

    // -----------------------------------------------------
    // 2) CARREGAR RA E SERIE/TURMA AO SELECIONAR O ALUNO
    // -----------------------------------------------------
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

    // -----------------------------------------------------
    // 3) BOTÃO PESQUISAR
    // -----------------------------------------------------
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

        // PREENCHER NA TELA (MÉDIAS)
        tfNota1.setText(format(medias.get("Redação")));
        tfNota2.setText(format(medias.get("Matemática")));
        tfNota3.setText(format(medias.get("Ciências")));
        tfNota4.setText(format(medias.get("História")));
        tfNota5.setText(format(medias.get("Geografia")));
        tfNota6.setText(format(medias.get("Educação Física")));
        tfNota7.setText(format(medias.get("Inglês")));
        tfNota8.setText(format(medias.get("Outros"))); // caso exista 8 matérias no banco

        // ENTREGA MAIS FREQUENTE
        tfEntregaTrabalho.setText(buscarEntregaMaisFrequente(idAluno, inicio, fim));

        // PARTICIPAÇÃO MAIS FREQUENTE
        tfParticipacao.setText(buscarTipoParticipacaoMaisFrequente(idAluno, inicio, fim));

        // QUANTIDADE INTERVENÇÕES
        tfQuantidadeIntervencoes.setText(String.valueOf(buscarQuantidadeIntervencoes(idAluno, inicio, fim)));

        // RESPONSÁVEL MAIS FREQUENTE
        tfResponsavelFrequente.setText(buscarResponsavelMaisFrequente(idAluno));

        // PLANOS
        tfQunatidadePlanos.setText(String.valueOf(buscarQuantidadePlanos(idAluno)));
        tfQuantidadePlanosFinalizados.setText(String.valueOf(buscarQuantidadePlanosFinalizados(idAluno)));
    }

    // -----------------------------------------------------
    // 4) MÉDIAS POR MATÉRIA
    // -----------------------------------------------------
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

    // -----------------------------------------------------
    // 5) ENTREGA DE TRABALHO MAIS FREQUENTE
    // -----------------------------------------------------
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

    // -----------------------------------------------------
    // 6) PARTICIPAÇÃO MAIS FREQUENTE
    // -----------------------------------------------------
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

    // -----------------------------------------------------
    // 7) QUANTIDADE DE INTERVENÇÕES
    // -----------------------------------------------------
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

    // -----------------------------------------------------
    // 8) RESPONSÁVEL MAIS FREQUENTE
    // -----------------------------------------------------
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



    // -----------------------------------------------------
    // 9) QUANTIDADE DE PLANOS
    // -----------------------------------------------------
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

    // -----------------------------------------------------
    // 10) QUANTIDADE DE PLANOS FINALIZADOS
    // -----------------------------------------------------
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

    // -----------------------------------------------------
    // BOTÕES EXTRAS
    // -----------------------------------------------------
    @FXML
    void onClickVoltar(ActionEvent event) {
        navegarParaHome();
    }

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



    // -----------------------------------------------------
    // FUNÇÕES AUXILIARES
    // -----------------------------------------------------
    private String format(Double d) {
        return d == null ? "-" : String.format("%.1f", d);
    }

    private void carregarNomesMaterias() {
        tfMateria1.setText("Redação");
        tfMateria2.setText("Matemática");
        tfMateria3.setText("Ciências");
        tfMateria4.setText("História");
        tfMateria5.setText("Geografia");
        tfMateria6.setText("Educação Física");
        tfMateria7.setText("Inglês");
        tfMateria8.setText("Outros");
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}




