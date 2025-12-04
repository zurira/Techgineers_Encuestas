package mx.edu.utez.encuestas.dao.impl;

import javafx.scene.image.Image;
import mx.edu.utez.encuestas.config.DBConnection;
import mx.edu.utez.encuestas.dao.IEncuesta;
import mx.edu.utez.encuestas.model.Encuesta;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EncuestaDaoImpl implements IEncuesta {

    @Override
    public List<Encuesta> obtenerEncuestasPorDocente(int idDocente) {
        List<Encuesta> lista = new ArrayList<>();
        String sql = "SELECT id, titulo, categoria, estado FROM Encuestas WHERE creador_id = ? ORDER BY id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idDocente);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Encuesta.EstadoEncuesta estado;

                String estadoBD = rs.getString("estado").trim().toLowerCase();
                switch (estadoBD) {
                    case "activa": estado = Encuesta.EstadoEncuesta.activa; break;
                    case "inactiva": estado = Encuesta.EstadoEncuesta.inactiva; break;
                    case "borrador": estado = Encuesta.EstadoEncuesta.borrador; break;
                    default: estado = Encuesta.EstadoEncuesta.borrador;
                }


                lista.add(new Encuesta(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("categoria"),
                        estado
                ));
            }


        } catch (SQLException e) {
            System.err.println("Error al obtener encuestas: " + e.getMessage());
        }

        return lista;
    }

    @Override
    public Encuesta obtenerEncuestaCompletaPorId(int idEncuesta) {
        String sql = "SELECT id, titulo, categoria, imagen, estado, creador_id, descripcion " +
                "FROM Encuestas WHERE id = ?";
        Encuesta encuesta = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEncuesta);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Encuesta.EstadoEncuesta estado;
                    String estadoBD = rs.getString("estado").trim().toLowerCase();
                    switch (estadoBD) {
                        case "activa": estado = Encuesta.EstadoEncuesta.activa; break;
                        case "inactiva": estado = Encuesta.EstadoEncuesta.inactiva; break;
                        case "borrador": estado = Encuesta.EstadoEncuesta.borrador; break;
                        default: estado = Encuesta.EstadoEncuesta.borrador;
                    }

                    encuesta = new Encuesta(
                            rs.getInt("id"),
                            rs.getString("titulo"),
                            rs.getString("categoria"),
                            rs.getBytes("imagen"),
                            estado,
                            rs.getInt("creador_id"),
                            rs.getString("descripcion")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener encuesta completa: " + e.getMessage());
        }
        return encuesta;
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
    public int guardarEncuesta(Encuesta encuesta) {
        String sql = "INSERT INTO encuestas (titulo, categoria, imagen, estado, creador_id, descripcion) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, encuesta.getTitulo());
            stmt.setString(2, encuesta.getCategoria());
            stmt.setBytes(3, encuesta.getImagen() != null ? encuesta.getImagen() : new byte[0]);
            stmt.setString(4, encuesta.getEstado().name());
            stmt.setInt(5, encuesta.getCreadorId());
            stmt.setString(6, encuesta.getDescripcionCorta());

            int rows = stmt.executeUpdate();
            if (rows == 1) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    //evita error de conversión
                    return (int) rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar encuesta: " + e.getMessage());
        }
        return -1;
    }


    @Override
    public boolean actualizarEncuesta(Encuesta encuesta) {
        // se revisa si la imgen es null
        boolean actualizarImagen = (encuesta.getImagen() != null && encuesta.getImagen().length > 0);

        // se hace un query diferente para ver si se cambio la imagen
        String sql;
        if (actualizarImagen) {
            sql = "UPDATE encuestas SET titulo = ?, categoria = ?, imagen = ?, estado = ?, creador_id = ?, descripcion = ? WHERE id = ?";
        } else {
            sql = "UPDATE encuestas SET titulo = ?, categoria = ?, estado = ?, creador_id = ?, descripcion = ? WHERE id = ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, encuesta.getTitulo());
            stmt.setString(2, encuesta.getCategoria());

            // el índice de los parámetros cambia si actualizamos la imagen
            int idx = 3;
            if (actualizarImagen) {
                stmt.setBytes(idx++, encuesta.getImagen());
            }

            stmt.setString(idx++, encuesta.getEstado().name());
            stmt.setInt(idx++, encuesta.getCreadorId());
            stmt.setString(idx++, encuesta.getDescripcionCorta());
            stmt.setInt(idx, encuesta.getId());

            return stmt.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error al actualizar encuesta: " + e.getMessage());
            return false;
        }
    }

    private static final String BASE_SELECT_ACTIVE =
            "SELECT id, titulo, categoria, imagen, estado, creador_id FROM Encuestas WHERE LOWER(estado) = 'activa'";

    private List<Encuesta> mapResultSetToEncuestas(ResultSet rs) throws SQLException {
        List<Encuesta> encuestas = new ArrayList<>();
        while (rs.next()) {
            Encuesta encuesta = new Encuesta();
            encuesta.setId(rs.getInt("id"));
            encuesta.setTitulo(rs.getString("titulo"));
            encuesta.setCategoria(rs.getString("categoria"));

            //normaliza el enum
            String estadoTexto = rs.getString("estado");
            if (estadoTexto != null) {
                encuesta.setEstado(Encuesta.EstadoEncuesta.valueOf(estadoTexto.trim().toLowerCase()));
            }

            encuesta.setCreadorId(rs.getInt("creador_id"));

            byte[] imagenBlob = rs.getBytes("imagen");
            if (imagenBlob != null) {
                // Convertir BLOB a Image de JavaFX
                Image imagen = new Image(new ByteArrayInputStream(imagenBlob));
                encuesta.setImagen(imagenBlob);
            } else {
                encuesta.setImagen(null);
            }

            // Creando texto de detalle simulado
            encuesta.setDescripcionCorta("Participa y opina sobre el tema de " + encuesta.getTitulo() + ".");

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
        final String SQL_CATEGORIES =
                "SELECT DISTINCT categoria FROM Encuestas WHERE LOWER(estado) = 'activa' ORDER BY categoria";
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

    @Override
    public boolean actualizarEstado(int idEncuesta, String nuevoEstado) {
        String sql = "UPDATE Encuestas SET estado = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, idEncuesta);
            return stmt.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error al actualizar estado: " + e.getMessage());
            return false;
        }
    }

    public static int contarEncuestas() {
        int total = 0;
        String query = "SELECT COUNT(*) FROM encuestas";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al contar encuestas: " + e.getMessage());
        }
        return total;
    }

    public static int contarEncuestasPorEstado(String estado) {
        int total = 0;
        String query = "SELECT COUNT(*) FROM encuestas WHERE estado = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, estado);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al contar encuestas por estado: " + e.getMessage());
        }
        return total;
    }

    public static int contarEncuestasPorDocente(int docenteId) {
        int total = 0;
        String query = "SELECT COUNT(*) FROM encuestas WHERE creador_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, docenteId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) total = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al contar encuestas por docente: " + e.getMessage());
        }
        return total;
    }

    public static int contarEncuestasPorDocenteEstado(int docenteId, String estado) {
        int total = 0;
        String query = "SELECT COUNT(*) FROM encuestas WHERE creador_id = ? AND estado = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, docenteId);
            stmt.setString(2, estado);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) total = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al contar encuestas por estado: " + e.getMessage());
        }
        return total;
    }





}