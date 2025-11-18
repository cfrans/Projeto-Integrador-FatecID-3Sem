package controllers;

import database.ConexaoDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import util.SessaoUsuario;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DetalhesIntervencaoController extends BaseController implements Initializable {

    // Campos do FXML
    @FXML private TextField tfNome;
    @FXML private TextField tfSerieTurma;
    @FXML private TextField tfRA;
    @FXML private TextField tfTituloIntervencao;
    @FXML private DatePicker dpDataIntervencao;
    @FXML private TextField tfResponsavelIntervencao;
    @FXML private TextArea taObservacoes;
    @FXML private Button btVoltar;

    /**
     * Inicializa o controller, pegando o ID da sessão e carregando os dados.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int idIntervencaoAtual = SessaoUsuario.getIdIntervencaoSelecionada();

        if (idIntervencaoAtual > 0) {
            carregarDadosIntervencao(idIntervencaoAtual);
        } else {
            exibirAlertaErro("Erro", "ID da Intervenção não encontrado.");
        }
    }

    /**
     * Carrega os dados da intervenção do banco de dados.
     */
    private void carregarDadosIntervencao(int id) {
        String sql = "SELECT " +
                "    i.titulo, i.observacao, i.data, " +
                "    a.nome AS nome_aluno, " +
                "    a.ra, " +
                "    st.nome AS nome_serie_turma, " +
                "    u.nome AS nome_usuario " +
                "FROM intervencao i " +
                "JOIN aluno a ON i.id_aluno = a.id_aluno " +
                "JOIN usuario u ON i.id_usuario = u.id_usuario " +
                "JOIN serie_turma st ON a.id_serie_turma = st.id_serie_turma " +
                "WHERE i.id_intervencao = ?";

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Preenche os campos da tela
                tfNome.setText(rs.getString("nome_aluno"));
                tfSerieTurma.setText(rs.getString("nome_serie_turma"));
                tfRA.setText(rs.getString("ra"));
                tfTituloIntervencao.setText(rs.getString("titulo"));
                dpDataIntervencao.setValue(rs.getDate("data").toLocalDate());
                tfResponsavelIntervencao.setText(rs.getString("nome_usuario"));
                taObservacoes.setText(rs.getString("observacao"));

            } else {
                exibirAlertaErro("Erro de Banco de Dados", "Intervenção não encontrada.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            exibirAlertaErro("Erro de SQL", "Falha ao carregar dados: " + e.getMessage());
        }
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

    /**
     * SOBRESCRITO: Manipula o clique no botão "Voltar".
     * Navega de volta para a tela de Visualização, em vez da Home.
     */
    @FXML
    @Override
    void onClickVoltar(ActionEvent event) {
        System.out.println("DetalhesIntervencaoController: Voltando para Visualização.");
        navegarPara("/view/VisualizacaoIntervencao.fxml");
    }
}