package cl.biblioteca.controller;

import cl.biblioteca.dao.CategoriaDao;
import cl.biblioteca.dao.EstudianteDao;
import cl.biblioteca.dao.LibroDao;
import cl.biblioteca.dao.UsuarioDao;

import cl.biblioteca.dao.impl.CategoriaDaoImpl;
import cl.biblioteca.dao.impl.EstudianteDaoImpl;
import cl.biblioteca.dao.impl.LibroDaoImpl;
import cl.biblioteca.dao.impl.UsuarioDaoImpl;

import cl.biblioteca.model.Categoria;
import cl.biblioteca.model.Estudiante;
import cl.biblioteca.model.Libro;
import cl.biblioteca.model.Usuario;

import java.sql.SQLException;
import java.util.List;

public class BibliotecaController {

    private final LibroDao libroDao;
    private final CategoriaDao categoriaDao;
    private final EstudianteDao estudianteDao;
    private final UsuarioDao usuarioDao;

    public BibliotecaController() {
        libroDao = new LibroDaoImpl();
        categoriaDao = new CategoriaDaoImpl();
        estudianteDao = new EstudianteDaoImpl();
        usuarioDao = new UsuarioDaoImpl();
    }

    // GESTIÓN DE LIBROS

    public void crearLibro(Libro libro) throws SQLException {
        libroDao.crear(libro);
    }

    public List<Libro> listarLibros() throws SQLException {
        return libroDao.listar();
    }

    public boolean actualizarLibro(Libro libro)
            throws SQLException {
        return libroDao.actualizar(libro);
    }

    public boolean eliminarLibro(int id) throws SQLException {
        return libroDao.eliminar(id);
    }

    // GESTIÓN DE CATEGORÍAS

    public void crearCategoria(Categoria categoria)
            throws SQLException {
        categoriaDao.crear(categoria);
    }

    public List<Categoria> listarCategorias()
            throws SQLException {
        return categoriaDao.listar();
    }

    public boolean actualizarCategoria(Categoria categoria)
            throws SQLException {
        return categoriaDao.actualizar(categoria);
    }

    public boolean eliminarCategoria(int id)
            throws SQLException {
        return categoriaDao.eliminar(id);
    }

    // GESTIÓN DE ESTUDIANTES

    public void crearEstudiante(Estudiante estudiante)
            throws SQLException {
        estudianteDao.crear(estudiante);
    }

    public List<Estudiante> listarEstudiantes()
            throws SQLException {
        return estudianteDao.listar();
    }

    public boolean actualizarEstudiante(Estudiante estudiante)
            throws SQLException {
        return estudianteDao.actualizar(estudiante);
    }

    public boolean eliminarEstudiante(int id)
            throws SQLException {
        return estudianteDao.eliminar(id);
    }

    // GESTIÓN DE USUARIOS

    public void crearUsuario(Usuario usuario)
            throws SQLException {
        usuarioDao.crear(usuario);
    }

    public List<Usuario> listarUsuarios()
            throws SQLException {
        return usuarioDao.listar();
    }

    public boolean actualizarUsuario(Usuario usuario)
            throws SQLException {
        return usuarioDao.actualizar(usuario);
    }

    public boolean eliminarUsuario(int id)
            throws SQLException {
        return usuarioDao.eliminar(id);
    }
}

