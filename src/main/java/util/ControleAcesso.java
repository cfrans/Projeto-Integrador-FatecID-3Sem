package util;

import javafx.scene.control.Alert;
import util.SessaoUsuario;

/**
 * Classe utilitária para gerenciar o controle de acesso e permissões no sistema.
 *
 * Esta classe fornece métodos estáticos para verificar se o usuário
 * logado (obtido de {@link SessaoUsuario}) possui a permissão necessária
 * para acessar uma determinada funcionalidade ou tela.
 */
public class ControleAcesso {

    /**
     * Verifica se o usuário logado atualmente possui uma das funções (cargos)
     * necessárias para acessar um recurso.
     *
     * O método compara a função do usuário (obtida de {@link SessaoUsuario#getFuncaoUsuario()})
     * com a lista de funções permitidas. A verificação não diferencia maiúsculas/minúsculas.
     *
     * Se o usuário não estiver logado ou não tiver a permissão, um alerta de erro
     * será exibido automaticamente.
     *
     * @param funcoesPermitidas Uma lista (varargs) de nomes de funções (ex: "Administrador", "Professor")
     * que têm permissão para acessar o recurso.
     * @return {@code true} se o usuário atual possui uma das funções permitidas,
     * {@code false} caso contrário (ou se ninguém estiver logado).
     */
    public static boolean verificarPermissao(String... funcoesPermitidas) {
        // Obtém a função do usuário logado na sessão
        String funcaoAtual = SessaoUsuario.getFuncaoUsuario();

        System.out.println("Função atual do usuário: " + funcaoAtual);
        System.out.println("Funções permitidas: " + java.util.Arrays.toString(funcoesPermitidas));

        // Verifica se há alguém logado
        if (funcaoAtual == null) {
            exibirAlerta("Acesso negado", "Nenhum usuário logado.");
            return false;
        }

        // Compara a função atual com a lista de permissões
        for (String f : funcoesPermitidas) {
            if (f.equalsIgnoreCase(funcaoAtual)) {
                return true; // Encontrou! O usuário tem permissão.
            }
        }

        // Se o loop terminar, o usuário não tem a permissão
        exibirAlerta("Acesso negado", "Você não tem permissão para acessar esta tela.");
        return false;
    }

    /**
     * Exibe um pop-up de Alerta do tipo ERRO.
     * Método auxiliar privado para padronizar as mensagens de acesso negado.
     *
     * @param titulo   O texto a ser exibido na barra de título da janela do alerta.
     * @param mensagem O texto principal de conteúdo a ser exibido no alerta.
     */
    private static void exibirAlerta(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null); // Não usar cabeçalho
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}