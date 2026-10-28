package com.imcapp.dao;

import com.imcapp.model.RegistroIMC;
import java.util.List;

public interface IRegistroImcDAO {
    boolean insertarRegistro(RegistroIMC registro);
    List<RegistroIMC> listarPorUsuario(int idUsuario);
    RegistroIMC obtenerUltimoRegistro(int idUsuario);
}
