package controllers;

import database.ConexaoDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import util.SessaoUsuario;
import controllers.NavegadorUtil;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class DetalhesPAIController extends BaseController implements Initializable {

    // Campos do FXML
    @FXML private TextField tfNome;
    @FXML private TextField tfSerieTurma;
    @FXML private TextField tfRA;
    @FXML private TextField tfTituloPlano;
    @FXML private TextField tfResponsavelPlano;
    @FXML private TextField tfMeta;
    @FXML private TextField tfMeta2;
    @FXML private TextField tfMeta3;
    @FXML private TextField tfRecursos;
    @FXML private DatePicker dpRevisaoPlano;
    @FXML private TextArea taDescricaoPlano;
    @FXML private AnchorPane rootPane;

    // Botões
    @FXML private Button btImprimir;
    @FXML private Button btFinalizar;
    @FXML private Button btVoltar;

    // ID do PAI que está sendo visualizado
    private int idPaiAtual;
    private String tituloPaiAtual;

    /**
     * Inicializa o controller. Pega o ID do PAI da sessão e
     * chama o método para carregar os dados.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Pega o ID do PAI que foi salvo na SessaoUsuario pela tela anterior
        idPaiAtual = SessaoUsuario.getIdPaiSelecionado();

        if (idPaiAtual > 0) {
            carregarDadosPAI(idPaiAtual);
        } else {
            exibirAlertaErro(
                    "Erro",
                    "ID do PAI não encontrado.",
                    "Não foi possível carregar os detalhes do PAI. Tente voltar e selecionar novamente."
            );
        }
    }

    /**
     * Carrega todos os dados do PAI do banco de dados e preenche os campos
     * da interface.
     *
     * @param idPai O ID do PAI a ser carregado.
     */
    private void carregarDadosPAI(int idPai) {
        String sql = "SELECT " +
                "p.titulo, p.descricao, p.meta, p.meta2, p.meta3, p.recurso_necessario, p.prazo_revisao, p.status, " +
                "a.nome AS nome_aluno, " +
                "a.ra, " +
                "st.nome AS nome_serie_turma, " +
                "u.nome AS nome_responsavel " +
                "FROM pai p " +
                "JOIN aluno a ON p.id_aluno = a.id_aluno " +
                "JOIN usuario u ON p.id_usuario = u.id_usuario " +
                "JOIN serie_turma st ON a.id_serie_turma = st.id_serie_turma " +
                "WHERE p.id_pai = ?";

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPai);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Guarda o título para o pop-up de confirmação
                tituloPaiAtual = rs.getString("titulo");

                // Preenche os campos da tela com os nomes CORRETOS
                tfNome.setText(rs.getString("nome_aluno"));
                tfSerieTurma.setText(rs.getString("nome_serie_turma"));
                tfRA.setText(rs.getString("ra"));
                tfTituloPlano.setText(tituloPaiAtual);
                tfResponsavelPlano.setText(rs.getString("nome_responsavel"));
                tfMeta.setText(rs.getString("meta"));
                tfMeta2.setText(rs.getString("meta2"));
                tfMeta3.setText(rs.getString("meta3"));
                tfRecursos.setText(rs.getString("recurso_necessario"));
                taDescricaoPlano.setText(rs.getString("descricao"));
                dpRevisaoPlano.setValue(rs.getDate("prazo_revisao").toLocalDate());

                // Se o PAI já estiver "Finalizado", desabilita o botão Finalizar
                if ("Finalizado".equals(rs.getString("status"))) {
                    btFinalizar.setDisable(true);
                }

            } else {
                exibirAlertaErro(
                        "Erro de Banco de Dados",
                        "PAI não encontrado.",
                        "O PAI com ID " + idPai + " não foi localizado no banco de dados."
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
            exibirAlertaErro(
                    "Erro de Banco de Dados",
                    "Falha ao carregar PAI.",
                    "Ocorreu um erro ao consultar o banco de dados: " + e.getMessage()
            );
        }
    }


    /**
     * Manipula o clique no botão Finalizar.
     * Exibe um pop-up de confirmação e, se confirmado, atualiza o status
     * do PAI para 'Finalizado' e retorna para a tela de Andamento.
     */
    @FXML
    void onClickFinalizar(ActionEvent event) {
        // 1. Criar o Alerta de Confirmação
        Alert alertConfirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        alertConfirmacao.setTitle("Confirmar Finalização");
        alertConfirmacao.setHeaderText("Finalizar PAI");
        alertConfirmacao.setContentText("Tem certeza que deseja finalizar o PAI de título '" + tituloPaiAtual + "'?");

        // Definindo o Owner para centralizar pop-ups
        if (menuController != null && menuController.getStage() != null) {
            alertConfirmacao.initOwner(menuController.getStage());
        }

        Optional<ButtonType> resultado = alertConfirmacao.showAndWait();

        // Verificar se o usuário clicou em "OK"
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // Chamar o método para atualizar o banco
            boolean sucesso = finalizarPAINoBanco(idPaiAtual);

            if (sucesso) {
                // Mostrar alerta de sucesso
                NavegadorUtil.exibirSucessoAlerta("PAI Finalizado",
                        "O PAI foi alterado para 'Finalizado' com sucesso.",
                        menuController.getStage()
                );

                // VOLTAR para a tela de Andamento
                navegarPara("/view/AndamentoPAI.fxml");
            }
        }
    }

    /**
     * Método helper para atualizar o status do PAI no banco de dados.
     */
    private boolean finalizarPAINoBanco(int idPai) {
        String sql = "UPDATE pai SET status = 'Finalizado' WHERE id_pai = ?";

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPai);
            int linhasAfetadas = stmt.executeUpdate();

            return linhasAfetadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            exibirAlertaErro(
                    "Erro de Banco de Dados",
                    "Não foi possível finalizar o PAI.",
                    "Ocorreu um erro ao tentar atualizar o banco de dados: " + e.getMessage()
            );
            return false;
        }
    }

    /**
     * Botão Imprimir (placeholder).
     */
    @FXML
    private void onClickImprimir(ActionEvent event) {
        try {

            btImprimir.setVisible(false);
            btFinalizar.setVisible(false);
            btVoltar.setVisible(false);

            WritableImage snapshot = rootPane.snapshot(new SnapshotParameters(), null);


            btImprimir.setVisible(true);
            btFinalizar.setVisible(true);
            btVoltar.setVisible(true);


            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(rootPane.getScene().getWindow())) {

                ImageView imageView = new ImageView(snapshot);
                imageView.setPreserveRatio(true);

                double pageWidth = job.getJobSettings().getPageLayout().getPrintableWidth();
                double pageHeight = job.getJobSettings().getPageLayout().getPrintableHeight();

                double scaleX = pageWidth / snapshot.getWidth();
                double scaleY = pageHeight / snapshot.getHeight();
                double scale = Math.min(scaleX, scaleY);

                imageView.setFitWidth(snapshot.getWidth() * scale);
                imageView.setFitHeight(snapshot.getHeight() * scale);

                boolean success = job.printPage(imageView);
                if (success) {
                    job.endJob();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro ao imprimir: " + e.getMessage());
        }
    }

    private void restaurarBotoes() {
        btImprimir.setVisible(true);
        btFinalizar.setVisible(true);
        btVoltar.setVisible(true);
    }

    // Método helper local para avisos (Warnings)
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(msg);

        // Centralizando o Aviso
        if (menuController != null && menuController.getStage() != null) {
            alert.initOwner(menuController.getStage());
        }
        // ---------------------------------------------

        alert.showAndWait();
    }

    /**
     * SOBRESCRITO: Manipula o clique no botão "Voltar".
     * Navega de volta para a tela de Andamento, em vez da Home.
     */
    @FXML
    @Override
    void onClickVoltar(ActionEvent event) {
        System.out.println("DetalhesPAIController: Voltando para Andamento.");
        navegarPara("/view/AndamentoPAI.fxml");
    }
}