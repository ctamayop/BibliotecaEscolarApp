package cl.biblioteca.dao;

import cl.biblioteca.model.Libro;
import java.sql.SQLException;
import java.util.List;

public interface LibroDao {

    void crear(Libro libro) throws SQLException;

    List<Libro> listar() throws SQLException;

    boolean actualizar(Libro libro) throws SQLException;

    boolean eliminar(int id) throws SQLException;
}

