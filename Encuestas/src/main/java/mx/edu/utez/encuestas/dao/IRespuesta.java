package mx.edu.utez.encuestas.dao;

import mx.edu.utez.encuestas.model.Respuesta;

import java.sql.SQLException;
import java.util.List;

public interface IRespuesta {
    boolean guardarRespuesta(Respuesta respuesta);
    List<Respuesta> findOpcionId(int opcionId) throws SQLException;
    List<Respuesta> findAll() throws SQLException;
}
