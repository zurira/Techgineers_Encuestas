package mx.edu.utez.encuestas.dao.impl.dao;
import mx.edu.utez.encuestas.model.Encuesta;

import java.sql.SQLException;
import java.util.List;

public interface EncuestaDao {
    List<Encuesta> findAllActive() throws SQLException;
    List<Encuesta> findActiveByCategory(String categoria) throws SQLException;
    List<String> findAllActiveCategories() throws SQLException;
}
