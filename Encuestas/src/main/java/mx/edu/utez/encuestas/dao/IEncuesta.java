package mx.edu.utez.encuestas.dao;

import mx.edu.utez.encuestas.model.Encuesta;

import java.sql.SQLException;
import java.util.List;

public interface IEncuesta {
    List<Encuesta> obtenerEncuestasPorDocente(int idDocente);
    int obtenerUltimoIdEncuestaDelDocente(int idDocente);
    boolean crearEncuesta(String titulo, String categoria, byte[] imagen, String estado, int idDocente);
    List<Encuesta> findAllActive() throws SQLException;
    List<Encuesta> findActiveByCategory(String categoria) throws SQLException;
    List<String> findAllActiveCategories() throws SQLException;

}