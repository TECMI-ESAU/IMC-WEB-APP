package com.imcapp.dao;

import com.imcapp.model.Usuario;

public interface IUsuarioDAO {
    Usuario autenticar(String username, String password);
    boolean registrar(Usuario usuario);
    Usuario obtenerPorId(int idUsuario);
    boolean existeUsername(String username);
}
