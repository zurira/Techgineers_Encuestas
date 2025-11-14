package mx.edu.utez.encuestas.dao.impl;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;
import mx.edu.utez.encuestas.config.DBConnection;
import mx.edu.utez.encuestas.dao.impl.dao.EncuestaDao;
import mx.edu.utez.encuestas.model.Encuesta;

public class EncuestaDaoImpl implements EncuestaDao {
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