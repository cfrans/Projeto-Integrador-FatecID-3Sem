package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import util.ControladorNavegavel; // Use o caminho da sua interface

public abstract class BaseController implements ControladorNavegavel {

    protected MenuController menuController;

    /**
     * Método obrigatório da interface.
     */
    @Override
    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }

    /**
     * Helper de navegação. Pede ao MenuController para carregar uma nova tela.
     */
    protected void navegarPara(String caminhoFXML) {
        if (menuController != null) {
            menuController.navegarPara(caminhoFXML);
        } else {
            System.err.println("Erro de Navegação: MenuController é nulo. A tela foi carregada corretamente?");
        }
    }

    /**
     * Helper de navegação para voltar à tela inicial.
     */
    protected void navegarParaHome() {
        navegarPara("/view/Home.fxml");
    }

    /**
     * Manipula o evento do botão "Voltar" de qualquer tela que herda
     * este controlador. Navega de volta para a tela Home.
     * O FXML (onAction="#onClickVoltar") encontrará este método.
     *
     * @param event O evento de clique (não usado, mas necessário pela assinatura)
     */
    @FXML
    void onClickVoltar(ActionEvent event) {
        System.out.println("BaseController: Botão 'Voltar' clicado. Navegando para Home.");
        navegarParaHome();
    }
}