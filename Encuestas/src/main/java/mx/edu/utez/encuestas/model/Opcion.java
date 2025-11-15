package mx.edu.utez.encuestas.model;

public class Opcion {
    private int id;
    private String texto;
    private int preguntaId;

    public Opcion(int id, String texto, int preguntaId) {
        this.id = id;
        this.texto = texto;
        this.preguntaId = preguntaId;
    }

    public Opcion(int id, String texto) {
        this.id = id;
        this.texto = texto;
    }

    public Opcion(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public int getPreguntaId() {
        return preguntaId;
    }

    public void setPreguntaId(int preguntaId) {
        this.preguntaId = preguntaId;
    }
}