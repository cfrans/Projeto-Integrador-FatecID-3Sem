package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import util.ControladorNavegavel; // Use o caminho da sua interface

import java.time.LocalDate;

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

    /**
     * Helper para exibir um Alerta de Erro padronizado.
     * @param titulo O titulo do alerta de erro
     * @param cabecalho O cabeçalho do alerta de erro
     * @param conteudo O conteúdo do alerta de erro
     */
    void exibirAlertaErro(String titulo, String cabecalho, String conteudo) {
        Alert alertErro = new Alert(Alert.AlertType.ERROR);
        alertErro.setTitle(titulo);
        alertErro.setHeaderText(cabecalho);
        alertErro.setContentText(conteudo);
        alertErro.showAndWait();
    }

    /**
     * Enum para definir qual direção de tempo bloquear no método desabilitarDatas.
     */
    protected enum TipoBloqueio {
        FUTURAS, // Bloqueia do amanhã para frente
        PASSADAS // Bloqueia do ontem para trás
    }

    /**
     * Método genérico para desabilitar dias no calendário.
     * @param datePicker O componente a ser configurado.
     * @param tipo O tipo de bloqueio (FUTURAS ou PASSADAS).
     */
    protected void desabilitarDatas(DatePicker datePicker, TipoBloqueio tipo) {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                // Se a data for nula ou vazia, não faz nada
                if (date == null || empty) {
                    return;
                }

                boolean deveBloquear = false;
                LocalDate hoje = LocalDate.now();

                // Lógica de decisão
                if (tipo == TipoBloqueio.FUTURAS) {
                    if (date.isAfter(hoje)) {
                        deveBloquear = true;
                    }
                } else if (tipo == TipoBloqueio.PASSADAS) {
                    if (date.isBefore(hoje)) {
                        deveBloquear = true;
                    }
                }

                // Aplica o estilo se necessário
                if (deveBloquear) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // Vermelho claro
                }
            }
        });
    }
}