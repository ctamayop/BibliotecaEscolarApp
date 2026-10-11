package cl.biblioteca.service;

import cl.biblioteca.dao.impl.PrestamoDaoImpl;
import cl.biblioteca.model.Prestamo;

import java.sql.SQLException;
import java.time.LocalDate;

public class PrestamoService {

    private final PrestamoDaoImpl prestamoDao;

    public PrestamoService() {
        this.prestamoDao = new PrestamoDaoImpl();
    }

    public void registrarPrestamo(int idEstudiante, int idLibro)
            throws SQLException {

        if (idEstudiante <= 0 || idLibro <= 0) {
            throw new SQLException(
                    "Debe seleccionar un estudiante y un libro válidos.");
        }

        LocalDate fechaPrestamo = LocalDate.now();
        LocalDate fechaVencimiento = fechaPrestamo.plusDays(7);

        Prestamo prestamo = new Prestamo(
                0,
                idEstudiante,
                idLibro,
                fechaPrestamo,
                fechaVencimiento,
                false
        );

        prestamoDao.crear(prestamo);
    }

    public boolean registrarDevolucion(int idPrestamo)
            throws SQLException {

        return prestamoDao.devolver(idPrestamo);
    }

    public boolean estaAtrasado(Prestamo prestamo) {

        return !prestamo.isDevuelto()
                && LocalDate.now().isAfter(
                prestamo.getFechaDevolucion());
    }
}
