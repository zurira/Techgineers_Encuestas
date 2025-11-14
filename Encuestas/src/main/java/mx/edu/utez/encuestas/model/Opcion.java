package mx.edu.utez.encuestas.model;

public class Opcion {
    private int id;
    private String texto;

    public Opcion(int id, String texto) {
        this.id = id;
        this.texto = texto;
    }

    public int getId() { return id; }
    public String getTexto() { return texto; }
}