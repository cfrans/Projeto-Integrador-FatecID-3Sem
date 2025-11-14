package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import util.ControladorNavegavel;
import util.ControleAcesso;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import database.ConexaoDB;
import util.SessaoUsuario;

public class AndamentoPAIController implements Initializable, ControladorNavegavel {

    // Variável para guardar o controlador principal
    private MenuController menuController;

    // Método obrigatório da interface
    @Override
    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }

    @FXML private Button btAbrirDescricao;
    @FXML private Button btCriarFinalizacao;
    @FXML private Button btVoltar;
    @FXML private TableView<PAIAndamento> tabelaPAIs;
    @FXML private TableColumn<PAIAndamento, String> colAluno;
    @FXML private TableColumn<PAIAndamento, String> colResponsavel;
    @FXML private TableColumn<PAIAndamento, LocalDate> colRevisao;
    @FXML private TableColumn<PAIAndamento, String> colStatus;
    @FXML private TableColumn<PAIAndamento, String> colTitulo;

    private ObservableList<PAIAndamento> listaPAIs = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAluno.setCellValueFactory(new PropertyValueFactory<>("nomeAluno"));
        colResponsavel.setCellValueFactory(new PropertyValueFactory<>("nomeResponsavel"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colRevisao.setCellValueFactory(new PropertyValueFactory<>("prazoRevisao"));
        tabelaPAIs.setItems(listaPAIs);
        carregarPAIs();

        tabelaPAIs.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        btAbrirDescricao.setDisable(false);
                        btCriarFinalizacao.setDisable(false);
                    } else {
                        btAbrirDescricao.setDisable(true);
                        btCriarFinalizacao.setDisable(true);
                    }
                }
        );
    }

    private void carregarPAIs() {
        listaPAIs.clear();
        String sql = "SELECT p.id_pai, p.titulo, p.status, p.prazo_revisao, " +
                "a.nome AS nome_aluno, u.nome AS nome_responsavel " +
                "FROM pai p " +
                "JOIN aluno a ON p.id_aluno = a.id_aluno " +
                "JOIN usuario u ON p.id_usuario = u.id_usuario " +
                "WHERE p.status = 'Em Andamento' " +
                "ORDER BY p.prazo_revisao";

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                listaPAIs.add(new PAIAndamento(
                        rs.getInt("id_pai"),
                        rs.getString("titulo"),
                        rs.getString("nome_aluno"),
                        rs.getString("nome_responsavel"),
                        rs.getString("status"),
                        rs.getDate("prazo_revisao").toLocalDate()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void onClickAbrirDescricao(ActionEvent event) throws IOException {
        PAIAndamento paiSelecionado = tabelaPAIs.getSelectionModel().getSelectedItem();
        if (paiSelecionado == null) return;

        SessaoUsuario.setIdPaiSelecionado(paiSelecionado.getIdPai());

        if (menuController != null) {
            menuController.navegarPara("/view/PAI.fxml");
        } else {
            System.err.println("Erro: MenuController não foi injetado!");
        }
    }

    @FXML
    void onClickCriarFinalizacao(ActionEvent event) throws IOException {
        PAIAndamento paiSelecionado = tabelaPAIs.getSelectionModel().getSelectedItem();
        if (paiSelecionado == null) return;
        if (!ControleAcesso.verificarPermissao("T.I.","Professor","Profissional Especializado")) return;

        SessaoUsuario.setIdPaiSelecionado(paiSelecionado.getIdPai());

        if (menuController != null) {
            menuController.navegarPara("/view/FinalizacaoPAI.fxml");
        } else {
            System.err.println("Erro: MenuController não foi injetado!");
        }
    }


    @FXML
    void onClickVoltar(ActionEvent event) {
        if (menuController != null) {
            menuController.navegarPara("/view/Home.fxml");
        } else {
            System.err.println("Erro: MenuController não foi injetado!");
        }
    }


    // --- CLASSE INTERNA (HELPER) ---
    public static class PAIAndamento {
        private int idPai;
        private String titulo;
        private String nomeAluno;
        private String nomeResponsavel;
        private String status;
        private LocalDate prazoRevisao;

        public PAIAndamento(int idPai, String titulo, String nomeAluno, String nomeResponsavel, String status, LocalDate prazoRevisao) {
            this.idPai = idPai;
            this.titulo = titulo;
            this.nomeAluno = nomeAluno;
            this.nomeResponsavel = nomeResponsavel;
            this.status = status;
            this.prazoRevisao = prazoRevisao;
        }

        // --- Getters
        public int getIdPai() { return idPai; }
        public String getTitulo() { return titulo; }
        public String getNomeAluno() { return nomeAluno; }
        public String getNomeResponsavel() { return nomeResponsavel; }
        public String getStatus() { return status; }
        public LocalDate getPrazoRevisao() { return prazoRevisao; }
    }
}