package com.imcapp.controller;

import com.imcapp.dao.IUsuarioDAO;
import com.imcapp.dao.UsuarioDAOImpl;
import com.imcapp.model.Usuario;
import com.imcapp.service.IImcCalculatorService;
import com.imcapp.service.ImcCalculatorServiceImpl;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private IUsuarioDAO usuarioDAO;
    private IImcCalculatorService imcService;

    @Override
    public void init() throws ServletException {
        this.usuarioDAO = new UsuarioDAOImpl();
        this.imcService = new ImcCalculatorServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("logout".equalsIgnoreCase(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login.jsp?mensaje=Sesion+cerrada+correctamente");
        } else if ("registro".equalsIgnoreCase(action)) {
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("login".equalsIgnoreCase(action)) {
            procesarLogin(request, response);
        } else if ("registro".equalsIgnoreCase(action)) {
            procesarRegistro(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }

    private void procesarLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Debe ingresar usuario y contraseña.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        Usuario usuario = usuarioDAO.autenticar(username.trim(), password.trim());
        if (usuario != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("usuario", usuario);
            session.setAttribute("usuarioLogueado", usuario);
            response.sendRedirect(request.getContextPath() + "/calculadora.jsp");
        } else {
            request.setAttribute("error", "Credenciales incorrectas. Verifique su usuario y contraseña.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private void procesarRegistro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nombreCompleto = request.getParameter("nombreCompleto");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String edadStr = request.getParameter("edad");
        String sexo = request.getParameter("sexo");
        String estaturaStr = request.getParameter("estatura");

        // Validaciones de presencia
        if (nombreCompleto == null || username == null || password == null || 
            edadStr == null || sexo == null || estaturaStr == null ||
            nombreCompleto.trim().isEmpty() || username.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("error", "Todos los campos del registro son obligatorios.");
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
            return;
        }

        int edad;
        double estatura;
        try {
            edad = Integer.parseInt(edadStr.trim());
            estatura = Double.parseDouble(estaturaStr.trim());
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Edad o estatura con formato numérico inválido.");
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
            return;
        }

        // Regla: No menores a 15 años
        if (!imcService.validarEdad(edad)) {
            request.setAttribute("error", "No se permiten edades menores a 15 años.");
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
            return;
        }

        // Regla: Estatura entre 1.0m y 2.5m
        if (!imcService.validarEstatura(estatura)) {
            request.setAttribute("error", "La estatura debe estar en el rango de 1.0 m a 2.5 m.");
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
            return;
        }

        // Regla: Nombre de usuario único
        if (usuarioDAO.existeUsername(username.trim())) {
            request.setAttribute("error", "El nombre de usuario '" + username + "' ya está registrado. Elija otro.");
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
            return;
        }

        Usuario nuevoUsuario = new Usuario(nombreCompleto.trim(), username.trim(), password.trim(), edad, sexo.trim(), estatura);
        boolean registrado = usuarioDAO.registrar(nuevoUsuario);

        if (registrado) {
            Usuario usuarioCreado = usuarioDAO.autenticar(username.trim(), password.trim());
            HttpSession session = request.getSession(true);
            session.setAttribute("usuario", usuarioCreado != null ? usuarioCreado : nuevoUsuario);
            session.setAttribute("usuarioLogueado", usuarioCreado != null ? usuarioCreado : nuevoUsuario);
            response.sendRedirect(request.getContextPath() + "/calculadora.jsp?mensaje=Registro+completado+con+exito");
        } else {
            request.setAttribute("error", "Error interno al guardar en base de datos. Intente nuevamente.");
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
        }
    }
}
