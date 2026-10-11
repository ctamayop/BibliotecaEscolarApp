package cl.biblioteca.dao;

import cl.biblioteca.model.Estudiante;
import java.sql.SQLException;
import java.util.List;

public interface EstudianteDao {

    void crear(Estudiante estudiante) throws SQLException;

    List<Estudiante> listar() throws SQLException;

    boolean actualizar(Estudiante estudiante) throws SQLException;

    boolean eliminar(int id) throws SQLException;
}
