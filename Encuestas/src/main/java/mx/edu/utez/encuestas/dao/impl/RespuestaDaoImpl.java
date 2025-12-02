package mx.edu.utez.encuestas.dao.impl;

import mx.edu.utez.encuestas.config.DBConnection;
import mx.edu.utez.encuestas.dao.IRespuesta;
import mx.edu.utez.encuestas.model.Respuesta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class RespuestaDaoImpl implements IRespuesta {
    @Override
    public boolean guardarRespuesta(Respuesta respuesta) throws SQLException {
        final String SQL_INSERT = "INSERT INTO Respuestas (nombre_alumno, grupo, opcion_id, fecha_respuesta) VALUES (?, ?, ?, SYSDATE)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

            stmt.setString(1, respuesta.getNombreAlumno());
            stmt.setString(2, respuesta.getGrupo());
            stmt.setInt(3, respuesta.getOpcionId());

            return stmt.executeUpdate() > 0;
        }
    }



    @Override
    public List<Respuesta> findOpcionId(int opcionId) throws SQLException {
        return List.of();
    }

    @Override
    public List<Respuesta> findAll() throws SQLException {
        return List.of();
    }
}
