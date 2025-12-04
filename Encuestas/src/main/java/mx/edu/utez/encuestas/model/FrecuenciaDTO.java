package mx.edu.utez.encuestas.model;



public class FrecuenciaDTO {
    private int preguntaId;
    private String pregunta;
    private int opcionId;
    private String opcion;
    private int total;

    public FrecuenciaDTO() {}

    public int getPreguntaId() { return preguntaId; }
    public void setPreguntaId(int preguntaId) { this.preguntaId = preguntaId; }

    public String getPregunta() { return pregunta; }
    public void setPregunta(String pregunta) { this.pregunta = pregunta; }

    public int getOpcionId() { return opcionId; }
    public void setOpcionId(int opcionId) { this.opcionId = opcionId; }

    public String getOpcion() { return opcion; }
    public void setOpcion(String opcion) { this.opcion = opcion; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
}
