<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro de Usuario - Calculadora IMC</title>
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
                <li><a href="${pageContext.request.contextPath}/login.jsp">Iniciar Sesión</a></li>
            </ul>
        </div>
    </header>

    <div class="container">
        <div class="auth-wrapper" style="max-width: 540px;">
            <div class="card">
                <div class="card-header">
                    <h1 class="card-title">Registro Único de Usuario</h1>
                    <p class="card-subtitle">Completa tus datos biométricos una sola vez para tu seguimiento</p>
                </div>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">
                        <strong>Error:</strong> <c:out value="${error}"/>
                    </div>
                </c:if>
                <c:if test="${empty error and not empty param.error}">
                    <div class="alert alert-danger">
                        <strong>Error:</strong> <c:out value="${param.error}"/>
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/auth" method="POST" autocomplete="off">
                    <input type="hidden" name="action" value="registro">

                    <div class="form-group">
                        <label for="nombreCompleto" class="form-label">Nombre Completo:</label>
                        <input type="text" id="nombreCompleto" name="nombreCompleto" class="form-control" 
                               value="${param.nombreCompleto}" required autofocus placeholder="Ej. Juan Pérez García">
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="username" class="form-label">Nombre de Usuario:</label>
                            <input type="text" id="username" name="username" class="form-control" 
                                   value="${param.username}" required placeholder="Ej. jperez">
                            <small class="form-text">Debe ser único</small>
                        </div>

                        <div class="form-group">
                            <label for="password" class="form-label">Contraseña:</label>
                            <input type="password" id="password" name="password" class="form-control" 
                                   required placeholder="Mínimo 6 caracteres">
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="edad" class="form-label">Edad (años):</label>
                            <input type="number" id="edad" name="edad" class="form-control" 
                                   min="15" max="120" value="${empty param.edad ? '25' : param.edad}" required>
                            <small class="form-text">Mínimo 15 años</small>
                        </div>

                        <div class="form-group">
                            <label for="sexo" class="form-label">Sexo:</label>
                            <select id="sexo" name="sexo" class="form-control" required>
                                <option value="M" ${param.sexo == 'M' ? 'selected' : ''}>Masculino</option>
                                <option value="F" ${param.sexo == 'F' ? 'selected' : ''}>Femenino</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="estatura" class="form-label">Estatura fija (metros):</label>
                        <input type="number" id="estatura" name="estatura" class="form-control" 
                               step="0.01" min="1.00" max="2.50" value="${empty param.estatura ? '1.75' : param.estatura}" required placeholder="Ej. 1.75">
                        <small class="form-text">Rango válido: 1.00 m a 2.50 m (Se guardará como tu estatura base)</small>
                    </div>

                    <div class="form-group" style="margin-top: 1.5rem;">
                        <button type="submit" class="btn btn-primary">Registrar Cuenta</button>
                    </div>
                </form>

                <div class="text-center mt-4">
                    <p class="form-text">¿Ya tienes una cuenta? 
                        <a href="${pageContext.request.contextPath}/login.jsp" style="color: var(--primary); font-weight: 600;">Iniciar Sesión</a>
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
