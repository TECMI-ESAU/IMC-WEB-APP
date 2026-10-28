package com.imcapp.dao;

import com.imcapp.model.Usuario;
import com.imcapp.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioDAOImpl implements IUsuarioDAO {

    private static final Logger LOGGER = Logger.getLogger(UsuarioDAOImpl.class.getName());

    // Almacenamiento en memoria de respaldo resiliente (Fallback)
    private static final Map<String, Usuario> USUARIOS_POR_USERNAME = new ConcurrentHashMap<>();
    private static final Map<Integer, Usuario> USUARIOS_POR_ID = new ConcurrentHashMap<>();
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(10);

    static {
        // Usuarios iniciales precargados de prueba
        Usuario u1 = new Usuario(1, "Juan Perez Garcia", "jperez", "admin123", 28, "M", 1.75, new Date());
        Usuario u2 = new Usuario(2, "Maria Lopez Silva", "mlopez", "admin123", 24, "F", 1.62, new Date());
        Usuario u3 = new Usuario(3, "Carlos Garcia Ruiz", "cgarcia", "admin123", 45, "M", 1.80, new Date());
        
        USUARIOS_POR_USERNAME.put(u1.getUsername().toLowerCase(), u1);
        USUARIOS_POR_USERNAME.put(u2.getUsername().toLowerCase(), u2);
        USUARIOS_POR_USERNAME.put(u3.getUsername().toLowerCase(), u3);
        USUARIOS_POR_ID.put(1, u1);
        USUARIOS_POR_ID.put(2, u2);
        USUARIOS_POR_ID.put(3, u3);
    }

    @Override
    public Usuario autenticar(String username, String password) {
        if (username == null || password == null) return null;

        String sql = "SELECT id_usuario, nombre_completo, username, password_hash, edad, sexo, estatura, fecha_registro " +
                     "FROM usuarios WHERE username = ? AND password_hash = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.getInstance().getConnection();
            if (conn != null) {
                ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, password);
                rs = ps.executeQuery();
                if (rs.next()) {
                    Usuario u = new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("nombre_completo"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getInt("edad"),
                            rs.getString("sexo"),
                            rs.getDouble("estatura"),
                            rs.getTimestamp("fecha_registro")
                    );
                    USUARIOS_POR_USERNAME.put(u.getUsername().toLowerCase(), u);
                    USUARIOS_POR_ID.put(u.getIdUsuario(), u);
                    return u;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Aviso en autenticación JDBC (usando fallback en memoria): " + e.getMessage());
        } finally {
            ConexionBD.close(rs, ps, conn);
        }

        // Respaldo en memoria
        Usuario enMemoria = USUARIOS_POR_USERNAME.get(username.trim().toLowerCase());
        if (enMemoria != null && password.equals(enMemoria.getPasswordHash())) {
            return enMemoria;
        }

        return null;
    }

    @Override
    public boolean registrar(Usuario usuario) {
        if (usuario == null) return false;

        // Registrar en memoria inmediatamente para garantizar disponibilidad
        if (usuario.getIdUsuario() <= 0) {
            usuario.setIdUsuario(ID_GENERATOR.incrementAndGet());
        }
        if (usuario.getFechaRegistro() == null) {
            usuario.setFechaRegistro(new Date());
        }
        USUARIOS_POR_USERNAME.put(usuario.getUsername().toLowerCase(), usuario);
        USUARIOS_POR_ID.put(usuario.getIdUsuario(), usuario);

        String sql = "INSERT INTO usuarios (nombre_completo, username, password_hash, edad, sexo, estatura) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.getInstance().getConnection();
            if (conn != null) {
                ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, usuario.getNombreCompleto());
                ps.setString(2, usuario.getUsername());
                ps.setString(3, usuario.getPasswordHash());
                ps.setInt(4, usuario.getEdad());
                ps.setString(5, usuario.getSexo());
                ps.setDouble(6, usuario.getEstatura());

                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        usuario.setIdUsuario(generatedId);
                        USUARIOS_POR_ID.put(generatedId, usuario);
                    }
                    LOGGER.info("Usuario registrado exitosamente en Base de Datos: " + usuario.getUsername());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Aviso al registrar en BD (usuario guardado en memoria activa): " + e.getMessage());
        } finally {
            ConexionBD.close(rs, ps, conn);
        }

        return true;
    }

    @Override
    public Usuario obtenerPorId(int idUsuario) {
        String sql = "SELECT id_usuario, nombre_completo, username, password_hash, edad, sexo, estatura, fecha_registro " +
                     "FROM usuarios WHERE id_usuario = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.getInstance().getConnection();
            if (conn != null) {
                ps = conn.prepareStatement(sql);
                ps.setInt(1, idUsuario);
                rs = ps.executeQuery();
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("nombre_completo"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getInt("edad"),
                            rs.getString("sexo"),
                            rs.getDouble("estatura"),
                            rs.getTimestamp("fecha_registro")
                    );
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Aviso al consultar usuario por ID en BD: " + e.getMessage());
        } finally {
            ConexionBD.close(rs, ps, conn);
        }
        return USUARIOS_POR_ID.get(idUsuario);
    }

    @Override
    public boolean existeUsername(String username) {
        if (username == null) return false;

        String sql = "SELECT COUNT(*) FROM usuarios WHERE username = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.getInstance().getConnection();
            if (conn != null) {
                ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Aviso al validar username en BD: " + e.getMessage());
        } finally {
            ConexionBD.close(rs, ps, conn);
        }

        return USUARIOS_POR_USERNAME.containsKey(username.trim().toLowerCase());
    }
}
