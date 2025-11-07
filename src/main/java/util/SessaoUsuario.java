package util;

public class SessaoUsuario {

    private static int idUsuario;
    private static String nomeUsuario;

    /**
     * Define o usuário que acabou de logar.
     * @param id O id_usuario do banco.
     * @param nome O nome do usuário.
     */
    public static void setUsuarioLogado(int id, String nome) {
        idUsuario = id;
        nomeUsuario = nome;
    }

    /**
     * Pega o ID do usuário logado.
     * @return O id_usuario.
     */
    public static int getIdUsuario() {
        return idUsuario;
    }

    /**
     * Pega o Nome do usuário logado.
     * @return O nome do usuário.
     */
    public static String getNomeUsuario() {
        return nomeUsuario;
    }

    /**
     * Limpa a sessão (para fazer logout).
     */
    public static void limparSessao() {
        idUsuario = 0;
        nomeUsuario = null;
    }
}
