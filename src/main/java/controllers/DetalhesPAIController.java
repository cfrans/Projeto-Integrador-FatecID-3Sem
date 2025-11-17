package controllers;

import database.ConexaoDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import util.SessaoUsuario;

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

    // Botões
    @FXML private Button btImprimir;
    @FXML private Button btFinalizar;
    @FXML private Button btVoltar;

    // ID do PAI que está sendo visualizado
    private int idPaiAtual;
    private String tituloPaiAtual; // Para usar na confirmação

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
            // Tratar erro - PAI não encontrado
            Alert alertErro = new Alert(Alert.AlertType.ERROR);
            alertErro.setTitle("Erro");
            alertErro.setHeaderText("ID do PAI não encontrado.");
            alertErro.setContentText("Não foi possível carregar os detalhes do PAI. Tente voltar e selecionar novamente.");
            alertErro.showAndWait();
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
                // PAI não encontrado no banco
                Alert alertErro = new Alert(Alert.AlertType.ERROR);
                alertErro.setTitle("Erro de Banco de Dados");
                alertErro.setHeaderText("PAI não encontrado.");
                alertErro.setContentText("O PAI com ID " + idPai + " não foi localizado no banco de dados.");
                alertErro.showAndWait();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alertErro = new Alert(Alert.AlertType.ERROR);
            alertErro.setTitle("Erro de Banco de Dados");
            alertErro.setHeaderText("Falha ao carregar PAI.");
            alertErro.setContentText("Ocorreu um erro ao consultar o banco de dados: " + e.getMessage());
            alertErro.showAndWait();
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

        // 2. Mostrar o alerta e esperar a resposta
        Optional<ButtonType> resultado = alertConfirmacao.showAndWait();

        // 3. Verificar se o usuário clicou em "OK"
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // 4. Chamar o método para atualizar o banco
            boolean sucesso = finalizarPAINoBanco(idPaiAtual);

            if (sucesso) {
                // 5. Mostrar alerta de sucesso
                NavegadorUtil.exibirSucessoAlerta("PAI Finalizado", "O PAI foi alterado para 'Finalizado' com sucesso.");

                // 6. VOLTAR para a tela de Andamento
                navegarPara("/view/AndamentoPAI.fxml");
            }
        }
        // Se o usuário clicar em "Cancelar", nada acontece.
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
            Alert alertErro = new Alert(Alert.AlertType.ERROR);
            alertErro.setTitle("Erro de Banco de Dados");
            alertErro.setHeaderText("Não foi possível finalizar o PAI.");
            alertErro.setContentText("Ocorreu um erro ao tentar atualizar o banco de dados: " + e.getMessage());
            alertErro.showAndWait();
            return false;
        }
    }

    /**
     * Botão Imprimir (placeholder).
     */
    @FXML
    void onClickImprimir(ActionEvent event) {
        System.out.println("Botão Imprimir clicado. (Lógica a implementar)");
        Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
        alertInfo.setTitle("Em Breve");
        alertInfo.setHeaderText(null);
        alertInfo.setContentText("A funcionalidade de impressão será implementada futuramente.");
        alertInfo.showAndWait();
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