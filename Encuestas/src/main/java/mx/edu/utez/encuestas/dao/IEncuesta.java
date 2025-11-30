package mx.edu.utez.encuestas.dao;

import mx.edu.utez.encuestas.model.Encuesta;

import java.sql.SQLException;
import java.util.List;

public interface IEncuesta {
    List<Encuesta> obtenerEncuestasPorDocente(int idDocente);
    int obtenerUltimoIdEncuestaDelDocente(int idDocente);
    int guardarEncuesta(Encuesta encuesta);
    boolean actualizarEncuesta(Encuesta encuesta);
    public Encuesta obtenerEncuestaCompletaPorId(int idEncuesta);
    boolean crearEncuesta(String titulo, String categoria, byte[] imagen, String estado, String descripcion, int idDocente);
    List<Encuesta> findAllActive() throws SQLException;
    List<Encuesta> findActiveByCategory(String categoria) throws SQLException;
    List<String> findAllActiveCategories() throws SQLException;
    boolean actualizarEstado(int idEncuesta, String nuevoEstado);
}