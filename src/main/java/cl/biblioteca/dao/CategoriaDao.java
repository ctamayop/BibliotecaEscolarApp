package cl.biblioteca.dao;

import cl.biblioteca.model.Categoria;
import java.sql.SQLException;
import java.util.List;

public interface CategoriaDao {

    void crear(Categoria categoria) throws SQLException;

    List<Categoria> listar() throws SQLException;

    boolean actualizar(Categoria categoria) throws SQLException;

    boolean eliminar(int id) throws SQLException;
}
