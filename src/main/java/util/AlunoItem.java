package util;

/**
 * Representa um item de Aluno, combinando ID e Nome.
 * Ela armazena os dados do aluno e sobrescreve {@link #toString()}
 * para exibir o nome na interface do usuário.
 */
public class AlunoItem {

    /** O identificador único do aluno . */
    private int id;

    /** O nome de exibição do aluno. */
    private String nome;

    /**
     * Constrói um novo item de Aluno.
     *
     * @param id   O ID único do aluno (chave primária).
     * @param nome O nome completo do aluno para exibição.
     */
    public AlunoItem(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    /**
     * Obtém o ID único do aluno.
     *
     * @return O ID do aluno.
     */
    public int getId() {
        return id;
    }

    /**
     * Obtém o nome de exibição do aluno.
     *
     * @return O nome do aluno.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Retorna a representação em String do objeto.
     * @return O nome do aluno ({@code this.nome}).
     */
    @Override
    public String toString() {
        return this.nome;
    }
}