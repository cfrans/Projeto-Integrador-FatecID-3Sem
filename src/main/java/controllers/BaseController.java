package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.ControladorNavegavel;
import java.time.format.DateTimeFormatter;

import java.time.LocalDate;

public abstract class BaseController implements ControladorNavegavel {


    protected static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
     * Helper para exibir um Alerta de Erro padronizado e centralizado.
     * @param titulo O titulo do alerta de erro
     * @param cabecalho O cabeçalho do alerta de erro
     * @param conteudo O conteúdo do alerta de erro
     */
    void exibirAlertaErro(String titulo, String cabecalho, String conteudo) {
        Alert alertErro = new Alert(Alert.AlertType.ERROR);
        alertErro.setTitle(titulo);
        alertErro.setHeaderText(cabecalho);
        alertErro.setContentText(conteudo);

        // Tenta pegar o Stage (janela) através do MenuController para centralizar o alerta
        if (menuController != null) {
            Stage stage = menuController.getStage();
            if (stage != null) {
                alertErro.initOwner(stage);
            }
        }

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

    /**
     * Configura um TextField para NÃO aceitar números.
     * @param textField O campo a ser monitorado.
     */
    protected void campoSomenteTexto(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Se o novo valor tiver algum número (dígito 0-9)
            if (newValue != null && newValue.matches(".*\\d.*")) {
                textField.setText(oldValue);
            }
        });
    }

    /**
     * Configura um TextField para aceitar APENAS números.
     * @param textField O campo a ser monitorado.
     */
    protected void campoSomenteNumeros(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Se o novo valor tiver qualquer coisa que NÃO seja número
            if (newValue != null && !newValue.matches("\\d*")) {
                textField.setText(oldValue);
            }
        });
    }

    /**
     * Limita o tamanho máximo de caracteres de um campo.
     * @param textField O campo.
     * @param tamanhoMaximo Quantidade máxima de caracteres.
     */
    protected void limitarTamanhoCampo(TextField textField, int tamanhoMaximo) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > tamanhoMaximo) {
                textField.setText(oldValue);
            }
        });
    }

    protected boolean validacaoEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }

}