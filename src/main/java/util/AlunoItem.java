package util;

// Classe simples para guardar o NOME e o ID do aluno no ChoiceBox
public class AlunoItem {
    private int id;
    private String nome;

    public AlunoItem(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }

    // O ChoiceBox vai mostrar
    @Override public String toString() { return this.nome; }
}