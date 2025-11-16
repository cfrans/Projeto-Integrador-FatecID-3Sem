package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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

public class AndamentoPAIController extends BaseController implements Initializable {

    @FXML private Button btAbrirDescricao;
    @FXML private Button btCriarFinalizacao;
    @FXML private Button btVoltar;
    @FXML private TableView<PAIAndamento> tabelaPAIs;
    @FXML private TableColumn<PAIAndamento, String> colAluno;
    @FXML private TableColumn<PAIAndamento, String> colResponsavel;
    @FXML private TableColumn<PAIAndamento, LocalDate> colRevisao;
    @FXML private TableColumn<PAIAndamento, String> colStatus;
    @FXML private TableColumn<PAIAndamento, String> colTitulo;

    private final ObservableList<PAIAndamento> listaPAIs = FXCollections.observableArrayList();

    /**
     * Inicializa os componentes da interface após o carregamento do FXML
     * Método que configura as colunas da tabela associando cada uma às
     * propriedades correspondentes, carrega a lista inicial
     * de PAIs, define os itens da tabela e adiciona um listener para habilitar
     * ou desabilitar botões com base na seleção atual do usuário
     *
     *
     * @param location   localização do arquivo FXML utilizado para inicializar a interface.
     * @param resources  recursos específicos da localidade, caso existam
     */
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

    /**
     * Apresenta em tela as informações cadastradas na tabela PAI do banco de dados
     */
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

    /**
     * Metodo que ao clicar no botão de ver detalhes na tela AndamentoPAI ela levará para a tela PAI,
     * onde deverá mostrar os detalhes cadastrados
     * @param event
     * @throws IOException
     */
    @FXML
    void onClickAbrirDescricao(ActionEvent event) throws IOException {
        PAIAndamento paiSelecionado = tabelaPAIs.getSelectionModel().getSelectedItem();
        if (paiSelecionado == null) return;

        SessaoUsuario.setIdPaiSelecionado(paiSelecionado.getIdPai());
        navegarPara("/view/PAI.fxml");
    }

    /**
     * Manipula o evento de clique no botão de criação de finalização de um PAI.
     *
     * Este método verifica se há um PAI selecionado na tabela e se o usuário
     * possui permissão para criar uma finalização. Caso as validações sejam
     * atendidas, o ID do PAI selecionado é armazenado na sessão e a interface
     * é redirecionada para a tela de finalização do PAI.
     *
     * @param event o evento de ação gerado pelo clique no botão.
     * @throws IOException caso ocorra algum erro ao carregar a nova interface FXML.
     */
    @FXML
    void onClickCriarFinalizacao(ActionEvent event) throws IOException {
        PAIAndamento paiSelecionado = tabelaPAIs.getSelectionModel().getSelectedItem();
        if (paiSelecionado == null) return;
        if (!ControleAcesso.verificarPermissao("T.I.","Professor","Profissional Especializado")) return;

        SessaoUsuario.setIdPaiSelecionado(paiSelecionado.getIdPai());

        navegarPara("/view/FinalizacaoPAI.fxml");
    }

    // --- CLASSE INTERNA (HELPER) ---
    public static class PAIAndamento {
        private final int idPai;
        private final String titulo;
        private final String nomeAluno;
        private final String nomeResponsavel;
        private final String status;
        private final LocalDate prazoRevisao;

        /**
         * Cria uma nova instância de {@code PAIAndamento} com os dados fornecidos
         * Construtor que inicializa todas as informações do PAI em andamento,
         * incluindo identificador, título, aluno associado, responsável, status
         * atual e prazo de revisão
         *
         * @param idPai          identificador único do PAI.
         * @param titulo         título do PAI.
         * @param nomeAluno      nome do aluno vinculado ao PAI.
         * @param nomeResponsavel nome do responsável pelo acompanhamento.
         * @param status         status atual do PAI.
         * @param prazoRevisao   data limite para a revisão do PAI.
         */
        public PAIAndamento(int idPai, String titulo, String nomeAluno, String nomeResponsavel, String status, LocalDate prazoRevisao) {
            this.idPai = idPai;
            this.titulo = titulo;
            this.nomeAluno = nomeAluno;
            this.nomeResponsavel = nomeResponsavel;
            this.status = status;
            this.prazoRevisao = prazoRevisao;
        }

        /**
         * Retorna o identificador único do PAI
         * @return o ID do PAI
         */
        public int getIdPai() { return idPai; }

        /**
         * Retorna o título do PAI
         * @return o título do PAI
         */
        public String getTitulo() { return titulo; }

        /**
         * Retorna o nome do aluno associado ao PAI
         * @return o nome do aluno
         */
        public String getNomeAluno() { return nomeAluno; }

        /**
         * Retorna o nome do responsável pelo acompanhamento do PAI
         * @return o nome do responsável
         */
        public String getNomeResponsavel() { return nomeResponsavel; }

        /**
         * Retorna o status atual do PAI
         * @return o status do PAI
         */
        public String getStatus() { return status; }

        /**
         * Retorna a data limite para revisão do PAI
         * @return o prazo de revisão
         */
        public LocalDate getPrazoRevisao() { return prazoRevisao; }
    }
}