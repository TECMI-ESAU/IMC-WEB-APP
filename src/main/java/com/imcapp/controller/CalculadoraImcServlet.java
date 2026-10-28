package com.imcapp.controller;

import com.imcapp.dao.IRegistroImcDAO;
import com.imcapp.dao.RegistroImcDAOImpl;
import com.imcapp.model.RegistroIMC;
import com.imcapp.model.ResultadoIMC;
import com.imcapp.model.Usuario;
import com.imcapp.service.IImcCalculatorService;
import com.imcapp.service.ImcCalculatorServiceImpl;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CalculadoraImcServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private IImcCalculatorService imcService;
    private IRegistroImcDAO registroDAO;

    @Override
    public void init() throws ServletException {
        this.imcService = new ImcCalculatorServiceImpl();
        this.registroDAO = new RegistroImcDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/calculadora.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;
        if (usuario == null && session != null) {
            usuario = (Usuario) session.getAttribute("usuarioLogueado");
        }

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?mensaje=Sesion+expirada.+Inicie+sesion+nuevamente.");
            return;
        }

        String masaStr = request.getParameter("masaCorporal");
        if (masaStr == null || masaStr.trim().isEmpty()) {
            masaStr = request.getParameter("masaCorporalKg");
        }

        if (masaStr == null || masaStr.trim().isEmpty()) {
            request.setAttribute("error", "Debe ingresar el valor de su masa corporal.");
            request.getRequestDispatcher("/calculadora.jsp").forward(request, response);
            return;
        }

        double masaCorporal;
        try {
            masaCorporal = Double.parseDouble(masaStr.trim());
        } catch (NumberFormatException e) {
            request.setAttribute("error", "La masa corporal ingresada tiene un formato no válido.");
            request.getRequestDispatcher("/calculadora.jsp").forward(request, response);
            return;
        }

        // Regla: No masas en 0 o negativas
        if (!imcService.validarMasaCorporal(masaCorporal)) {
            request.setAttribute("error", "No se aceptan masas corporales en 0 o negativas.");
            request.getRequestDispatcher("/calculadora.jsp").forward(request, response);
            return;
        }

        double estatura = usuario.getEstatura();
        ResultadoIMC resultado = imcService.generarResultado(masaCorporal, estatura);

        // Guardar registro en histórico de base de datos
        RegistroIMC nuevoRegistro = new RegistroIMC(
                usuario.getIdUsuario(),
                masaCorporal,
                estatura,
                resultado.getValorImc(),
                resultado.getClasificacion().getDescripcion()
        );
        registroDAO.insertarRegistro(nuevoRegistro);
        resultado.setRegistroPersistido(nuevoRegistro);

        request.setAttribute("resultado", resultado);
        request.setAttribute("ultimoRegistro", nuevoRegistro);
        request.getRequestDispatcher("/calculadora.jsp").forward(request, response);
    }
}
