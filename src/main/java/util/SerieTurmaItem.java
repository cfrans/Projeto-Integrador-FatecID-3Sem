package util;

public class SerieTurmaItem {
    private int id;
    private String nome;

    public SerieTurmaItem(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }

    // O que o ChoiceBox vai mostrar (Ex: "1º Ano A")
    @Override public String toString() { return this.nome; }
}
