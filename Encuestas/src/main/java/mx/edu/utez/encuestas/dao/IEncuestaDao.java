package mx.edu.utez.encuestas.dao;

import mx.edu.utez.encuestas.model.Encuesta;

import java.util.List;

public interface IEncuestaDao {
    int obtenerUltimoIdEncuestaDelDocente(int idDocente);
    public List<Encuesta> obtenerEncuestasPorDocente(int idDocente);
    boolean crearEncuesta(String titulo, String categoria, byte[] imagen, String estado, int idDocente);
}