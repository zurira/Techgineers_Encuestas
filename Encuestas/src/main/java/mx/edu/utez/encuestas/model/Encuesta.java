package mx.edu.utez.encuestas.model;

public class Encuesta {
    private int id;
    private String titulo;
    private String categoria;
    private String estado;

    public Encuesta(int id, String titulo, String categoria, String estado) {
        this.id = id;
        this.titulo = titulo;
        this.categoria = categoria;
        this.estado = estado;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getCategoria() { return categoria; }
    public String getEstado() { return estado; }
}
