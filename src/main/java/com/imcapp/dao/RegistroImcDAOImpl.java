package com.imcapp.dao;

import com.imcapp.model.RegistroIMC;
import com.imcapp.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegistroImcDAOImpl implements IRegistroImcDAO {

    private static final Logger LOGGER = Logger.getLogger(RegistroImcDAOImpl.class.getName());

    @Override
    public boolean insertarRegistro(RegistroIMC registro) {
        String sql = "INSERT INTO registros_imc (id_usuario, masa_corporal, estatura, valor_imc, clasificacion, fecha_medicion) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.getInstance().getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, registro.getIdUsuario());
            ps.setDouble(2, registro.getMasaCorporal());
            ps.setDouble(3, registro.getEstatura());
            ps.setDouble(4, registro.getValorImc());
            ps.setString(5, registro.getClasificacion());

            Timestamp ts = (registro.getFechaMedicion() != null) 
                    ? new Timestamp(registro.getFechaMedicion().getTime()) 
                    : new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(6, ts);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    registro.setIdRegistro(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al insertar registro de IMC", e);
        } finally {
            ConexionBD.close(rs, ps, conn);
        }
        return false;
    }

    @Override
    public List<RegistroIMC> listarPorUsuario(int idUsuario) {
        List<RegistroIMC> lista = new ArrayList<>();
        String sql = "SELECT id_registro, id_usuario, masa_corporal, estatura, valor_imc, clasificacion, fecha_medicion " +
                     "FROM registros_imc WHERE id_usuario = ? ORDER BY fecha_medicion DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();
            while (rs.next()) {
                RegistroIMC reg = new RegistroIMC(
                        rs.getInt("id_registro"),
                        rs.getInt("id_usuario"),
                        rs.getDouble("masa_corporal"),
                        rs.getDouble("estatura"),
                        rs.getDouble("valor_imc"),
                        rs.getString("clasificacion"),
                        rs.getTimestamp("fecha_medicion")
                );
                lista.add(reg);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar registros de IMC para usuario: " + idUsuario, e);
        } finally {
            ConexionBD.close(rs, ps, conn);
        }
        return lista;
    }

    @Override
    public RegistroIMC obtenerUltimoRegistro(int idUsuario) {
        String sql = "SELECT id_registro, id_usuario, masa_corporal, estatura, valor_imc, clasificacion, fecha_medicion " +
                     "FROM registros_imc WHERE id_usuario = ? ORDER BY fecha_medicion DESC LIMIT 1";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new RegistroIMC(
                        rs.getInt("id_registro"),
                        rs.getInt("id_usuario"),
                        rs.getDouble("masa_corporal"),
                        rs.getDouble("estatura"),
                        rs.getDouble("valor_imc"),
                        rs.getString("clasificacion"),
                        rs.getTimestamp("fecha_medicion")
                );
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener último registro de IMC", e);
        } finally {
            ConexionBD.close(rs, ps, conn);
        }
        return null;
    }
}
