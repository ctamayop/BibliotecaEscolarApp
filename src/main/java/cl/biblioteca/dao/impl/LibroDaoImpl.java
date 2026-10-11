package cl.biblioteca.dao.impl;

import cl.biblioteca.dao.LibroDao;
import cl.biblioteca.database.DatabaseConnection;
import cl.biblioteca.model.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LibroDaoImpl implements LibroDao {

    @Override
    public void crear(Libro libro) throws SQLException {

        validarLibro(libro);

        String sql = "INSERT INTO libros " +
                "(titulo, autor, isbn, editorial, stock, id_categoria) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getEditorial());
            ps.setInt(5, libro.getStock());
            ps.setInt(6, libro.getIdCategoria());

            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new SQLException("No se pudo registrar el libro.");
            }
        }
    }

    @Override
    public List<Libro> listar() throws SQLException {

        List<Libro> libros = new ArrayList<>();

        String sql = "SELECT * FROM libros";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Libro libro = new Libro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("isbn"),
                        rs.getString("editorial"),
                        rs.getInt("stock"),
                        rs.getInt("id_categoria")
                );

                libros.add(libro);
            }
        }

        return libros;
    }

    @Override
    public boolean actualizar(Libro libro) throws SQLException {

        validarLibro(libro);

        if (libro.getId() <= 0) {
            throw new SQLException("El ID del libro no es válido.");
        }

        String sql = "UPDATE libros SET titulo = ?, " +
                "autor = ?, isbn = ?, editorial = ?, " +
                "stock = ?, id_categoria = ? WHERE id = ?";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getEditorial());
            ps.setInt(5, libro.getStock());
            ps.setInt(6, libro.getIdCategoria());
            ps.setInt(7, libro.getId());

            int filas = ps.executeUpdate();

            return filas > 0;
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {

        if (id <= 0) {
            throw new SQLException("El ID del libro no es válido.");
        }

        String sql = "DELETE FROM libros WHERE id = ?";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            int filas = ps.executeUpdate();

            return filas > 0;
        }
    }

    private void validarLibro(Libro libro) throws SQLException {

        if (libro == null) {
            throw new SQLException("Debe ingresar un libro.");
        }

        if (libro.getTitulo() == null ||
                libro.getTitulo().trim().isEmpty() ||
                libro.getAutor() == null ||
                libro.getAutor().trim().isEmpty() ||
                libro.getIsbn() == null ||
                libro.getIsbn().trim().isEmpty() ||
                libro.getEditorial() == null ||
                libro.getEditorial().trim().isEmpty()) {

            throw new SQLException(
                    "Todos los campos del libro son obligatorios."
            );
        }

        if (libro.getStock() < 0) {
            throw new SQLException("El stock no puede ser negativo.");
        }

        if (libro.getIdCategoria() <= 0) {
            throw new SQLException("Debe seleccionar una categoría válida.");
        }
    }
}
