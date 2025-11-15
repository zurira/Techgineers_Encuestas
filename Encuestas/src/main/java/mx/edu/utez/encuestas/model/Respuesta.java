package mx.edu.utez.encuestas.model;

public class Respuesta {
    private int id;
    private String nombreAlumno;
    private String grupo;
    private int opcionId;
    private String fechaRespuesta;

    public Respuesta(int id, String nombreAlumno, String grupo, int opcionId, String fechaRespuesta) {
        this.id = id;
        this.nombreAlumno = nombreAlumno;
        this.grupo = grupo;
        this.opcionId = opcionId;
        this.fechaRespuesta = fechaRespuesta;
    }

    public Respuesta(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreAlumno() {
        return nombreAlumno;
    }

    public void setNombreAlumno(String nombreAlumno) {
        this.nombreAlumno = nombreAlumno;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(String fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

    public int getOpcionId() {
        return opcionId;
    }

    public void setOpcionId(int opcionId) {
        this.opcionId = opcionId;
    }
}
