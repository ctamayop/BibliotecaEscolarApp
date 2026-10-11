package cl.biblioteca.dao;

import cl.biblioteca.model.Prestamo;
import java.sql.SQLException;
import java.util.List;

public interface PrestamoDao {

    void crear(Prestamo prestamo) throws SQLException;

    List<Prestamo> listar() throws SQLException;

    boolean actualizar(Prestamo prestamo) throws SQLException;

    boolean eliminar(int id) throws SQLException;
}
