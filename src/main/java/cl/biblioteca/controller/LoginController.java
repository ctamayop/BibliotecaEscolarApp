package cl.biblioteca.controller;

import cl.biblioteca.dao.UsuarioDao;
import cl.biblioteca.dao.impl.UsuarioDaoImpl;
import cl.biblioteca.model.Usuario;

import java.sql.SQLException;

public class LoginController {

    private final UsuarioDao usuarioDao;

    public LoginController() {
        usuarioDao = new UsuarioDaoImpl();
    }

    public Usuario iniciarSesion(String correo, String contrasena)
            throws SQLException {

        if (correo == null || correo.trim().isEmpty() ||
                contrasena == null || contrasena.isEmpty()) {

            throw new IllegalArgumentException(
                    "Debe ingresar correo y contraseña."
            );
        }

        return usuarioDao.autenticar(correo.trim(), contrasena);
    }
}
