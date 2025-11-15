package mx.edu.utez.encuestas.dao.impl;

import mx.edu.utez.encuestas.config.DBConnection;
import mx.edu.utez.encuestas.dao.IEncuestaDao;
import mx.edu.utez.encuestas.model.Encuesta;
import mx.edu.utez.encuestas.model.Opcion;
import mx.edu.utez.encuestas.model.Pregunta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EncuestaDaoImpl implements IEncuestaDao {

    @Override
    public List<Encuesta> obtenerEncuestasPorDocente(int idDocente) {
        List<Encuesta> lista = new ArrayList<>();
        String sql = "SELECT id, titulo, categoria, estado FROM Encuestas WHERE creador_id = ? ORDER BY id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idDocente);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(new Encuesta(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("categoria"),
                        rs.getString("estado")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener encuestas: " + e.getMessage());
        }

        return lista;
    }

    public int insertarPregunta(String texto, int idEncuesta) {
        String sql = "INSERT INTO Preguntas (texto, encuesta_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, texto);
            stmt.setInt(2, idEncuesta);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.err.println("Error al insertar pregunta: " + e.getMessage());
        }
        return -1;
    }

    public boolean insertarOpcion(String texto, int idPregunta) {
        String sql = "INSERT INTO Opciones (texto, pregunta_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, texto);
            stmt.setInt(2, idPregunta);
            return stmt.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error al insertar opción: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int obtenerUltimoIdEncuestaDelDocente(int idDocente) {
        String sql = "SELECT id FROM Encuestas WHERE creador_id = ? ORDER BY id DESC FETCH FIRST 1 ROWS ONLY";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idDocente);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener el último ID de encuesta: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean crearEncuesta(String titulo, String categoria, byte[] imagen, String estado, int idDocente) {
        String sql = "INSERT INTO Encuestas (titulo, categoria, imagen, estado, creador_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, titulo);
            stmt.setString(2, categoria);
            stmt.setBytes(3, imagen);
            stmt.setString(4, estado);
            stmt.setInt(5, idDocente); // ← aquí se guarda el docente

            int filas = stmt.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.err.println("Error al crear encuesta: " + e.getMessage());
            return false;
        }
    }

    public List<Pregunta> obtenerPreguntasPorEncuesta(int idEncuesta) {
        List<Pregunta> lista = new ArrayList<>();
        String sql = "SELECT id, texto FROM Preguntas WHERE encuesta_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEncuesta);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(new Pregunta(rs.getInt("id"), rs.getString("texto")));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener preguntas: " + e.getMessage());
        }

        return lista;
    }

    public List<Opcion> obtenerOpcionesPorPregunta(int idPregunta) {
        List<Opcion> lista = new ArrayList<>();
        String sql = "SELECT id, texto FROM Opciones WHERE pregunta_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPregunta);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(new Opcion(rs.getInt("id"), rs.getString("texto")));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener opciones: " + e.getMessage());
        }

        return lista;
    }

    private static final String BASE_SELECT_ACTIVE =
            "SELECT id, titulo, categoria, imagen, estado, creador_id FROM Encuestas WHERE estado = 'activa'";

    private List<Encuesta> mapResultSetToEncuestas(ResultSet rs) throws SQLException {
        List<Encuesta> encuestas = new ArrayList<>();
        while (rs.next()) {
            Encuesta encuesta = new Encuesta();
            encuesta.setId(rs.getLong("id"));
            encuesta.setTitulo(rs.getString("titulo"));
            encuesta.setCategoria(rs.getString("categoria"));
            encuesta.setEstado(rs.getString("estado"));
            encuesta.setCreadorId(rs.getLong("creador_id"));

            byte[] imagenBlob = rs.getBytes("imagen");
            Image imagen = null;
            if (imagenBlob != null) {
                // Convertir BLOB a Image de JavaFX
                imagen = new Image(new ByteArrayInputStream(imagenBlob));
            } else {
                imagen = new Image("/images/default-survey.png"); // Placeholder
            }
            encuesta.setImagen(imagen);

            // Creando texto de detalle simulado
            encuesta.setDescripcionCorta("Participa y opina sobre el tema de " + encuesta.getCategoria() + ".");

            encuestas.add(encuesta);
        }
        return encuestas;
    }

    @Override
    public List<Encuesta> findAllActive() throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(BASE_SELECT_ACTIVE);
             ResultSet rs = stmt.executeQuery()) {
            return mapResultSetToEncuestas(rs);
        }
    }

    @Override
    public List<Encuesta> findActiveByCategory(String categoria) throws SQLException {
        final String SQL_BY_CAT = BASE_SELECT_ACTIVE + " AND categoria = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_BY_CAT)) {
            stmt.setString(1, categoria);
            try (ResultSet rs = stmt.executeQuery()) {
                return mapResultSetToEncuestas(rs);
            }
        }
    }

    @Override
    public List<String> findAllActiveCategories() throws SQLException {
        final String SQL_CATEGORIES = "SELECT DISTINCT categoria FROM Encuestas WHERE estado = 'activa' ORDER BY categoria";
        List<String> categories = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_CATEGORIES);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString("categoria"));
            }
        }
        return categories;
    }
}