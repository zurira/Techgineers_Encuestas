package mx.edu.utez.encuestas.model;

public class Pregunta {
    private int id;
    private String texto;

    public Pregunta(int id, String texto) {
        this.id = id;
        this.texto = texto;
    }

    public int getId() { return id; }
    public String getTexto() { return texto; }
}