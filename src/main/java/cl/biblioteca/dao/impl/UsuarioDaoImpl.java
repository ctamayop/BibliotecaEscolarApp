package cl.biblioteca.dao.impl;

import cl.biblioteca.dao.UsuarioDao;
import cl.biblioteca.database.DatabaseConnection;
import cl.biblioteca.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDaoImpl implements UsuarioDao {

    @Override
    public void crear(Usuario usuario) throws SQLException {

        validarUsuario(usuario);

        String sql = "INSERT INTO usuarios " +
                "(nombre, rut, correo, contraseña, rol) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getRut());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getContrasena());
            ps.setString(5, usuario.getRol());

            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se pudo registrar el usuario.");
            }
        }
    }

    @Override
    public List<Usuario> listar() throws SQLException {

        List<Usuario> usuarios = new ArrayList<>();

        String sql = "SELECT * FROM usuarios";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Usuario usuario = new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("rut"),
                        rs.getString("correo"),
                        rs.getString("contraseña"),
                        rs.getString("rol")
                );

                usuarios.add(usuario);
            }
        }

        return usuarios;
    }

    @Override
    public boolean actualizar(Usuario usuario) throws SQLException {

        validarUsuario(usuario);

        if (usuario.getId() <= 0) {
            throw new SQLException("El ID del usuario no es válido.");
        }

        String sql = "UPDATE usuarios SET nombre = ?, rut = ?, " +
                "correo = ?, contraseña = ?, rol = ? WHERE id = ?";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getRut());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getContrasena());
            ps.setString(5, usuario.getRol());
            ps.setInt(6, usuario.getId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {

        if (id <= 0) {
            throw new SQLException("El ID del usuario no es válido.");
        }

        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Usuario autenticar(String correo, String contrasena)
            throws SQLException {

        if (correo == null || correo.trim().isEmpty() ||
                contrasena == null || contrasena.isEmpty()) {
            return null;
        }

        String sql = "SELECT * FROM usuarios " +
                "WHERE correo = ? AND contraseña = ?";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, correo);
            ps.setString(2, contrasena);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("rut"),
                            rs.getString("correo"),
                            rs.getString("contraseña"),
                            rs.getString("rol")
                    );
                }
            }
        }

        return null;
    }

    private void validarUsuario(Usuario usuario) throws SQLException {

        if (usuario == null) {
            throw new SQLException("Debe ingresar un usuario.");
        }

        if (usuario.getNombre() == null ||
                usuario.getNombre().trim().isEmpty() ||
                usuario.getRut() == null ||
                usuario.getRut().trim().isEmpty() ||
                usuario.getCorreo() == null ||
                usuario.getCorreo().trim().isEmpty() ||
                usuario.getContrasena() == null ||
                usuario.getContrasena().isEmpty()) {

            throw new SQLException(
                    "Todos los campos del usuario son obligatorios."
            );
        }

        if (!"bibliotecario".equals(usuario.getRol()) &&
                !"estudiante".equals(usuario.getRol())) {

            throw new SQLException("El rol del usuario no es válido.");
        }
    }
}

