package util;

public class FuncaoItem {
    private int id;
    private String nome;

    public FuncaoItem(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }

    // O que o ChoiceBox vai mostrar (Ex: "Coordenador")
    @Override public String toString() { return this.nome; }
}
