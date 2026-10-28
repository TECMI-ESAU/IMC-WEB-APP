package com.imcapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ConexionBD {

    private static final Logger LOGGER = Logger.getLogger(ConexionBD.class.getName());
    private static ConexionBD instance;
    private DataSource dataSource;

    private static final String JNDI_PRIMARY = "java:comp/env/jdbc/imc_db";
    private static final String JNDI_SECONDARY = "jdbc/imc_db";

    // Credenciales de conexión directa de respaldo (Fallback)
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/imc_system_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "admin123";

    private ConexionBD() {
        try {
            Context ctx = new InitialContext();
            try {
                dataSource = (DataSource) ctx.lookup(JNDI_PRIMARY);
                LOGGER.info("DataSource JNDI encontrado en: " + JNDI_PRIMARY);
            } catch (NamingException e1) {
                try {
                    dataSource = (DataSource) ctx.lookup(JNDI_SECONDARY);
                    LOGGER.info("DataSource JNDI encontrado en: " + JNDI_SECONDARY);
                } catch (NamingException e2) {
                    LOGGER.log(Level.WARNING, "No se encontró DataSource JNDI en GlassFish. Se usará conexión JDBC directa.");
                    dataSource = null;
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error inicializando contexto JNDI", ex);
        }
    }

    public static synchronized ConexionBD getInstance() {
        if (instance == null) {
            instance = new ConexionBD();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (dataSource != null) {
            try {
                return dataSource.getConnection();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Fallo al obtener conexión de DataSource. Intentando conexión directa...", e);
            }
        }
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Driver MySQL no encontrado en el classpath.", e);
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public static void close(ResultSet rs, PreparedStatement ps, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {}
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException ignored) {}
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {}
        }
    }
}
