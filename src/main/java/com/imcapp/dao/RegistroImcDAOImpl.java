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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegistroImcDAOImpl implements IRegistroImcDAO {

    private static final Logger LOGGER = Logger.getLogger(RegistroImcDAOImpl.class.getName());

    // Almacenamiento en memoria de respaldo resiliente (Fallback)
    private static final Map<Integer, List<RegistroIMC>> REGISTROS_POR_USUARIO = new ConcurrentHashMap<>();
    private static final Map<Integer, RegistroIMC> REGISTROS_POR_ID = new ConcurrentHashMap<>();
    private static final AtomicInteger REG_ID_GENERATOR = new AtomicInteger(100);

    static {
        // Cargar registros de prueba iniciales
        RegistroIMC r1 = new RegistroIMC(1, 1, 70.0, 1.75, 22.86, "Peso normal", new Date(System.currentTimeMillis() - 86400000L * 15));
        RegistroIMC r2 = new RegistroIMC(2, 1, 72.5, 1.75, 23.67, "Peso normal", new Date());
        
        List<RegistroIMC> listaU1 = new CopyOnWriteArrayList<>();
        listaU1.add(r2);
        listaU1.add(r1);
        REGISTROS_POR_USUARIO.put(1, listaU1);
        REGISTROS_POR_ID.put(1, r1);
        REGISTROS_POR_ID.put(2, r2);
    }

    @Override
    public boolean insertarRegistro(RegistroIMC registro) {
        if (registro == null) return false;

        if (registro.getIdRegistro() <= 0) {
            registro.setIdRegistro(REG_ID_GENERATOR.incrementAndGet());
        }
        if (registro.getFechaMedicion() == null) {
            registro.setFechaMedicion(new Date());
        }

        // Guardar en memoria inmediatamente
        REGISTROS_POR_ID.put(registro.getIdRegistro(), registro);
        REGISTROS_POR_USUARIO.computeIfAbsent(registro.getIdUsuario(), k -> new CopyOnWriteArrayList<>()).add(0, registro);

        String sql = "INSERT INTO registros_imc (id_usuario, masa_corporal, estatura, valor_imc, clasificacion, fecha_medicion) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.getInstance().getConnection();
            if (conn != null) {
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
                        int genId = rs.getInt(1);
                        registro.setIdRegistro(genId);
                        REGISTROS_POR_ID.put(genId, registro);
                    }
                    LOGGER.info("Registro IMC guardado exitosamente en BD para usuario ID: " + registro.getIdUsuario());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Aviso al insertar en BD (medición guardada en memoria activa): " + e.getMessage());
        } finally {
            ConexionBD.close(rs, ps, conn);
        }

        return true;
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
            if (conn != null) {
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
                if (!lista.isEmpty()) {
                    return lista;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Aviso al listar registros de IMC en BD: " + e.getMessage());
        } finally {
            ConexionBD.close(rs, ps, conn);
        }

        List<RegistroIMC> enMemoria = REGISTROS_POR_USUARIO.get(idUsuario);
        return (enMemoria != null) ? new ArrayList<>(enMemoria) : Collections.emptyList();
    }

    @Override
    public RegistroIMC obtenerUltimoRegistro(int idUsuario) {
        List<RegistroIMC> lista = listarPorUsuario(idUsuario);
        return (!lista.isEmpty()) ? lista.get(0) : null;
    }

    @Override
    public RegistroIMC obtenerPorId(int idRegistro) {
        String sql = "SELECT id_registro, id_usuario, masa_corporal, estatura, valor_imc, clasificacion, fecha_medicion " +
                     "FROM registros_imc WHERE id_registro = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.getInstance().getConnection();
            if (conn != null) {
                ps = conn.prepareStatement(sql);
                ps.setInt(1, idRegistro);
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
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Aviso al obtener registro por ID en BD: " + e.getMessage());
        } finally {
            ConexionBD.close(rs, ps, conn);
        }

        return REGISTROS_POR_ID.get(idRegistro);
    }
}
