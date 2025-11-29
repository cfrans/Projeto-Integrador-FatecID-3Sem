package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import util.ControleAcesso;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

import database.ConexaoDB;
import controllers.NavegadorUtil;
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
    @FXML private CheckBox cbVisualizarFinalizados;

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
        carregarPAIs(); // Carrega o estado inicial (só "Em Andamento")

        // Define como a data é exibida
        colRevisao.setCellFactory(column -> new TableCell<AndamentoPAIController.PAIAndamento, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Usa o formatador do BaseController
                    setText(item.format(DATA_FORMATTER));
                }
            }
        });

        // Listener
        tabelaPAIs.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    boolean itemSelecionado = (newSelection != null);

                    // Botão "Ver Detalhes" só precisa que um item esteja selecionado
                    btAbrirDescricao.setDisable(!itemSelecionado);

                    // Botão "Finalizar PAI"
                    if (itemSelecionado) {
                        // Se o PAI selecionado já está 'Finalizado', desabilita o botão
                        if ("Finalizado".equals(newSelection.getStatus())) {
                            btCriarFinalizacao.setDisable(true);
                        } else {
                            // Se está 'Em Andamento', habilita
                            btCriarFinalizacao.setDisable(false);
                        }
                    } else {
                        // Se nada está selecionado, desabilita
                        btCriarFinalizacao.setDisable(true);
                    }
                }
        );
    }

    /**
     * Apresenta em tela as informações cadastradas na tabela PAI do banco de dados.
     * A consulta SQL é dinâmica e depende do estado do CheckBox 'cbVisualizarFinalizados'.
     */
    private void carregarPAIs() {
        listaPAIs.clear();

        // 1. Define a base da consulta
        String sqlBase = "SELECT p.id_pai, p.titulo, p.status, p.prazo_revisao, " +
                "a.nome AS nome_aluno, u.nome AS nome_responsavel " +
                "FROM pai p " +
                "JOIN aluno a ON p.id_aluno = a.id_aluno " +
                "JOIN usuario u ON p.id_usuario = u.id_usuario ";

        // 2. Define o filtro (WHERE) com base no CheckBox
        String sqlFiltro;
        if (cbVisualizarFinalizados != null && cbVisualizarFinalizados.isSelected()) {
            // Se marcado, busca "Em Andamento" E "Finalizado"
            sqlFiltro = "WHERE p.status IN ('Em Andamento', 'Finalizado') ";
        } else {
            // Se desmarcado (default), busca SÓ "Em Andamento"
            sqlFiltro = "WHERE p.status = 'Em Andamento' ";
        }

        // 3. Define a ordenação
        String sqlOrderBy = "ORDER BY p.status, p.prazo_revisao"; // Ordena por status primeiro

        // 4. Junta tudo
        String sql = sqlBase + sqlFiltro + sqlOrderBy;

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
     * Este método é chamado toda vez que o CheckBox é clicado.
     * Ele simplesmente manda recarregar a lista de PAIs com o novo filtro.
     *
     * @param event O evento de clique no CheckBox
     */
    @FXML
    void onCheckFinalizados(ActionEvent event) {
        carregarPAIs();
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

        navegarPara("/view/DetalhesPAI.fxml");
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
     */
    @FXML
    void onClickCriarFinalizacao(ActionEvent event) {
        PAIAndamento paiSelecionado = tabelaPAIs.getSelectionModel().getSelectedItem();
        if (paiSelecionado == null) return;

        // Verificação de permissão
        if (!ControleAcesso.verificarPermissao("T.I.","Professor","Profissional Especializado")) {
            Alert alertPermissao = new Alert(Alert.AlertType.WARNING);
            alertPermissao.setTitle("Acesso Negado");
            alertPermissao.setHeaderText("Você não tem permissão para esta ação.");
            alertPermissao.setContentText("Apenas T.I., Professores e Profissionais Especializados podem finalizar um PAI.");

            // --- CENTRALIZAÇÃO MANUAL ---
            if (menuController != null && menuController.getStage() != null) {
                alertPermissao.initOwner(menuController.getStage());
            }

            alertPermissao.showAndWait();
            return;
        }

        // 1. Criar o Alerta de Confirmação
        Alert alertConfirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        alertConfirmacao.setTitle("Confirmar Finalização");
        alertConfirmacao.setHeaderText("Finalizar PAI");
        alertConfirmacao.setContentText("Tem certeza que deseja finalizar o PAI de título '" + paiSelecionado.getTitulo() + "'?");

        // --- CENTRALIZAÇÃO MANUAL ---
        if (menuController != null && menuController.getStage() != null) {
            alertConfirmacao.initOwner(menuController.getStage());
        }

        Optional<ButtonType> resultado = alertConfirmacao.showAndWait();

        // 3. Verificar se o usuário clicou em "OK"
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // 4. Chamar o método para atualizar o banco
            boolean sucesso = finalizarPAINoBanco(paiSelecionado.getIdPai());

            if (sucesso) {
                NavegadorUtil.exibirSucessoAlerta(
                        "PAI Finalizado",
                        "O PAI foi alterado para 'Finalizado' com sucesso.",
                        menuController.getStage()
                );

                // 6. Recarregar a tabela (o item finalizado desaparecerá)
                carregarPAIs();
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

            return linhasAfetadas > 0; // Retorna true se pelo menos 1 linha foi afetada

        } catch (SQLException e) {
            e.printStackTrace();
            // Exibir alerta de erro
            Alert alertErro = new Alert(Alert.AlertType.ERROR);
            alertErro.setTitle("Erro de Banco de Dados");
            alertErro.setHeaderText("Não foi possível finalizar o PAI.");
            alertErro.setContentText("Ocorreu um erro ao tentar atualizar o banco de dados: " + e.getMessage());

            // --- CENTRALIZAÇÃO MANUAL ---
            if (menuController != null && menuController.getStage() != null) {
                alertErro.initOwner(menuController.getStage());
            }

            alertErro.showAndWait();
            return false;
        }
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