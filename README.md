# Sistema Web de Cálculo y Monitoreo de IMC


  
**Patrón de Arquitectura:** Model-View-Controller (MVC) + Servicios Web RESTful (JAX-RS)  
**Servidor de Aplicaciones:** Oracle GlassFish Server / Payara Server  
**Motor de Persistencia:** MySQL Database Server (JDBC / JNDI)  
**Gestor de Dependencias y Construcción:** Apache Maven  

---

## Tabla de Contenidos
1. [Descripción del Proyecto](#descripción-del-proyecto)
2. [Arquitectura del Sistema (MVC + REST)](#arquitectura-del-sistema-mvc--rest)
3. [Requerimientos de Software](#requerimientos-de-software)
4. [Estructura del Proyecto Maven](#estructura-del-proyecto-maven)
5. [Configuración de la Base de Datos MySQL](#configuración-de-la-base-de-datos-mysql)
6. [Despliegue en NetBeans y GlassFish Server](#despliegue-en-netbeans-y-glassfish-server)
7. [Documentación del API RESTful (JAX-RS)](#documentación-del-api-restful-jax-rs)
8. [Reglas de Negocio y Validaciones](#reglas-de-negocio-y-validaciones)

---

## 1. Descripción del Proyecto
La aplicación web de cálculo y seguimiento de IMC permite a los usuarios registrarse una sola vez con sus datos biométricos fijos (nombre completo, usuario único, contraseña, edad $\ge 15$ años, sexo y estatura base de $1.00\text{ m}$ a $2.50\text{ m}$). 

Una vez autenticado en la plataforma con una sesión segura HTTP (`HttpSession`), el usuario únicamente debe ingresar su **Masa Corporal ($kg$)** ($> 0\text{ kg}$). El sistema recupera la estatura base almacenada y calcula de forma precisa el Índice de Masa Corporal aplicando la fórmula de Quetelet:

$$\text{IMC} = \frac{\text{Masa Corporal (kg)}}{\text{Estatura (m)}^2}$$

El sistema categoriza el resultado conforme a los rangos clínicos oficiales de la **Organización Mundial de la Salud (OMS)**, emite una recomendación diagnóstica, persiste la medición con fecha y hora exacta en la base de datos MySQL y expone un **Servicio Web RESTful (JAX-RS)** en formato JSON y XML para consulta histórica asíncrona mediante **Client-Side Rendering (CSR)** con Fetch API.

---

## 2. Arquitectura del Sistema (MVC + REST)

```
+-----------------------------------------------------------------------------------+
|                               CAPA DE VISTA (VIEW)                                |
|  - login.jsp (Formulario de autenticación)                                        |
|  - registro.jsp (Registro de usuario con estatura base)                           |
|  - calculadora.jsp (Formulario de masa corporal única y tarjeta de diagnóstico)   |
|  - historial.jsp (Renderizado JSTL y CSR asíncrono con JavaScript Fetch API)      |
+-----------------------------------------------------------------------------------+
                                         │  ▲
                     HTTP Request (POST/GET) │  │ Forward / JSON Response
                                         ▼  │
+-----------------------------------------------------------------------------------+
|                        CAPA DE CONTROLADOR & SERVICIOS REST                       |
|  - AuthFilter (Filtro HTTP que protege rutas privadas)                            |
|  - AuthServlet (Gestión de sesiones: login, registro y logout)                    |
|  - CalculadoraImcServlet (Procesamiento de cálculo de peso y despacho)            |
|  - HistorialServlet (Carga de historial de mediciones)                            |
|  - HistoricoRestService (JAX-RS REST Endpoint: GET/POST /api/historico/*)         |
+-----------------------------------------------------------------------------------+
                                         │  ▲
                            Llamadas Java│  │ Objetos Modelo / DTO
                                         ▼  │
+-----------------------------------------------------------------------------------+
|                       CAPA DE MODELO Y LÓGICA DE NEGOCIO                          |
|  - Usuario & RegistroIMC (Entidades de dominio con anotaciones JAXB XML/JSON)     |
|  - ResultadoIMC (DTO de transferencia de resultados clínicos)                     |
|  - ClasificacionIMC (Enum con categorías OMS y recomendaciones diagnósticas)      |
|  - ImcCalculatorServiceImpl (Lógica de cálculo matemático y validación clínica)   |
+-----------------------------------------------------------------------------------+
                                         │  ▲
                            SQL Queries  │  │ JDBC ResultSet
                                         ▼  │
+-----------------------------------------------------------------------------------+
|                           CAPA DE ACCESO A DATOS (DAO)                            |
|  - ConexionBD (Pool JNDI 'jdbc/imc_db' con fallback a JDBC Directo)               |
|  - UsuarioDAOImpl (Operaciones CRUD sobre la tabla 'usuarios')                    |
|  - RegistroImcDAOImpl (Operaciones CRUD sobre la tabla 'registros_imc')           |
+-----------------------------------------------------------------------------------+
                                         │  ▲
                             TCP/IP:3306 │  │ MySQL Driver
                                         ▼  │
+-----------------------------------------------------------------------------------+
|                           BASE DE DATOS RELACIONAL (MySQL)                        |
|  - Esquema: imc_system_db                                                         |
|  - Tablas: usuarios (1) ───< registros_imc (N) con Foreign Key y CHECK constraints|
+-----------------------------------------------------------------------------------+
```

---

## 3. Requerimientos de Software

- **Java Development Kit (JDK):** JDK 8 (recomendado para GlassFish 4/5) o JDK 11.
- **IDE:** Apache NetBeans 12+ o NetBeans 8.2.
- **Servidor de Aplicaciones:** GlassFish Server 4.1.x / 5.x o Payara Server 5.x.
- **Manejador de Base de Datos:** MySQL Server 5.7+ o MySQL 8.0+.
- **Herramienta de Construcción:** Apache Maven 3.6+.
- **Navegador Web:** Google Chrome, Mozilla Firefox, Microsoft Edge (con soporte JavaScript ES6/Fetch).

---

## 4. Estructura del Proyecto Maven

```text
Avance-Evidencia_1/
├── pom.xml                                  <- Descriptor de dependencias y build Maven
├── database_schema.sql                      <- Script DDL SQL de creación y datos semilla
├── README.md                                <- Documentación técnica integral
├── guion_presentacion_youtube.md            <- Libreto de la presentación técnica para video
├── reporte_avance1_evidencia.html           <- Documento entregable del Avance 1
├── reporte_entrega_final.html               <- Documento entregable final (imprimible a PDF/Word)
├── diagrama_clases.drawio                   <- Diagrama de clases MVC + REST (.drawio)
├── diagrama_bd_er.drawio                    <- Diagrama Entidad-Relación de la base de datos
├── bocetos_interfaces.drawio                <- Bocetos de interfaz de usuario minimalistas
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/imcapp/
│   │   │       ├── model/
│   │   │       │   ├── ClasificacionIMC.java   <- Enumeración con escalas OMS
│   │   │       │   ├── Usuario.java            <- Entidad de usuario persistente
│   │   │       │   ├── RegistroIMC.java        <- Entidad de medición histórica
│   │   │       │   └── ResultadoIMC.java       <- DTO de cálculo y respuesta
│   │   │       ├── util/
│   │   │       │   └── ConexionBD.java         <- Conexión JDBC / JNDI DataSource
│   │   │       ├── dao/
│   │   │       │   ├── IUsuarioDAO.java        <- Contrato DAO de usuario
│   │   │       │   ├── UsuarioDAOImpl.java     <- Implementación JDBC de usuario
│   │   │       │   ├── IRegistroImcDAO.java    <- Contrato DAO de mediciones
│   │   │       │   └── RegistroImcDAOImpl.java <- Implementación JDBC de mediciones
│   │   │       ├── service/
│   │   │       │   ├── IImcCalculatorService.java   <- Contrato de lógica de negocio
│   │   │       │   └── ImcCalculatorServiceImpl.java<- Cálculo de Quetelet y reglas
│   │   │       ├── filter/
│   │   │       │   └── AuthFilter.java         <- Filtro de seguridad de sesión
│   │   │       ├── controller/
│   │   │       │   ├── AuthServlet.java        <- Servlet de login, registro y logout
│   │   │       │   ├── CalculadoraImcServlet.java <- Servlet de cálculo de masa corporal
│   │   │       │   └── HistorialServlet.java   <- Servlet de vista de historial
│   │   │       └── rest/
│   │   │           ├── RestApplicationConfig.java <- Configuración JAX-RS (/api/*)
│   │   │           └── HistoricoRestService.java  <- Endpoint RESTful JSON/XML
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── web.xml                  <- Descriptor de despliegue Web Servlet 3.1
│   │       │   └── glassfish-web.xml        <- Configuración de contexto GlassFish
│   │       ├── css/
│   │       │   └── styles.css               <- Estilos CSS responsivos
│   │       ├── index.jsp                    <- Página de bienvenida y enrutamiento
│   │       ├── login.jsp                    <- Vista de inicio de sesión
│   │       ├── registro.jsp                 <- Vista de registro único de usuario
│   │       ├── calculadora.jsp              <- Vista principal de cálculo
│   │       ├── historial.jsp                <- Vista de histórico con cliente Fetch CSR
│   │       └── error.jsp                    <- Vista para manejo de excepciones HTTP
│   └── test/
│       └── java/
│           └── com/imcapp/
│               └── service/
│                   └── ImcCalculatorServiceTest.java <- Pruebas unitarias JUnit
```

---

## 5. Configuración de la Base de Datos MySQL

1. Abra su cliente MySQL (MySQL Workbench, phpMyAdmin, DBeaver o línea de comandos `mysql -u root -p`).
2. Ejecute el script [`database_schema.sql`](database_schema.sql):
   ```sql
   SOURCE database_schema.sql;
   ```
3. El script creará la base de datos `imc_system_db`, las tablas `usuarios` y `registros_imc` con restricciones `CHECK`, índices y usuarios de prueba.

### Usuarios de Prueba Precargados:
| Nombre de Usuario | Contraseña | Nombre Completo | Edad | Sexo | Estatura |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `jperez` | `admin123` | Juan Pérez Gómez | 28 | M | 1.75 m |
| `mlopez` | `admin123` | María López Silva | 24 | F | 1.62 m |
| `cgarcia` | `admin123` | Carlos García Ruiz | 45 | M | 1.80 m |

---

## 6. Despliegue en NetBeans y GlassFish Server

### Opción A: Despliegue Directo desde NetBeans
1. Abra NetBeans IDE.
2. Vaya a **File > Open Project...** y seleccione la carpeta `Avance-Evidencia_1`.
3. Verifique que GlassFish Server esté registrado en la pestaña **Services > Servers**.
4. Haga clic derecho sobre el proyecto y seleccione **Clean and Build**.
5. Haga clic derecho y seleccione **Run** o **Deploy**. NetBeans iniciará GlassFish y abrirá el navegador en:
   `http://localhost:8080/imc-web-app/`

### Opción B: Configuración del DataSource JNDI en GlassFish (Opcional para Producción)
1. Ingrese a la consola de administración de GlassFish (`http://localhost:4848`).
2. Vaya a **JDBC > JDBC Connection Pools** y cree un nuevo pool llamado `ImcDbPool`:
   - Resource Type: `javax.sql.DataSource`
   - Database Driver Vendor: `MySQL`
   - Properties: `User=root`, `Password=tu_password`, `ServerName=localhost`, `PortNumber=3306`, `DatabaseName=imc_system_db`.
3. Vaya a **JDBC > JDBC Resources** y cree el recurso:
   - JNDI Name: `jdbc/imc_db`
   - Pool Name: `ImcDbPool`.

> **Nota:** La clase `ConexionBD.java` incluye un mecanismo de **fallback automático**: si el recurso JNDI no está configurado en GlassFish, se conecta de manera transparente vía JDBC directo a `localhost:3306/imc_system_db`.

---

## 7. Documentación del API RESTful (JAX-RS)

La aplicación implementa un servicio web RESTful de nivel 2 y 3 bajo la especificación JAX-RS (Jersey):

### 1. Obtener Histórico de un Usuario
- **Método HTTP:** `GET`
- **URI:** `/api/historico/{idUsuario}`
- **Cabeceras:** `Accept: application/json` o `Accept: application/xml`
- **Códigos de Respuesta:**
  - `200 OK`: Lista de mediciones en formato JSON/XML.
  - `400 Bad Request`: ID de usuario menor o igual a 0.
  - `404 Not Found`: El usuario solicitado no existe.
  - `500 Internal Server Error`: Fallo de base de datos.

**Ejemplo de Respuesta JSON (`GET /api/historico/1`):**
```json
[
  {
    "idRegistro": 1,
    "idUsuario": 1,
    "masaCorporalKg": 70.0,
    "estaturaMetros": 1.75,
    "valorImc": 22.86,
    "clasificacion": "Peso normal",
    "fechaRegistro": "2026-03-01T10:00:00"
  },
  {
    "idRegistro": 2,
    "idUsuario": 1,
    "masaCorporalKg": 72.5,
    "estaturaMetros": 1.75,
    "valorImc": 23.67,
    "clasificacion": "Peso normal",
    "fechaRegistro": "2026-03-15T11:30:00"
  }
]
```

### 2. Obtener Detalle de una Medición Específica
- **Método HTTP:** `GET`
- **URI:** `/api/historico/detalle/{idRegistro}`
- **Códigos de Respuesta:** `200 OK` | `404 Not Found`.

### 3. Registrar Medición vía REST API
- **Método HTTP:** `POST`
- **URI:** `/api/historico`
- **Content-Type:** `application/json`
- **Cuerpo de Solicitud (Payload):**
  ```json
  {
    "idUsuario": 1,
    "masaCorporalKg": 74.0
  }
  ```
- **Códigos de Respuesta:** `201 Created` con cabecera `Location: /api/historico/detalle/{id}`.

---

## 8. Reglas de Negocio y Validaciones

1. **Registro Único:** Los datos de nombre completo, username, contraseña, edad, sexo y estatura base se capturan una sola vez en el registro.
2. **Restricción de Edad:** La edad debe ser obligatoriamente $\ge 15$ años (normativa pediátrica/adulta OMS).
3. **Restricción de Estatura:** La estatura debe estar comprendida entre $1.00\text{ m}$ y $2.50\text{ m}$.
4. **Cálculo con Peso Único:** En la vista de cálculo, el usuario sólo puede ingresar su masa corporal actual ($kg$). La estatura se toma de la sesión activa de forma inmutable.
5. **Masa Corporal Positiva:** El peso ingresado debe ser estrictamente mayor a $0\text{ kg}$ (no se aceptan ceros ni valores negativos).
6. **Seguridad y Filtros:** Rutas protegidas (`/calculadora`, `/historial`) requieren autenticación obligatoria; de lo contrario, `AuthFilter` redirige a `/auth?action=login`.

---
*Computación Avanzada en Java – Manuel Esau Noriega Rodriguez*
