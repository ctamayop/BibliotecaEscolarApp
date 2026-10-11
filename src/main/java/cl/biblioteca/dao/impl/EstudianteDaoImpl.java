package cl.biblioteca.dao.impl;

import cl.biblioteca.dao.EstudianteDao;
import cl.biblioteca.database.DatabaseConnection;
import cl.biblioteca.model.Estudiante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDaoImpl implements EstudianteDao {

    @Override
    public void crear(Estudiante estudiante) throws SQLException {

        validarEstudiante(estudiante);

        String sql = "INSERT INTO estudiantes " +
                "(nombre, rut, curso, correo) VALUES (?, ?, ?, ?)";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getRut());
            ps.setString(3, estudiante.getCurso());
            ps.setString(4, estudiante.getCorreo());

            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new SQLException("No se pudo registrar el estudiante.");
            }
        }
    }

    @Override
    public List<Estudiante> listar() throws SQLException {

        List<Estudiante> estudiantes = new ArrayList<>();

        String sql = "SELECT * FROM estudiantes";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Estudiante estudiante = new Estudiante(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("rut"),
                        rs.getString("curso"),
                        rs.getString("correo")
                );

                estudiantes.add(estudiante);
            }
        }

        return estudiantes;
    }

    @Override
    public boolean actualizar(Estudiante estudiante) throws SQLException {

        validarEstudiante(estudiante);

        if (estudiante.getId() <= 0) {
            throw new SQLException("El ID del estudiante no es válido.");
        }

        String sql = "UPDATE estudiantes SET nombre = ?, " +
                "rut = ?, curso = ?, correo = ? WHERE id = ?";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getRut());
            ps.setString(3, estudiante.getCurso());
            ps.setString(4, estudiante.getCorreo());
            ps.setInt(5, estudiante.getId());

            int filas = ps.executeUpdate();

            return filas > 0;
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {

        if (id <= 0) {
            throw new SQLException("El ID del estudiante no es válido.");
        }

        String sql = "DELETE FROM estudiantes WHERE id = ?";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            int filas = ps.executeUpdate();

            return filas > 0;
        }
    }

    private void validarEstudiante(Estudiante estudiante)
            throws SQLException {

        if (estudiante == null) {
            throw new SQLException("Debe ingresar un estudiante.");
        }

        if (estudiante.getNombre() == null ||
                estudiante.getNombre().trim().isEmpty() ||
                estudiante.getRut() == null ||
                estudiante.getRut().trim().isEmpty() ||
                estudiante.getCurso() == null ||
                estudiante.getCurso().trim().isEmpty() ||
                estudiante.getCorreo() == null ||
                estudiante.getCorreo().trim().isEmpty()) {

            throw new SQLException(
                    "Todos los campos del estudiante son obligatorios."
            );
        }
    }
}
