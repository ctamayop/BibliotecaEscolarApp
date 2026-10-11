package cl.biblioteca.dao;

import cl.biblioteca.model.Usuario;
import java.sql.SQLException;
import java.util.List;

public interface UsuarioDao {

    void crear(Usuario usuario) throws SQLException;

    List<Usuario> listar() throws SQLException;

    boolean actualizar(Usuario usuario) throws SQLException;

    boolean eliminar(int id) throws SQLException;

    Usuario autenticar(String correo, String contrasena) throws SQLException;
}
