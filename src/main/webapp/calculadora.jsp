<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Calculadora de IMC - Sistema Java EE</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>

    <header>
        <div class="navbar">
            <a href="${pageContext.request.contextPath}/calculadora" class="brand">
                <span class="brand-badge">Java EE</span>
                <span>Calculadora IMC</span>
            </a>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/calculadora" class="active">Calculadora</a></li>
                <li><a href="${pageContext.request.contextPath}/historial">Mi Historial</a></li>
                <li class="user-badge">
                    <span>👤 ${sessionScope.usuarioLogueado.nombreCompleto}</span>
                </li>
                <li><a href="${pageContext.request.contextPath}/auth?action=logout" class="btn-logout">Cerrar Sesión</a></li>
            </ul>
        </div>
    </header>

    <div class="container">
        <!-- Barra de Perfil Biométrico del Usuario Activo -->
        <div class="user-profile-bar">
            <div class="profile-item">
                <small class="text-muted" style="display:block;">Usuario:</small>
                <strong>@<c:out value="${sessionScope.usuarioLogueado.username}"/></strong>
            </div>
            <div class="profile-item">
                <small class="text-muted" style="display:block;">Estatura Registrada:</small>
                <strong><fmt:formatNumber value="${sessionScope.usuarioLogueado.estaturaMetros}" pattern="0.00"/> m</strong>
            </div>
            <div class="profile-item">
                <small class="text-muted" style="display:block;">Edad:</small>
                <strong><c:out value="${sessionScope.usuarioLogueado.edad}"/> años</strong>
            </div>
            <div class="profile-item">
                <small class="text-muted" style="display:block;">Sexo:</small>
                <strong>${sessionScope.usuarioLogueado.sexo == 'M' ? 'Masculino' : 'Femenino'}</strong>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/historial" class="btn btn-secondary btn-sm">Ver Histórico REST</a>
            </div>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">
                <strong>Error:</strong> <c:out value="${error}"/>
            </div>
        </c:if>

        <c:if test="${not empty mensajeExito}">
            <div class="alert alert-success">
                <strong>¡Éxito!</strong> <c:out value="${mensajeExito}"/>
            </div>
        </c:if>

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; align-items: start;">
            <!-- Formulario de Cálculo -->
            <div class="card">
                <div class="card-header" style="text-align: left;">
                    <h2 class="card-title" style="font-size: 1.3rem;">Nueva Medición de Peso</h2>
                    <p class="card-subtitle">Ingresa tu peso actual. Tu estatura base (<fmt:formatNumber value="${sessionScope.usuarioLogueado.estaturaMetros}" pattern="0.00"/> m) se recupera automáticamente de tu sesión.</p>
                </div>

                <form action="${pageContext.request.contextPath}/calculadora" method="POST">
                    <div class="form-group">
                        <label for="masaCorporalKg" class="form-label">Masa Corporal Actual (kg):</label>
                        <input type="number" id="masaCorporalKg" name="masaCorporalKg" class="form-control" 
                               step="0.01" min="1.0" max="500.0" 
                               value="${param.masaCorporalKg != null ? param.masaCorporalKg : ''}" 
                               required autofocus placeholder="Ej. 70.50">
                        <small class="form-text">Debe ser un valor positivo mayor a 0 kg.</small>
                    </div>

                    <button type="submit" class="btn btn-primary" style="margin-top: 1rem;">
                        ⚡ Calcular y Registrar IMC
                    </button>
                </form>

                <div class="mt-4" style="border-top: 1px solid var(--border); padding-top: 1rem;">
                    <small class="text-muted">
                        <strong>Fórmula de Quetelet:</strong> $\text{IMC} = \frac{\text{Masa (kg)}}{\text{Estatura (m)}^2}$
                    </small>
                </div>
            </div>

            <!-- Tabla de Referencia OMS -->
            <div class="card">
                <div class="card-header" style="text-align: left;">
                    <h2 class="card-title" style="font-size: 1.3rem;">Tabla de Clasificación OMS</h2>
                    <p class="card-subtitle">Estándares internacionales de masa corporal para adultos</p>
                </div>

                <table class="data-table" style="font-size: 0.85rem;">
                    <thead>
                        <tr>
                            <th>Categoría</th>
                            <th>Rango IMC ($kg/m^2$)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr style="${resultado.clasificacion.codigo == 'BAJO_PESO' ? 'background-color: #e0f2fe; font-weight: bold;' : ''}">
                            <td>Bajo peso</td>
                            <td>&lt; 18.5</td>
                        </tr>
                        <tr style="${resultado.clasificacion.codigo == 'PESO_NORMAL' ? 'background-color: #dcfce7; font-weight: bold;' : ''}">
                            <td>Peso normal</td>
                            <td>18.5 – 24.9</td>
                        </tr>
                        <tr style="${resultado.clasificacion.codigo == 'SOBREPESO' ? 'background-color: #fef3c7; font-weight: bold;' : ''}">
                            <td>Sobrepeso</td>
                            <td>25.0 – 29.9</td>
                        </tr>
                        <tr style="${resultado.clasificacion.codigo == 'OBESIDAD_I' ? 'background-color: #ffedd5; font-weight: bold;' : ''}">
                            <td>Obesidad Clase I (Leve)</td>
                            <td>30.0 – 34.9</td>
                        </tr>
                        <tr style="${resultado.clasificacion.codigo == 'OBESIDAD_II' ? 'background-color: #fee2e2; font-weight: bold;' : ''}">
                            <td>Obesidad Clase II (Moderada)</td>
                            <td>35.0 – 39.9</td>
                        </tr>
                        <tr style="${resultado.clasificacion.codigo == 'OBESIDAD_III' ? 'background-color: #fecaca; font-weight: bold;' : ''}">
                            <td>Obesidad Clase III (Mórbida)</td>
                            <td>&ge; 40.0</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Tarjeta de Resultados si se ha calculado -->
        <c:if test="${not empty resultado}">
            <div class="result-card">
                <div class="result-header">
                    <div>
                        <h3 style="color: var(--primary); font-size: 1.2rem;">Resultado de la Medición</h3>
                        <small class="text-muted">Registrado exitosamente en la base de datos MySQL (ID Registro: #${resultado.registroPersistido.idRegistro})</small>
                    </div>
                    <div>
                        <c:choose>
                            <c:when test="${resultado.clasificacion.codigo == 'BAJO_PESO'}">
                                <span class="imc-badge badge-bajo-peso">${resultado.clasificacion.etiqueta}</span>
                            </c:when>
                            <c:when test="${resultado.clasificacion.codigo == 'PESO_NORMAL'}">
                                <span class="imc-badge badge-normal">${resultado.clasificacion.etiqueta}</span>
                            </c:when>
                            <c:when test="${resultado.clasificacion.codigo == 'SOBREPESO'}">
                                <span class="imc-badge badge-sobrepeso">${resultado.clasificacion.etiqueta}</span>
                            </c:when>
                            <c:when test="${resultado.clasificacion.codigo == 'OBESIDAD_I'}">
                                <span class="imc-badge badge-obesidad1">${resultado.clasificacion.etiqueta}</span>
                            </c:when>
                            <c:when test="${resultado.clasificacion.codigo == 'OBESIDAD_II'}">
                                <span class="imc-badge badge-obesidad2">${resultado.clasificacion.etiqueta}</span>
                            </c:when>
                            <c:otherwise>
                                <span class="imc-badge badge-obesidad3">${resultado.clasificacion.etiqueta}</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div style="display: grid; grid-template-columns: 200px 1fr; gap: 2rem; align-items: center;">
                    <div style="text-align: center; border-right: 1px solid var(--border); padding-right: 1.5rem;">
                        <span class="imc-value-large">${resultado.valorImcFormateado}</span>
                        <div style="font-size: 0.85rem; color: var(--text-muted); font-weight: 600;">kg / m²</div>
                    </div>
                    <div>
                        <h4 style="margin-bottom: 0.35rem; color: var(--dark);">Diagnóstico Clínico y Recomendación:</h4>
                        <p style="font-size: 0.95rem; color: #334155;">${resultado.clasificacion.recomendacion}</p>
                        <div class="mt-2">
                            <small class="text-muted">
                                Medición tomada para masa corporal de <strong>${resultado.masaCorporalKg} kg</strong> y estatura de <strong>${resultado.estaturaMetros} m</strong>.
                            </small>
                        </div>
                    </div>
                </div>

                <div class="mt-4" style="text-align: right;">
                    <a href="${pageContext.request.contextPath}/historial" class="btn btn-primary btn-sm">Consultar Historial Completo &rarr;</a>
                </div>
            </div>
        </c:if>
    </div>

    <footer>
        <p>Computación Avanzada en Java &copy; Tecmilenio - Arquitectura MVC & JAX-RS RESTful</p>
    </footer>

</body>
</html>
