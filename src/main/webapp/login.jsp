<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar Sesión - Calculadora IMC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>

    <header>
        <div class="navbar">
            <a href="${pageContext.request.contextPath}/" class="brand">
                <span class="brand-badge">Java EE</span>
                <span>Calculadora IMC</span>
            </a>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/registro.jsp">Registrarse</a></li>
            </ul>
        </div>
    </header>

    <div class="container">
        <div class="auth-wrapper">
            <div class="card">
                <div class="card-header">
                    <h1 class="card-title">Acceso al Sistema</h1>
                    <p class="card-subtitle">Inicia sesión para calcular y registrar tu IMC</p>
                </div>

                <c:if test="${not empty param.error}">
                    <div class="alert alert-danger">
                        <strong>Error:</strong> <c:out value="${param.error}"/>
                    </div>
                </c:if>
                <c:if test="${not empty param.registroExitoso}">
                    <div class="alert alert-success">
                        <strong>¡Registro exitoso!</strong> Ahora puedes iniciar sesión con tu cuenta.
                    </div>
                </c:if>
                <c:if test="${not empty param.mensaje}">
                    <div class="alert alert-info">
                        <c:out value="${param.mensaje}"/>
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/auth?action=login" method="POST" autocomplete="off">
                    <div class="form-group">
                        <label for="username" class="form-label">Nombre de Usuario:</label>
                        <input type="text" id="username" name="username" class="form-control" 
                               value="${param.username}" required autofocus placeholder="Ej. jperez">
                    </div>

                    <div class="form-group">
                        <label for="password" class="form-label">Contraseña:</label>
                        <input type="password" id="password" name="password" class="form-control" 
                               required placeholder="Ingresa tu contraseña">
                    </div>

                    <div class="form-group" style="margin-top: 1.5rem;">
                        <button type="submit" class="btn btn-primary">Iniciar Sesión</button>
                    </div>
                </form>

                <div class="text-center mt-4">
                    <p class="form-text">¿No tienes una cuenta registrada? 
                        <a href="${pageContext.request.contextPath}/registro.jsp" style="color: var(--primary); font-weight: 600;">Regístrate aquí</a>
                    </p>
                </div>
            </div>
        </div>
    </div>

    <footer>
        <p>Computación Avanzada en Java &copy; Tecmilenio - Arquitectura MVC & JAX-RS RESTful</p>
    </footer>

</body>
</html>
