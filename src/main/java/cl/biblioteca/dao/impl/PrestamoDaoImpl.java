package cl.biblioteca.dao.impl;

import cl.biblioteca.dao.PrestamoDao;
import cl.biblioteca.database.DatabaseConnection;
import cl.biblioteca.model.Prestamo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDaoImpl implements PrestamoDao {

    @Override
    public synchronized void crear(Prestamo prestamo)
            throws SQLException {

        if (prestamo == null ||
                prestamo.getIdEstudiante() <= 0 ||
                prestamo.getIdLibro() <= 0 ||
                prestamo.getFechaPrestamo() == null ||
                prestamo.getFechaDevolucion() == null) {
            throw new SQLException("Datos del préstamo incompletos.");
        }

        if (prestamo.getFechaDevolucion()
                .isBefore(prestamo.getFechaPrestamo())) {
            throw new SQLException(
                    "La fecha de devolución no puede ser anterior al préstamo.");
        }

        if (prestamo.isDevuelto()) {
            throw new SQLException(
                    "Un préstamo nuevo no puede estar devuelto.");
        }

        String descontarStock = "UPDATE libros SET stock = stock - 1 " +
                "WHERE id = ? AND stock > 0";

        String insertarPrestamo = "INSERT INTO prestamos " +
                "(id_estudiante, id_libro, fecha_prestamo, " +
                "fecha_devolucion, devuelto) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection()) {

            conexion.setAutoCommit(false);

            try {
                // Verificar y descontar el stock disponible
                try (PreparedStatement ps =
                             conexion.prepareStatement(descontarStock)) {

                    ps.setInt(1, prestamo.getIdLibro());

                    if (ps.executeUpdate() == 0) {
                        throw new SQLException(
                                "El libro no existe o no tiene stock disponible.");
                    }
                }

                // Registrar el préstamo
                try (PreparedStatement ps =
                             conexion.prepareStatement(insertarPrestamo)) {

                    ps.setInt(1, prestamo.getIdEstudiante());
                    ps.setInt(2, prestamo.getIdLibro());
                    ps.setDate(3, Date.valueOf(
                            prestamo.getFechaPrestamo()));
                    ps.setDate(4, Date.valueOf(
                            prestamo.getFechaDevolucion()));
                    ps.setBoolean(5, false);

                    if (ps.executeUpdate() == 0) {
                        throw new SQLException(
                                "No se pudo registrar el préstamo.");
                    }
                }

                conexion.commit();

            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        }
    }

    @Override
    public List<Prestamo> listar() throws SQLException {

        List<Prestamo> prestamos = new ArrayList<>();

        String sql = "SELECT * FROM prestamos";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Prestamo prestamo = new Prestamo(
                        rs.getInt("id"),
                        rs.getInt("id_estudiante"),
                        rs.getInt("id_libro"),
                        rs.getDate("fecha_prestamo").toLocalDate(),
                        rs.getDate("fecha_devolucion").toLocalDate(),
                        rs.getBoolean("devuelto")
                );

                prestamos.add(prestamo);
            }
        }

        return prestamos;
    }

    @Override
    public boolean actualizar(Prestamo prestamo)
            throws SQLException {

        if (prestamo == null || prestamo.getId() <= 0) {
            throw new SQLException("Préstamo no válido.");
        }

        if (prestamo.getFechaPrestamo() == null ||
                prestamo.getFechaDevolucion() == null ||
                prestamo.getFechaDevolucion()
                        .isBefore(prestamo.getFechaPrestamo())) {
            throw new SQLException("Fechas del préstamo no válidas.");
        }

        // Solo se actualizan fechas para proteger la consistencia
        // de estudiantes, libros y stock.
        String sql = "UPDATE prestamos SET fecha_prestamo = ?, " +
                "fecha_devolucion = ? WHERE id = ? AND devuelto = FALSE";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(
                    prestamo.getFechaPrestamo()));
            ps.setDate(2, Date.valueOf(
                    prestamo.getFechaDevolucion()));
            ps.setInt(3, prestamo.getId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {

        if (id <= 0) {
            throw new SQLException("ID de préstamo no válido.");
        }

        // No eliminamos préstamos para conservar el historial.
        throw new SQLException(
                "Los préstamos no se eliminan. " +
                        "Utilice la opción de devolución.");
    }

    public synchronized boolean devolver(int id)
            throws SQLException {

        if (id <= 0) {
            throw new SQLException("ID de préstamo no válido.");
        }

        String buscar = "SELECT id_libro FROM prestamos " +
                "WHERE id = ? AND devuelto = FALSE FOR UPDATE";

        String actualizar = "UPDATE prestamos SET devuelto = TRUE " +
                "WHERE id = ? AND devuelto = FALSE";

        String devolverStock = "UPDATE libros SET stock = stock + 1 " +
                "WHERE id = ?";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection()) {

            conexion.setAutoCommit(false);

            try {
                int idLibro;

                try (PreparedStatement ps =
                             conexion.prepareStatement(buscar)) {

                    ps.setInt(1, id);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conexion.rollback();
                            return false;
                        }

                        idLibro = rs.getInt("id_libro");
                    }
                }

                try (PreparedStatement ps =
                             conexion.prepareStatement(actualizar)) {

                    ps.setInt(1, id);

                    if (ps.executeUpdate() == 0) {
                        throw new SQLException(
                                "No se pudo registrar la devolución.");
                    }
                }

                try (PreparedStatement ps =
                             conexion.prepareStatement(devolverStock)) {

                    ps.setInt(1, idLibro);

                    if (ps.executeUpdate() == 0) {
                        throw new SQLException(
                                "No se pudo actualizar el stock.");
                    }
                }

                conexion.commit();
                return true;

            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        }
    }
}
