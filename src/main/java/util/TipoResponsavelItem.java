package util;

public class TipoResponsavelItem {
    private int id;
    private String nome;

    /**
     * Construtor padrão
     * @param id
     * @param nome
     */
    public TipoResponsavelItem(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }

    // O que o ChoiceBox vai mostrar (Ex: "Mãe", "Pai")
    @Override public String toString() { return this.nome; }
}
