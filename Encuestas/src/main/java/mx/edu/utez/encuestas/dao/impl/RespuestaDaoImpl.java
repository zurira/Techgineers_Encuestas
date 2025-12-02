package mx.edu.utez.encuestas.dao.impl;

import mx.edu.utez.encuestas.config.DBConnection;
import mx.edu.utez.encuestas.dao.IRespuesta;
import mx.edu.utez.encuestas.model.Respuesta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
    public List<Respuesta> findByOpcionId(int opcionId) throws SQLException {
        final String SQL_SELECT = "SELECT * FROM Respuestas WHERE opcion_id = ?";
        List<Respuesta> respuestas = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT)) {

            stmt.setInt(1, opcionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    respuestas.add(mapResultSet(rs));
                }
            }
        }
        return respuestas;
    }


    @Override
    public List<Respuesta> findAll() throws SQLException {
        final String SQL_SELECT_ALL = "SELECT * FROM Respuestas";
        List<Respuesta> respuestas = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                respuestas.add(mapResultSet(rs));
            }
        }
        return respuestas;
    }

    private Respuesta mapResultSet(ResultSet rs) throws SQLException {
        Respuesta respuesta = new Respuesta();
        respuesta.setId(rs.getInt("id"));
        respuesta.setNombreAlumno(rs.getString("nombre_alumno"));
        respuesta.setGrupo(rs.getString("grupo"));
        respuesta.setOpcionId(rs.getInt("opcion_id"));
        respuesta.setFechaRespuesta(rs.getString("fecha_respuesta"));
        return respuesta;
    }

}
