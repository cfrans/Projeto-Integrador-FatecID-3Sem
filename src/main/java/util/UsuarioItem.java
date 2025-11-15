package util;

public class UsuarioItem {
    private int id;
    private String nome;

    /**
     * Construtor padrão
     * @param id
     * @param nome
     */
    public UsuarioItem(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }

    // O que o ChoiceBox vai mostrar
    @Override public String toString() { return this.nome; }
}