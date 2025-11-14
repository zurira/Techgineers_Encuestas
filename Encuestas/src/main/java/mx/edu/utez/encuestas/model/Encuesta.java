package mx.edu.utez.encuestas.model;

import javafx.scene.image.Image;

public class Encuesta {
    private long id;
    private String titulo;
    private String categoria;
    private Image imagen;
    private String estado;
    private long creadorId;
    private String descripcion;

    // Constructor completo
    public Encuesta(long id, String titulo, String categoria, Image imagen, String estado, long creadorId, String descripcion) {
        this.id = id;
        this.titulo = titulo;
        this.categoria = categoria;
        this.imagen = imagen;
        this.estado = estado;
        this.creadorId = creadorId;
        this.descripcion = descripcion;
    }

    // Constructor vacío (necesario para el DAO)
    public Encuesta() {
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Image getImagen() { return imagen; }
    public void setImagen(Image imagen) { this.imagen = imagen; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public long getCreadorId() { return creadorId; }
    public void setCreadorId(long creadorId) { this.creadorId = creadorId; }

    public String getDescripcionCorta() { return descripcion; }
    public void setDescripcionCorta(String descripcionCorta) { this.descripcion = descripcionCorta; }
}