<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error del Sistema - Calculadora IMC</title>
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
                <li><a href="${pageContext.request.contextPath}/calculadora">Ir al Inicio</a></li>
            </ul>
        </div>
    </header>

    <div class="container">
        <div class="auth-wrapper" style="max-width: 600px;">
            <div class="card" style="border-top: 4px solid #dc2626;">
                <div class="card-header">
                    <h1 class="card-title" style="color: #dc2626;">Ha Ocurrido un Inconveniente</h1>
                    <p class="card-subtitle">El servidor encontró un error al procesar la solicitud</p>
                </div>

                <div class="alert alert-danger">
                    <strong>Detalle del Error:</strong>
                    <p>${pageContext.errorData.statusCode != 0 ? pageContext.errorData.statusCode : 'Error inesperado'}: 
                       ${pageContext.errorData.throwable != null ? pageContext.errorData.throwable.message : 'Solicitud no pudo ser completada'}</p>
                </div>

                <div class="text-center mt-4">
                    <a href="${pageContext.request.contextPath}/calculadora" class="btn btn-primary" style="margin-bottom: 0.5rem;">
                        Volver a la Calculadora
                    </a>
                    <a href="${pageContext.request.contextPath}/auth?action=login" class="btn btn-secondary">
                        Ir al Acceso de Usuario
                    </a>
                </div>
            </div>
        </div>
    </div>

    <footer>
        <p>Computación Avanzada en Java &copy; Tecmilenio - Arquitectura MVC & JAX-RS RESTful</p>
    </footer>

</body>
</html>
