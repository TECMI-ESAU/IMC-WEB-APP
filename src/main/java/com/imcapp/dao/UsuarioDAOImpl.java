package com.imcapp.dao;

import com.imcapp.model.Usuario;
import com.imcapp.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioDAOImpl implements IUsuarioDAO {

    private static final Logger LOGGER = Logger.getLogger(UsuarioDAOImpl.class.getName());

    @Override
    public Usuario autenticar(String username, String password) {
        String sql = "SELECT id_usuario, nombre_completo, username, password_hash, edad, sexo, estatura, fecha_registro " +
                     "FROM usuarios WHERE username = ? AND password_hash = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
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
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error en autenticación de usuario", e);
        } finally {
            ConexionBD.close(rs, ps, conn);
        }
        return null;
    }

    @Override
    public boolean registrar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre_completo, username, password_hash, edad, sexo, estatura) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.getInstance().getConnection();
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
                    usuario.setIdUsuario(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al registrar usuario en BD", e);
        } finally {
            ConexionBD.close(rs, ps, conn);
        }
        return false;
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
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener usuario por ID", e);
        } finally {
            ConexionBD.close(rs, ps, conn);
        }
        return null;
    }

    @Override
    public boolean existeUsername(String username) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE username = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al validar existencia de username", e);
        } finally {
            ConexionBD.close(rs, ps, conn);
        }
        return false;
    }
}
