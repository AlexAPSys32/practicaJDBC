package es.dam.di.infrastructure;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Punto único de acceso a la base de datos H2 (modo fichero).
 * El fichero se crea en ./data/musica.mv.db, relativo a la carpeta del proyecto.
 */
public final class BaseDatos {

    private static final String URL = "jdbc:h2:./data/musica";
    private static final String USUARIO = "sa";
    private static final String PASSWORD = "";
    private static final String SCRIPT_ESQUEMA = "/schema.sql";

    private BaseDatos() {
        // Clase de utilidad: no se instancia
    }

    /**
     * Abre una conexión nueva. Quien la pida es responsable de cerrarla.
     */
    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }

    /**
     * Crea las tablas si no existen. Llamar una vez al arrancar la aplicación.
     */
    public static void inicializar() {
        String script = leerScript();
        try (Connection conexion = getConexion();
             Statement sentencia = conexion.createStatement()) {
            sentencia.execute(script);
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo ejecutar el esquema", e);
        }
    }

    private static String leerScript() {
        try (InputStream in = BaseDatos.class.getResourceAsStream(SCRIPT_ESQUEMA)) {
            if (in == null) {
                throw new IllegalStateException("No se encuentra " + SCRIPT_ESQUEMA + " en resources");
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + SCRIPT_ESQUEMA, e);
        }
    }
}