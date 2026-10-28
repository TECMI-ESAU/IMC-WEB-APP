package com.imcapp.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * Gestor centralizado de conexiones de Base de Datos.
 * Soporta de manera transparente y resiliente:
 * 1. DataSource JNDI de GlassFish (jdbc/imc_db o jdbc/__default).
 * 2. MySQL Server (localhost:3306/imc_system_db).
 * 3. Java DB / Apache Derby integrado en NetBeans y GlassFish
 * 4. Embedded Derby Database (creación automática en disco).
 * 
 * Auto-inicializa las tablas 'usuarios' y 'registros_imc' si no existen.
 */
public class ConexionBD {

    private static final Logger LOGGER = Logger.getLogger(ConexionBD.class.getName());
    private static ConexionBD instance;
    private DataSource dataSource;
    private boolean tablesVerified = false;

    private static final String JNDI_PRIMARY = "java:comp/env/jdbc/imc_db";
    private static final String JNDI_SECONDARY = "jdbc/imc_db";
    private static final String JNDI_DEFAULT = "jdbc/__default";

    // Configuraciones de fallback
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/imc_system_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASS = "";

    private static final String DERBY_NET_DRIVER = "org.apache.derby.jdbc.ClientDriver";
    private static final String DERBY_NET_URL = "jdbc:derby://localhost:1527/imc_db;create=true";
    private static final String DERBY_USER = "app";
    private static final String DERBY_PASS = "app";

    private static final String DERBY_EMBEDDED_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    private static final String DERBY_EMBEDDED_URL = "jdbc:derby:imc_embedded_db;create=true";

    private ConexionBD() {
        try {
            Context ctx = new InitialContext();
            try {
                dataSource = (DataSource) ctx.lookup(JNDI_PRIMARY);
                LOGGER.info("DataSource JNDI encontrado: " + JNDI_PRIMARY);
            } catch (Exception e1) {
                try {
                    dataSource = (DataSource) ctx.lookup(JNDI_SECONDARY);
                    LOGGER.info("DataSource JNDI encontrado: " + JNDI_SECONDARY);
                } catch (Exception e2) {
                    try {
                        dataSource = (DataSource) ctx.lookup(JNDI_DEFAULT);
                        LOGGER.info("DataSource JNDI por defecto de GlassFish encontrado: " + JNDI_DEFAULT);
                    } catch (Exception e3) {
                        LOGGER.info("No se encontró DataSource JNDI. Se utilizarán drivers JDBC directos.");
                        dataSource = null;
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Error consultando contexto JNDI", ex);
        }
    }

    public static synchronized ConexionBD getInstance() {
        if (instance == null) {
            instance = new ConexionBD();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        Connection conn = null;

        // 1. Intentar DataSource JNDI
        if (dataSource != null) {
            try {
                conn = dataSource.getConnection();
            } catch (SQLException e) {
                LOGGER.log(Level.FINE, "No se pudo obtener conexion del DataSource JNDI: " + e.getMessage());
            }
        }

        // 2. Intentar MySQL Local (si está disponible)
        if (conn == null) {
            try {
                Class.forName(MYSQL_DRIVER);
                conn = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASS);
                LOGGER.info("Conectado exitosamente a MySQL Server.");
            } catch (Exception ignored) {
                // MySQL no está corriendo, continuar al siguiente fallback
            }
        }

        // 3. Intentar Java DB / Apache Derby Network (Servidor de GlassFish en puerto 1527)
        if (conn == null) {
            try {
                Class.forName(DERBY_NET_DRIVER);
                conn = DriverManager.getConnection(DERBY_NET_URL, DERBY_USER, DERBY_PASS);
                LOGGER.info("Conectado exitosamente a Java DB / Apache Derby Network (puerto 1527).");
            } catch (Exception ignored) {
                // Derby red no respondió, intentar embebido
            }
        }

        // 4. Intentar Apache Derby Embebido (Creación local garantizada)
        if (conn == null) {
            try {
                Class.forName(DERBY_EMBEDDED_DRIVER);
                conn = DriverManager.getConnection(DERBY_EMBEDDED_URL, DERBY_USER, DERBY_PASS);
                LOGGER.info("Conectado exitosamente a Apache Derby Embebido.");
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error crítico: no se pudo establecer conexión con ningún manejador de base de datos.", ex);
                throw new SQLException("No se pudo conectar a ninguna base de datos disponible (MySQL, Derby Network o Embedded).", ex);
            }
        }

        // Inicializar esquema si es la primera conexión
        if (conn != null && !tablesVerified) {
            verificarYCrearTablas(conn);
            tablesVerified = true;
        }

        return conn;
    }

    private void verificarYCrearTablas(Connection conn) {
        Statement stmt = null;
        try {
            DatabaseMetaData meta = conn.getMetaData();
            String dbName = meta.getDatabaseProductName().toLowerCase();

            boolean isDerby = dbName.contains("derby");
            stmt = conn.createStatement();

            if (isDerby) {
                // Verificar si existe tabla USUARIOS
                ResultSet rsTables = meta.getTables(null, null, "USUARIOS", null);
                if (!rsTables.next()) {
                    LOGGER.info("Creando tablas en Apache Derby...");
                    stmt.executeUpdate(
                        "CREATE TABLE usuarios (" +
                        "  id_usuario INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY," +
                        "  nombre_completo VARCHAR(100) NOT NULL," +
                        "  username VARCHAR(50) NOT NULL UNIQUE," +
                        "  password_hash VARCHAR(255) NOT NULL," +
                        "  edad INT NOT NULL," +
                        "  sexo VARCHAR(5) NOT NULL," +
                        "  estatura DOUBLE NOT NULL," +
                        "  fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ")"
                    );

                    stmt.executeUpdate(
                        "CREATE TABLE registros_imc (" +
                        "  id_registro INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY," +
                        "  id_usuario INT NOT NULL," +
                        "  masa_corporal DOUBLE NOT NULL," +
                        "  estatura DOUBLE NOT NULL," +
                        "  valor_imc DOUBLE NOT NULL," +
                        "  clasificacion VARCHAR(50) NOT NULL," +
                        "  fecha_medicion TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "  CONSTRAINT fk_usuario_imc FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE" +
                        ")"
                    );

                    // Insertar usuario de prueba
                    stmt.executeUpdate(
                        "INSERT INTO usuarios (nombre_completo, username, password_hash, edad, sexo, estatura) " +
                        "VALUES ('Juan Perez Garcia', 'jperez', 'admin123', 28, 'M', 1.75)"
                    );
                    LOGGER.info("Tablas creadas y usuario inicial 'jperez' cargado en Derby.");
                }
            } else {
                // MySQL
                stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS usuarios (" +
                    "  id_usuario INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "  nombre_completo VARCHAR(100) NOT NULL," +
                    "  username VARCHAR(50) NOT NULL UNIQUE," +
                    "  password_hash VARCHAR(255) NOT NULL," +
                    "  edad INT NOT NULL," +
                    "  sexo ENUM('M', 'F') NOT NULL," +
                    "  estatura DECIMAL(4,2) NOT NULL," +
                    "  fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ") ENGINE=InnoDB"
                );

                stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS registros_imc (" +
                    "  id_registro INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "  id_usuario INT NOT NULL," +
                    "  masa_corporal DECIMAL(5,2) NOT NULL," +
                    "  estatura DECIMAL(4,2) NOT NULL," +
                    "  valor_imc DECIMAL(5,2) NOT NULL," +
                    "  clasificacion VARCHAR(50) NOT NULL," +
                    "  fecha_medicion TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "  CONSTRAINT fk_usuario_imc FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB"
                );
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Aviso durante la verificación/creación de tablas: " + e.getMessage());
        } finally {
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public static void close(ResultSet rs, PreparedStatement ps, Connection conn) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException ignored) {}
        }
        if (ps != null) {
            try { ps.close(); } catch (SQLException ignored) {}
        }
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }
}
