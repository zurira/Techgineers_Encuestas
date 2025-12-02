package mx.edu.utez.encuestas.model;

public class Encuesta {
    private int id;
    private String titulo;
    private String categoria;
    private byte[] imagen;
    private EstadoEncuesta estado;
    private int creadorId;
    private String descripcion;

    public enum EstadoEncuesta { activa, inactiva, borrador }

    // Constructor completo
    public Encuesta(int id, String titulo, String categoria, byte[] imagen, EstadoEncuesta estado, int creadorId, String descripcion) {
        this.id = id;
        this.titulo = titulo;
        this.categoria = categoria;
        this.imagen = imagen;
        this.estado = estado;
        this.creadorId = creadorId;
        this.descripcion = descripcion;
    }

    //constructor para mostrar las cards con info basica en el panel de docentes
    public Encuesta(int id, String titulo, String categoria,  EstadoEncuesta estado) {
        this.id = id;
        this.titulo = titulo;
        this.categoria = categoria;
        this.estado = estado;
    }
    // Constructor vacío (necesario para el DAO)
    public Encuesta() {
    }

    public static EstadoEncuesta fromStringEstado(String texto) {
        if (texto == null) return null;
        return EstadoEncuesta.valueOf(texto.trim().toLowerCase());
    }


    //metodos usados para cambiar y actualizar el switch
    public boolean isActiva() {
        return estado == EstadoEncuesta.activa;
    }

    public void setActiva(boolean activa) {
        this.estado = activa ? EstadoEncuesta.activa : EstadoEncuesta.inactiva;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public EstadoEncuesta getEstado() {
        return estado;
    }

    public void setEstado(EstadoEncuesta estado) {
        this.estado = estado;
    }

    public int getCreadorId() { return creadorId; }
    public void setCreadorId(int creadorId) { this.creadorId = creadorId; }

    public String getDescripcionCorta() { return descripcion; }
    public void setDescripcionCorta(String descripcionCorta) { this.descripcion = descripcionCorta; }
}
