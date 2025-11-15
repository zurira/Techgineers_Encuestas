package mx.edu.utez.encuestas.model;

public class Pregunta {
    private int id;
    private String texto;
    private int encuestaId;

    public Pregunta(int id, String texto, int encuestaId) {
        this.id = id;
        this.texto = texto;
        this.encuestaId = encuestaId;
    }

    public Pregunta(int id, String texto) {
        this.id = id;
        this.texto = texto;
    }

    public Pregunta(){

    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEncuestaId() {
        return encuestaId;
    }

    public void setEncuestaId(int encuestaId) {
        this.encuestaId = encuestaId;
    }
}