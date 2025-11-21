package controllers;

import database.ConexaoDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import util.SessaoUsuario;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class VisualizacaoIntervencaoController extends BaseController implements Initializable {


    @FXML private TableView<IntervencaoData> tabelaPAIs;
    @FXML private TableColumn<IntervencaoData, String> colTitulo;
    @FXML private TableColumn<IntervencaoData, String> colAluno;        // Responsável
    @FXML private TableColumn<IntervencaoData, String> colResponsavel;  // Observação
    @FXML private TableColumn<IntervencaoData, LocalDate> colRevisao;   // Data
    @FXML private TextField tfNome;
    @FXML private TextField tfRA;
    @FXML private Button btDetalhes;
    @FXML private Button btPesquisarTodos;

    private final ObservableList<IntervencaoData> listaIntervencoes = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAluno.setCellValueFactory(new PropertyValueFactory<>("nomeResponsavel")); // responsável
        colResponsavel.setCellValueFactory(new PropertyValueFactory<>("observacao"));
        colRevisao.setCellValueFactory(new PropertyValueFactory<>("data"));

        // Define como a data é exibida
        colRevisao.setCellFactory(column -> new TableCell<IntervencaoData, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty); // Mantém funcionamento padrão

                if (empty || item == null) {
                    setText(null);
                } else {
                    // Usa o formatador do BaseController
                    setText(item.format(DATA_FORMATTER));
                }
            }
        });

        tabelaPAIs.setItems(listaIntervencoes);

        // Desabilitar o botão de detalhes por padrão
        btDetalhes.setDisable(true);

        // Adicionar listener para habilitar o botão
        tabelaPAIs.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    btDetalhes.setDisable(newSelection == null);
                }
        );

        // NÃO carregar nada quando abrir a tela
        listaIntervencoes.clear();
    }

    /**
     * Quando usuário clicar em PESQUISAR
     */
    @FXML
    void perquisarIntervencoes(ActionEvent event) {
        String nome = tfNome.getText().trim();
        String ra = tfRA.getText().trim();
        carregarIntervencoes(nome, ra);
    }

    /**
     * Busca dados no banco usando nome ou RA
     */
    private void carregarIntervencoes(String nome, String ra) {

        listaIntervencoes.clear();

        String sql =
                "SELECT i.id_intervencao, i.titulo, i.observacao, i.data, " +
                        "a.nome AS nome_aluno, a.ra AS ra_aluno, " +
                        "u.nome AS nome_usuario " +
                        "FROM intervencao i " +
                        "JOIN aluno a ON i.id_aluno = a.id_aluno " +
                        "JOIN usuario u ON i.id_usuario = u.id_usuario " +
                        "WHERE (LOWER(a.nome) LIKE LOWER(?) OR ? = '') " +
                        "AND (a.ra LIKE ? OR ? = '') " +
                        "ORDER BY i.data DESC";

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nome + "%");
            stmt.setString(2, nome);
            stmt.setString(3, "%" + ra + "%");
            stmt.setString(4, ra);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                listaIntervencoes.add(new IntervencaoData(
                        rs.getInt("id_intervencao"),
                        rs.getString("titulo"),
                        rs.getString("nome_usuario"),   // responsável
                        rs.getString("observacao"),
                        rs.getDate("data").toLocalDate()
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Quando o usuário clicar em PESQUISAR TODOS.
     * Limpa os campos de filtro e chama o carregarIntervencoes
     * com parâmetros vazios, que buscará todos os registros.
     */
    @FXML
    void onClickPesquisarTodos(ActionEvent event) {
        tfNome.clear();
        tfRA.clear();

        // Chama o método de carregar existente com strings vazias.
        carregarIntervencoes("", "");
    }


    @FXML
    void onClickDetalhes(ActionEvent event) {
        IntervencaoData intervencaoSelecionada = tabelaPAIs.getSelectionModel().getSelectedItem();

        if (intervencaoSelecionada == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Nenhuma Seleção");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecione uma intervenção na tabela para ver os detalhes.");
            alert.showAndWait();
            return;
        }

        // 1. Salvar o ID na sessão
        SessaoUsuario.setIdIntervencaoSelecionada(intervencaoSelecionada.getId());

        // 2. Navegar para a tela de detalhes
        navegarPara("/view/DetalhesIntervencoes.fxml");
    }

    @FXML
    void onClickVoltar(ActionEvent event) {
        navegarParaHome();
    }

    public static class IntervencaoData {

        private final int id;
        private final String titulo;
        private final String nomeResponsavel;
        private final String observacao;
        private final LocalDate data;

        public IntervencaoData(int id, String titulo, String nomeResponsavel,
                               String observacao, LocalDate data) {

            this.id = id;
            this.titulo = titulo;
            this.nomeResponsavel = nomeResponsavel;
            this.observacao = observacao;
            this.data = data;
        }

        public int getId() { return id; }
        public String getTitulo() { return titulo; }
        public String getNomeResponsavel() { return nomeResponsavel; }
        public String getObservacao() { return observacao; }
        public LocalDate getData() { return data; }
    }
}