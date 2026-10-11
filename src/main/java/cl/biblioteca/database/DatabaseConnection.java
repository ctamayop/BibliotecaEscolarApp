package cl.biblioteca.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static DatabaseConnection instancia;

    private final String url =
            "jdbc:mysql://localhost:3306/biblioteca";

    private DatabaseConnection() {
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instancia == null) {
            instancia = new DatabaseConnection();
        }
        return instancia;
    }

    public Connection getConnection() throws SQLException {

        String usuario = System.getenv("DB_USER");
        String contrasena = System.getenv("DB_PASSWORD");

        if (usuario == null || contrasena == null) {
            throw new SQLException(
                    "Faltan las variables de entorno de MySQL."
            );
        }

        return DriverManager.getConnection(
                url, usuario, contrasena
        );
    }
}
