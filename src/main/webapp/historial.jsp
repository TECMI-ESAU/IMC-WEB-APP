<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Historial de Mediciones IMC - JAX-RS REST Client</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <style>
        .json-viewer {
            background-color: #0f172a;
            color: #38bdf8;
            font-family: 'Consolas', 'Courier New', monospace;
            font-size: 0.85rem;
            padding: 1rem;
            border-radius: var(--radius);
            max-height: 220px;
            overflow-y: auto;
            white-space: pre-wrap;
            margin-top: 1rem;
            display: none;
        }
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            margin-bottom: 1.5rem;
        }
        .stat-card {
            background: white;
            border: 1px solid var(--border);
            border-radius: var(--radius);
            padding: 1rem;
            text-align: center;
        }
        .stat-val {
            font-size: 1.5rem;
            font-weight: 700;
            color: var(--primary);
        }
        .stat-lbl {
            font-size: 0.8rem;
            color: var(--text-muted);
            text-transform: uppercase;
        }
    </style>
</head>
<body>

    <header>
        <div class="navbar">
            <a href="${pageContext.request.contextPath}/calculadora" class="brand">
                <span class="brand-badge">Java EE</span>
                <span>Calculadora IMC</span>
            </a>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/calculadora">Calculadora</a></li>
                <li><a href="${pageContext.request.contextPath}/historial" class="active">Mi Historial</a></li>
                <li class="user-badge">
                    <span>👤 ${sessionScope.usuarioLogueado.nombreCompleto}</span>
                </li>
                <li><a href="${pageContext.request.contextPath}/auth?action=logout" class="btn-logout">Cerrar Sesión</a></li>
            </ul>
        </div>
    </header>

    <div class="container">
        <!-- Barra de Perfil -->
        <div class="user-profile-bar">
            <div>
                <h2 style="font-size: 1.2rem; color: var(--primary);">Historial de Mediciones Clínicas</h2>
                <small class="text-muted">Monitoreo temporal de Índice de Masa Corporal para @<c:out value="${sessionScope.usuarioLogueado.username}"/></small>
            </div>
            <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
                <button type="button" id="btnRestFetch" class="btn btn-secondary btn-sm" onclick="recargarHistoricoREST()">
                    🔄 Sincronizar vía REST JAX-RS
                </button>
                <a href="${pageContext.request.contextPath}/calculadora" class="btn btn-primary btn-sm">
                    + Nueva Medición
                </a>
            </div>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">
                <strong>Error:</strong> <c:out value="${error}"/>
            </div>
        </c:if>

        <c:if test="${not empty mensaje}">
            <div class="alert alert-info">
                <c:out value="${mensaje}"/>
            </div>
        </c:if>

        <!-- Métricas Rápidas -->
        <c:if test="${not empty historico}">
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-val">${historico.size()}</div>
                    <div class="stat-lbl">Mediciones Registradas</div>
                </div>
                <div class="stat-card">
                    <div class="stat-val"><fmt:formatNumber value="${historico[0].valorImc}" pattern="0.00"/></div>
                    <div class="stat-lbl">Último IMC Registrado</div>
                </div>
                <div class="stat-card">
                    <div class="stat-val"><fmt:formatNumber value="${historico[0].masaCorporalKg}" pattern="0.0"/> kg</div>
                    <div class="stat-lbl">Último Peso Medido</div>
                </div>
                <div class="stat-card">
                    <div class="stat-val"><fmt:formatNumber value="${sessionScope.usuarioLogueado.estaturaMetros}" pattern="0.00"/> m</div>
                    <div class="stat-lbl">Estatura Base</div>
                </div>
            </div>
        </c:if>

        <!-- Indicador de Carga REST -->
        <div id="restStatusAlert" class="alert alert-info" style="display: none;">
            <span id="restStatusText">Consultando endpoint JAX-RS: <code>/api/historico/${sessionScope.usuarioLogueado.idUsuario}</code>...</span>
        </div>

        <!-- Tabla de Datos -->
        <div class="table-container">
            <table class="data-table" id="tablaHistorico">
                <thead>
                    <tr>
                        <th style="width: 70px;"># ID</th>
                        <th>Fecha y Hora</th>
                        <th>Peso (kg)</th>
                        <th>Estatura</th>
                        <th>IMC ($kg/m^2$)</th>
                        <th>Diagnóstico / Categoría</th>
                    </tr>
                </thead>
                <tbody id="tablaBody">
                    <c:choose>
                        <c:when test="${not empty historico}">
                            <c:forEach items="${historico}" var="item">
                                <tr>
                                    <td><strong>#${item.idRegistro}</strong></td>
                                    <td><fmt:formatDate value="${item.fechaRegistro}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
                                    <td><fmt:formatNumber value="${item.masaCorporalKg}" pattern="0.00"/> kg</td>
                                    <td><fmt:formatNumber value="${item.estaturaMetros}" pattern="0.00"/> m</td>
                                    <td>
                                        <strong><fmt:formatNumber value="${item.valorImc}" pattern="0.00"/></strong>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${item.clasificacion == 'Bajo peso'}">
                                                <span class="imc-badge badge-bajo-peso" style="font-size: 0.8rem; padding: 0.2rem 0.6rem;">${item.clasificacion}</span>
                                            </c:when>
                                            <c:when test="${item.clasificacion == 'Peso normal'}">
                                                <span class="imc-badge badge-normal" style="font-size: 0.8rem; padding: 0.2rem 0.6rem;">${item.clasificacion}</span>
                                            </c:when>
                                            <c:when test="${item.clasificacion == 'Sobrepeso'}">
                                                <span class="imc-badge badge-sobrepeso" style="font-size: 0.8rem; padding: 0.2rem 0.6rem;">${item.clasificacion}</span>
                                            </c:when>
                                            <c:when test="${item.clasificacion == 'Obesidad Clase I'}">
                                                <span class="imc-badge badge-obesidad1" style="font-size: 0.8rem; padding: 0.2rem 0.6rem;">${item.clasificacion}</span>
                                            </c:when>
                                            <c:when test="${item.clasificacion == 'Obesidad Clase II'}">
                                                <span class="imc-badge badge-obesidad2" style="font-size: 0.8rem; padding: 0.2rem 0.6rem;">${item.clasificacion}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="imc-badge badge-obesidad3" style="font-size: 0.8rem; padding: 0.2rem 0.6rem;">${item.clasificacion}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="6" class="text-center" style="padding: 2.5rem; color: var(--text-muted);">
                                    No se encontraron registros de IMC. 
                                    <a href="${pageContext.request.contextPath}/calculadora" style="color: var(--primary); font-weight: 600;">Haz tu primera medición aquí</a>.
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>

        <!-- Visor del Payload REST JSON (Demostración Técnica) -->
        <div class="mt-4">
            <button type="button" class="btn btn-secondary btn-sm" onclick="toggleJsonViewer()">
                🔍 Ver / Ocultar Respuesta JSON de la API REST (/api/historico)
            </button>
            <pre id="jsonViewer" class="json-viewer"></pre>
        </div>
    </div>

    <footer>
        <p>Computación Avanzada en Java &copy; Tecmilenio - Arquitectura MVC & JAX-RS RESTful</p>
    </footer>

    <!-- Script de Consumo Asíncrono RESTful (CSR - Client Side Rendering) -->
    <script>
        const userId = "${sessionScope.usuarioLogueado.idUsuario}";
        const restEndpoint = "${pageContext.request.contextPath}/api/historico/" + userId;

        function getBadgeClass(clasificacion) {
            if (!clasificacion) return "badge-normal";
            if (clasificacion.includes("Bajo")) return "badge-bajo-peso";
            if (clasificacion.includes("normal")) return "badge-normal";
            if (clasificacion.includes("Sobrepeso")) return "badge-sobrepeso";
            if (clasificacion.includes("Clase I")) return "badge-obesidad1";
            if (clasificacion.includes("Clase II")) return "badge-obesidad2";
            return "badge-obesidad3";
        }

        function formatDate(dateString) {
            if (!dateString) return "-";
            const d = new Date(dateString);
            if (isNaN(d.getTime())) return dateString;
            return d.toLocaleDateString('es-MX', { 
                year: 'numeric', month: '2-digit', day: '2-digit',
                hour: '2-digit', minute: '2-digit', second: '2-digit'
            });
        }

        async function recargarHistoricoREST() {
            const statusAlert = document.getElementById("restStatusAlert");
            const statusText = document.getElementById("restStatusText");
            const btn = document.getElementById("btnRestFetch");
            const jsonViewer = document.getElementById("jsonViewer");

            statusAlert.style.display = "block";
            statusAlert.className = "alert alert-info";
            statusText.innerText = "Conectando con el Servicio REST JAX-RS: " + restEndpoint + " [Accept: application/json]...";
            btn.disabled = true;

            try {
                const response = await fetch(restEndpoint, {
                    method: 'GET',
                    headers: {
                        'Accept': 'application/json'
                    }
                });

                if (!response.ok) {
                    throw new Error("HTTP Error " + response.status + ": " + response.statusText);
                }

                const data = await response.json();
                
                // Actualizar Visor JSON
                jsonViewer.textContent = JSON.stringify(data, null, 2);

                // Renderizar tabla dinámicamente en el cliente (CSR)
                const tbody = document.getElementById("tablaBody");
                tbody.innerHTML = "";

                if (!data || data.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="6" class="text-center" style="padding: 2.5rem; color: var(--text-muted);">' +
                                      'No hay mediciones registradas en el servicio REST.</td></tr>';
                } else {
                    data.forEach(item => {
                        const tr = document.createElement("tr");
                        const badgeClass = getBadgeClass(item.clasificacion);
                        
                        tr.innerHTML = 
                            '<td><strong>#' + item.idRegistro + '</strong></td>' +
                            '<td>' + formatDate(item.fechaRegistro) + '</td>' +
                            '<td>' + Number(item.masaCorporalKg).toFixed(2) + ' kg</td>' +
                            '<td>' + Number(item.estaturaMetros).toFixed(2) + ' m</td>' +
                            '<td><strong>' + Number(item.valorImc).toFixed(2) + '</strong></td>' +
                            '<td><span class="imc-badge ' + badgeClass + '" style="font-size: 0.8rem; padding: 0.2rem 0.6rem;">' + item.clasificacion + '</span></td>';
                        tbody.appendChild(tr);
                    });
                }

                statusAlert.className = "alert alert-success";
                statusText.innerText = "¡Sincronización REST exitosa! " + data.length + " mediciones recibidas e inyectadas dinámicamente vía CSR.";
            } catch (err) {
                console.error("Error al consumir API REST:", err);
                statusAlert.className = "alert alert-danger";
                statusText.innerText = "Error al consultar API REST: " + err.message;
            } finally {
                btn.disabled = false;
            }
        }

        function toggleJsonViewer() {
            const viewer = document.getElementById("jsonViewer");
            if (viewer.style.display === "none" || viewer.style.display === "") {
                if (!viewer.textContent) {
                    recargarHistoricoREST().then(() => {
                        viewer.style.display = "block";
                    });
                } else {
                    viewer.style.display = "block";
                }
            } else {
                viewer.style.display = "none";
            }
        }
    </script>

</body>
</html>
