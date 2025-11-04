package util;

// Classe simples para guardar o NOME e o ID do coordenador
public class CoordenadorItem {
    private int id;
    private String nome;

    public CoordenadorItem(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }

    // O que o ChoiceBox vai mostrar
    @Override public String toString() { return this.nome; }
}