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
import util.SessaoUsuario; // Usado para guardar dados entre telas

public class AndamentoPAIController implements Initializable {

    @FXML private Button btAbrirDescricao;
    @FXML private Button btCriarFinalizacao;
    @FXML private Button btVoltar;
    @FXML private TableView<PAIAndamento> tabelaPAIs;
    @FXML private TableColumn<PAIAndamento, String> colAluno;
    @FXML private TableColumn<PAIAndamento, String> colResponsavel;
    @FXML private TableColumn<PAIAndamento, LocalDate> colRevisao;
    @FXML private TableColumn<PAIAndamento, String> colStatus;
    @FXML private TableColumn<PAIAndamento, String> colTitulo;

    // Lista para popular a tabela
    private ObservableList<PAIAndamento> listaPAIs = FXCollections.observableArrayList();

    /**
     * Roda quando a tela é carregada
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Configura as colunas da tabela para ler os dados da classe PAIAndamento
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAluno.setCellValueFactory(new PropertyValueFactory<>("nomeAluno"));
        colResponsavel.setCellValueFactory(new PropertyValueFactory<>("nomeResponsavel"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colRevisao.setCellValueFactory(new PropertyValueFactory<>("prazoRevisao"));

        // 2. Vincula a lista de dados à tabela
        tabelaPAIs.setItems(listaPAIs);

        // 3. Carrega os dados do banco
        carregarPAIs();

        // 4. Adiciona um "listener" para habilitar/desabilitar botões
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

    /**
     * Busca os PAIs no banco e popula a tabela
     */
    private void carregarPAIs() {
        listaPAIs.clear();

        // SQL com JOIN para buscar nomes do Aluno e do Usuário (Responsável)
        String sql = "SELECT p.id_pai, p.titulo, p.status, p.prazo_revisao, " +
                "a.nome AS nome_aluno, u.nome AS nome_responsavel " +
                "FROM pai p " +
                "JOIN aluno a ON p.id_aluno = a.id_aluno " +
                "JOIN usuario u ON p.id_usuario = u.id_usuario " +
                "WHERE p.status = 'Em Andamento' " + // Filtra por PAIs "Em Andamento"
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
            System.err.println("Erro ao carregar PAIs em andamento:");
            e.printStackTrace();
        }
    }


    @FXML
    void onClickAbrirDescricao(ActionEvent event) throws IOException {
        // Pega o PAI selecionado na tabela
        PAIAndamento paiSelecionado = tabelaPAIs.getSelectionModel().getSelectedItem();
        if (paiSelecionado == null) return; // Segurança

        // Guarda o ID do PAI selecionado na "Sessão" para a próxima tela saber
        SessaoUsuario.setIdPaiSelecionado(paiSelecionado.getIdPai());

        // Carrega a tela PAI.fxml (que deve ser atualizada para carregar dados)
        // TODO: A tela PAI.fxml é de *criação*. O ideal seria ter uma tela
        // PAI_Detalhes.fxml (mas por enquanto, reutilizamos a PAI.fxml)
        Parent root = FXMLLoader.load(getClass().getResource("/view/PAI.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    void onClickCriarFinalizacao(ActionEvent event) throws IOException {
        // Pega o PAI selecionado na tabela
        PAIAndamento paiSelecionado = tabelaPAIs.getSelectionModel().getSelectedItem();
        if (paiSelecionado == null) return; // Segurança

        // Verifica permissão
        if (!ControleAcesso.verificarPermissao("T.I.","Professor","Profissional Especializado")) return;

        // Guarda o ID do PAI selecionado na "Sessão" para a próxima tela saber
        SessaoUsuario.setIdPaiSelecionado(paiSelecionado.getIdPai());

        // Abre a tela de finalização
        Parent root = FXMLLoader.load(getClass().getResource("/view/FinalizacaoPAI.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    void onClickVoltar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Menu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // --- CLASSE INTERNA (HELPER)
    // Classe simples para representar os dados de uma linha da tabela

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