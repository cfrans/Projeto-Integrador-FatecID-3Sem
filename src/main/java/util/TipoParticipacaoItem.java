package util;

public class TipoParticipacaoItem {
    private int id;
    private String nome;

    public TipoParticipacaoItem(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }

    // O que o ChoiceBox vai mostrar (Ex: "Muito Alta", "Média")
    @Override public String toString() { return this.nome; }
}
