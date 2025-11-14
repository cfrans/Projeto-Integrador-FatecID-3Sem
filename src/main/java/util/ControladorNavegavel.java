package util;
import controllers.MenuController;

public interface ControladorNavegavel {

    /**
     * Recebe a instância do MenuController principal para permitir a navegação.
     * @param menuController O controlador principal do menu.
     */
    void setMenuController(MenuController menuController);
}
