package com.imcapp.controller;

import com.imcapp.dao.IRegistroImcDAO;
import com.imcapp.dao.RegistroImcDAOImpl;
import com.imcapp.model.RegistroIMC;
import com.imcapp.model.Usuario;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class HistorialServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private IRegistroImcDAO registroDAO;

    @Override
    public void init() throws ServletException {
        this.registroDAO = new RegistroImcDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;
        if (usuario == null && session != null) {
            usuario = (Usuario) session.getAttribute("usuarioLogueado");
        }

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        List<RegistroIMC> historial = registroDAO.listarPorUsuario(usuario.getIdUsuario());
        RegistroIMC ultimo = registroDAO.obtenerUltimoRegistro(usuario.getIdUsuario());

        request.setAttribute("historial", historial);
        request.setAttribute("historico", historial);
        request.setAttribute("ultimoCalculo", ultimo);
        request.getRequestDispatcher("/historial.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
