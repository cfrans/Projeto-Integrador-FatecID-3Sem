package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.ControladorNavegavel;
import util.ControleAcesso;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.Initializable;
import util.SessaoUsuario;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML
    private AnchorPane anchorRoot;
    @FXML
    private MenuItem menuNovoCadastro;
    @FXML
    private MenuItem menuPlano;
    @FXML
    private MenuItem menuCadastrarUsuario;
    @FXML
    private MenuItem menuHistorico;
    @FXML
    private MenuItem menuVisualizacaoIntervencao;
    @FXML
    private MenuItem menuRendimento;
    @FXML
    private MenuItem menuAcompanhamento;
    @FXML
    private MenuItem menuSair;
    @FXML
    private MenuItem menuTrocarUsuario;
    @FXML
    private MenuItem menuProjeto;

    /**
     * Inicializa o controlador logo após o carregamento do arquivo FXML
     *
     * Assim que a tela é exibida, ele define a página Home como a tela inicial
     * da área de navegação
     *
     * @param location   localização usada para resolver caminhos relativos do FXML, geralmente não utilizada diretamente.
     * @param resources  recursos de internacionalização associados ao FXML, também opcional neste contexto.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Verifica se é o admin padrão
        if (SessaoUsuario.isModoAdminPadrao()) {
            iniciarFluxoTrocaAdmin();
        } else {
            // Fluxo normal
            navegarPara("/view/Home.fxml");
        }
    }

    private void iniciarFluxoTrocaAdmin() {
        // 1. Exibe o Alerta
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atenção - Configuração Inicial");
        alert.setHeaderText("Usuário Administrativo Padrão Detectado");
        alert.setContentText("Você será redirecionado para cadastrar seu usuário.\n" +
                "Ao concluir, o usuário 'admin' será excluído.");
        alert.showAndWait();

        // 2. Navega direto para o cadastro
        try {
            abrirCadastrarUsuario();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carrega a tela Cadastro Usuário
     * @throws IOException
     */
    @FXML
    void abrirCadastrarUsuario() throws IOException {
        if (!ControleAcesso.verificarPermissao("T.I.", "Coordenador")) return;
        mudarTela("/view/CadastroUsuario.fxml");
    }

    /**
     * Carrega a tela Cadastro (para cadastro de alunos)
     * @throws IOException
     */
    @FXML
    void abrirNovoCadastro() throws IOException {
        if (!ControleAcesso.verificarPermissao("T.I.","Coordenador")) return;
        mudarTela("/view/Cadastro.fxml");
    }

    /**
     * Carrega a tela PAI (utilizada para cadastro de planos de acompanhamento dos alunos cadastrados no sistema)
     * @throws IOException
     */
    @FXML
    void abrirPlano() throws IOException {
        if (!ControleAcesso.verificarPermissao("T.I.","Professor","Profissional Especializado")) return;
        mudarTela("/view/PAI.fxml");
    }

    /**
     * Carrega a tela Intervenções
     * @throws IOException
     */
    @FXML
    void abrirHistorico() throws IOException {
        if (!ControleAcesso.verificarPermissao("T.I.","Professor","Coordenador","Profissional Especializado")) return;
        mudarTela("/view/Intervencoes.fxml");
    }

    /**
     * Carrega a tela Visualização de Intervencões
     * @throws IOException
     */
    @FXML
    void abrirVisualizacaoIntervencao() throws IOException {
        if (!ControleAcesso.verificarPermissao("T.I.","Professor","Coordenador","Profissional Especializado")) return;
        mudarTela("/view/VisualizacaoIntervencao.fxml");
    }

    /**
     * Carrega a tela Rendimento
     * @throws IOException
     */
    @FXML
    void abrirRendimento() throws IOException {
        if (!ControleAcesso.verificarPermissao("T.I.","Professor")) return;
        mudarTela("/view/Rendimento.fxml");
    }

    /**
     * Carrega a tela Andamento PAI
     * @throws IOException
     */
    @FXML
    void abrirAndamento() throws IOException {
        if (!ControleAcesso.verificarPermissao("T.I.","Professor","Profissional Especializado")) return;
        mudarTela("/view/AndamentoPAI.fxml");
    }

    /**
     * Carrega a tela Consulta de Rendimento
     * @throws IOException
     */
    @FXML
    void abrirConsultaRendimento() throws IOException {
        if (!ControleAcesso.verificarPermissao("T.I.","Professor","Profissional Especializado", "Coordenador")) return;
        mudarTela("/view/ConsultaRendimento.fxml");
    }

    /**
     * Leva o usuário de colta para o Login para possibilidade de troca de usuário
     * @throws IOException
     */
    @FXML
    void trocarUsuario() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
        Stage stage = (Stage) anchorRoot.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Realiza a ação de fechamento completo da aplicação
     * @throws IOException
     */
    @FXML
    void sairSistema() throws IOException {
        NavegadorUtil.fecharAplicacao();
    }

    /**
     * Navega até a tela de Sobre do Projeto.
     */
    @FXML
    void sobreProjeto() throws IOException {
        mudarTela("/view/Sobre.fxml");
    }

    /**
     * Altera dinamicamente o conteúdo exibido dentro do AnchorPane principal da tela.
     *
     * Este método é responsável por carregar um arquivo FXML, criar seu controlador,
     * substituir completamente o conteúdo atual do container e ajustar
     * corretamente suas âncoras para que o novo layout ocupe toda a área disponível
     *
     * Além disso, se o controlador carregado implementar a interface, o método injeta a instância atual do menu,
     * permitindo que a nova tela possa solicitar navegação de volta.
     *
     * @param caminhoFXML caminho relativo para o arquivo FXML que deve ser carregado.
     * @throws IOException
     */
    private void mudarTela(String caminhoFXML) throws IOException {
        try {
            URL fxmlUrl = getClass().getResource(caminhoFXML);
            if (fxmlUrl == null) {
                System.err.println("Erro: Não foi possível encontrar o FXML: " + caminhoFXML);
                return;
            }

            // 1. Cria um FXMLLoader em vez de carregar direto
            FXMLLoader loader = new FXMLLoader(fxmlUrl);

            // 2. Carrega o AnchorPane
            AnchorPane paginaCarregada = loader.load();

            // 3. LIMPA E ADICIONA A PÁGINA
            anchorRoot.getChildren().clear();
            anchorRoot.getChildren().setAll(paginaCarregada);
            AnchorPane.setTopAnchor(paginaCarregada, 0.0);
            AnchorPane.setBottomAnchor(paginaCarregada, 0.0);
            AnchorPane.setLeftAnchor(paginaCarregada, 0.0);
            AnchorPane.setRightAnchor(paginaCarregada, 0.0);

            // 4. ENTREGAR A SI MESMO PARA O NOVO CONTROLADOR
            Object controller = loader.getController();
            if (controller instanceof ControladorNavegavel) {
                ((ControladorNavegavel) controller).setMenuController(this);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Realiza a navegação para uma nova tela dentro do container principal
     *
     * Este método é utilizado pelos controladores das telas internas para solicitar
     * ao MenuController que troque o conteúdo exibido no AnchorPane principal
     *
     * @param caminhoFXML caminho relativo do arquivo FXML que deve ser carregado
     */
    public void navegarPara(String caminhoFXML) {
        try {
            mudarTela(caminhoFXML);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: adicionar talvez um Alert
        }
    }

    /**
     * Retorna o Stage (janela) atual da aplicação.
     * Útil para definir o 'owner' de modais e alertas.
     */
    public Stage getStage() {
        if (anchorRoot != null && anchorRoot.getScene() != null) {
            return (Stage) anchorRoot.getScene().getWindow();
        }
        return null;
    }
}