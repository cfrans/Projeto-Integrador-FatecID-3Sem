package util;

public class SessaoUsuario {

    private static int idUsuario;
    private static String nomeUsuario;
    private static String funcaoUsuario;
    private static int idPaiSelecionado;
    private static boolean modoAdminPadrao = false;

    /**
     * Define o usuário que acabou de logar.
     *
     * @param id     O id_usuario do banco.
     * @param nome   O nome do usuário.
     * @param funcao O nome da função (ex: "Administrador", "Professor", "T.I.")
     */
    public static void setUsuarioLogado(int id, String nome, String funcao, boolean isPadrao) {
        idUsuario = id;
        nomeUsuario = nome;
        funcaoUsuario = funcao;
        modoAdminPadrao = isPadrao;
    }

    public static boolean isModoAdminPadrao() {
        return modoAdminPadrao;
    }

    /**
     * Retorna o ID do usuário da sessão.
     * @return Retorna o ID do usuário
     */
    public static int getIdUsuario() {
        return idUsuario;
    }

    /**
     * Retorna o nome do usuário da sessão.
     * @return Retorna o nome do usuário
     */
    public static String getNomeUsuario() {
        return nomeUsuario;
    }

    /**
     * Retorna a função do usuário da sessão.
     * @return Retorna a função do usuário
     */
    public static String getFuncaoUsuario() {
        return funcaoUsuario;
    }

    /**
     * Limpa a sessão, fazendo logoff.
     */
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

    private static int idIntervencaoSelecionada;
    public static int getIdIntervencaoSelecionada() { return idIntervencaoSelecionada; }
    public static void setIdIntervencaoSelecionada(int id) { idIntervencaoSelecionada = id; }
}

