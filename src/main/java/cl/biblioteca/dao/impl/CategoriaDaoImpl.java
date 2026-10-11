package cl.biblioteca.dao.impl;

import cl.biblioteca.dao.CategoriaDao;
import cl.biblioteca.database.DatabaseConnection;
import cl.biblioteca.model.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDaoImpl implements CategoriaDao {

    @Override
    public void crear(Categoria categoria) throws SQLException {

        validarCategoria(categoria);

        String sql = "INSERT INTO categorias (nombre) VALUES (?)";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, categoria.getNombre());

            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new SQLException("No se pudo registrar la categoría.");
            }
        }
    }

    @Override
    public List<Categoria> listar() throws SQLException {

        List<Categoria> categorias = new ArrayList<>();

        String sql = "SELECT * FROM categorias";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Categoria categoria = new Categoria(
                        rs.getInt("id"),
                        rs.getString("nombre")
                );

                categorias.add(categoria);
            }
        }

        return categorias;
    }

    @Override
    public boolean actualizar(Categoria categoria) throws SQLException {

        validarCategoria(categoria);

        if (categoria.getId() <= 0) {
            throw new SQLException("El ID de la categoría no es válido.");
        }

        String sql = "UPDATE categorias SET nombre = ? WHERE id = ?";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, categoria.getNombre());
            ps.setInt(2, categoria.getId());

            int filas = ps.executeUpdate();

            return filas > 0;
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {

        if (id <= 0) {
            throw new SQLException("El ID de la categoría no es válido.");
        }

        String sql = "DELETE FROM categorias WHERE id = ?";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            int filas = ps.executeUpdate();

            return filas > 0;
        }
    }

    private void validarCategoria(Categoria categoria) throws SQLException {

        if (categoria == null ||
                categoria.getNombre() == null ||
                categoria.getNombre().trim().isEmpty()) {

            throw new SQLException(
                    "Debe ingresar un nombre para la categoría."
            );
        }
    }
}
