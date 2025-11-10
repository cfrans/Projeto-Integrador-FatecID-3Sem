package util;


import javafx.scene.control.Alert;
import util.SessaoUsuario;

public class ControleAcesso {

    public static boolean verificarPermissao(String... funcoesPermitidas) {
        String funcaoAtual = SessaoUsuario.getFuncaoUsuario();

        System.out.println("Função atual do usuário: " + funcaoAtual);
        System.out.println("Funções permitidas: " + java.util.Arrays.toString(funcoesPermitidas));


        if (funcaoAtual == null) {
            exibirAlerta("Acesso negado", "Nenhum usuário logado.");
            return false;
        }

        for (String f : funcoesPermitidas) {
            if (f.equalsIgnoreCase(funcaoAtual)) {
                return true;
            }
        }

        exibirAlerta("Acesso negado", "Você não tem permissão para acessar esta tela.");
        return false;
    }

    private static void exibirAlerta(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}

