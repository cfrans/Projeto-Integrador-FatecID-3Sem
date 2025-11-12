package util;

public class SessaoUsuario {

    private static int idUsuario;
    private static String nomeUsuario;
    private static String funcaoUsuario;
    private static int idPaiSelecionado;

    /**
     * Define o usuário que acabou de logar.
     *
     * @param id     O id_usuario do banco.
     * @param nome   O nome do usuário.
     * @param funcao O nome da função (ex: "Administrador", "Professor", "T.I.")
     */
    public static void setUsuarioLogado(int id, String nome, String funcao) {
        idUsuario = id;
        nomeUsuario = nome;
        funcaoUsuario = funcao; // ✅ agora a função é salva corretamente
    }

    /** Pega o ID do usuário logado. */
    public static int getIdUsuario() {
        return idUsuario;
    }

    /** Pega o Nome do usuário logado. */
    public static String getNomeUsuario() {
        return nomeUsuario;
    }

    /** Pega a função do usuário logado. */
    public static String getFuncaoUsuario() {
        return funcaoUsuario;
    }

    /** Limpa a sessão (logout). */
    public static void limparSessao() {
        idUsuario = 0;
        nomeUsuario = null;
        funcaoUsuario = null;
        idPaiSelecionado = 0;
    }

    public static void setIdPaiSelecionado(int id) {
        idPaiSelecionado = id;
    }

    public static int getIdPaiSelecionado() {
        return idPaiSelecionado;
    }
}

