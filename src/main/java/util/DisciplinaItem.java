package util;

// Classe simples para guardar o NOME e o ID da disciplina
public class DisciplinaItem {
    private int id;
    private String nome;

    public DisciplinaItem(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }

    // Isso é o que o ChoiceBox vai mostrar (Ex: "Matemática")
    @Override public String toString() { return this.nome; }
}