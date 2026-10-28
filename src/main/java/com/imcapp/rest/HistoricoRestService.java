package com.imcapp.rest;

import com.imcapp.dao.IRegistroImcDAO;
import com.imcapp.dao.IUsuarioDAO;
import com.imcapp.dao.RegistroImcDAOImpl;
import com.imcapp.dao.UsuarioDAOImpl;
import com.imcapp.model.RegistroIMC;
import com.imcapp.model.ResultadoIMC;
import com.imcapp.model.Usuario;
import com.imcapp.service.IImcCalculatorService;
import com.imcapp.service.ImcCalculatorServiceImpl;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio Web RESTful (JAX-RS) para la gestión y consulta del histórico de mediciones IMC.
 * Soporta representaciones en JSON y XML y responde a clientes HTTP asíncronos (Fetch API / AJAX / Mobile).
 * 
 * URI Base: /api/historico
 */
@Path("/historico")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class HistoricoRestService {

    private static final Logger LOGGER = Logger.getLogger(HistoricoRestService.class.getName());

    private final IRegistroImcDAO registroImcDAO;
    private final IUsuarioDAO usuarioDAO;
    private final IImcCalculatorService calculatorService;

    public HistoricoRestService() {
        this.registroImcDAO = new RegistroImcDAOImpl();
        this.usuarioDAO = new UsuarioDAOImpl();
        this.calculatorService = new ImcCalculatorServiceImpl();
    }

    // Constructor para inyección en pruebas unitarias
    public HistoricoRestService(IRegistroImcDAO registroImcDAO, IUsuarioDAO usuarioDAO, IImcCalculatorService calculatorService) {
        this.registroImcDAO = registroImcDAO;
        this.usuarioDAO = usuarioDAO;
        this.calculatorService = calculatorService;
    }

    /**
     * Obtiene el listado completo de mediciones históricas de un usuario por su ID.
     * Endpoint: GET /api/historico/{idUsuario}
     *
     * @param idUsuario Identificador del usuario
     * @return 200 OK con la lista de registros | 400 Bad Request si el ID es inválido | 404 Not Found si el usuario no existe | 500 Internal Error
     */
    @GET
    @Path("/{idUsuario}")
    public Response obtenerHistoricoPorUsuario(@PathParam("idUsuario") int idUsuario) {
        if (idUsuario <= 0) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 400);
            error.put("error", "Bad Request");
            error.put("mensaje", "El ID de usuario proporcionado no es válido (" + idUsuario + "). Debe ser mayor a 0.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        try {
            Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
            if (usuario == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("status", 404);
                error.put("error", "Not Found");
                error.put("mensaje", "No se encontró ningún usuario con el ID " + idUsuario);
                return Response.status(Response.Status.NOT_FOUND).entity(error).build();
            }

            List<RegistroIMC> registros = registroImcDAO.listarPorUsuario(idUsuario);
            return Response.ok(registros).build();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error al consultar histórico REST para idUsuario=" + idUsuario, ex);
            Map<String, Object> error = new HashMap<>();
            error.put("status", 500);
            error.put("error", "Internal Server Error");
            error.put("mensaje", "Error interno al consultar la base de datos: " + ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }

    /**
     * Obtiene una medición individual por su ID de registro.
     * Endpoint: GET /api/historico/detalle/{idRegistro}
     *
     * @param idRegistro Identificador único del registro de medición
     * @return 200 OK con el registro | 404 Not Found si no existe
     */
    @GET
    @Path("/detalle/{idRegistro}")
    public Response obtenerRegistroPorId(@PathParam("idRegistro") int idRegistro) {
        if (idRegistro <= 0) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 400);
            error.put("error", "Bad Request");
            error.put("mensaje", "El ID de registro debe ser un entero positivo.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        try {
            RegistroIMC registro = registroImcDAO.buscarPorId(idRegistro);
            if (registro == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("status", 404);
                error.put("error", "Not Found");
                error.put("mensaje", "No existe medición con ID " + idRegistro);
                return Response.status(Response.Status.NOT_FOUND).entity(error).build();
            }
            return Response.ok(registro).build();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error al consultar detalle REST para idRegistro=" + idRegistro, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    /**
     * Crea un nuevo registro de medición IMC vía REST API.
     * Endpoint: POST /api/historico
     *
     * @param nuevoRegistro Datos con idUsuario y masaCorporalKg
     * @param uriInfo Información del contexto URI para hypermedia Location
     * @return 201 Created con el registro persistido y cabecera Location | 400 Bad Request
     */
    @POST
    public Response registrarMedicion(RegistroIMC nuevoRegistro, @Context UriInfo uriInfo) {
        if (nuevoRegistro == null || nuevoRegistro.getIdUsuario() <= 0 || nuevoRegistro.getMasaCorporalKg() <= 0) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 400);
            error.put("error", "Bad Request");
            error.put("mensaje", "Datos incompletos o inválidos: idUsuario y masaCorporalKg (> 0) son obligatorios.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        try {
            Usuario usuario = usuarioDAO.buscarPorId(nuevoRegistro.getIdUsuario());
            if (usuario == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("status", 404);
                error.put("error", "Not Found");
                error.put("mensaje", "Usuario no encontrado con ID " + nuevoRegistro.getIdUsuario());
                return Response.status(Response.Status.NOT_FOUND).entity(error).build();
            }

            ResultadoIMC resultado = calculatorService.calcularYRegistrar(
                    nuevoRegistro.getIdUsuario(),
                    nuevoRegistro.getMasaCorporalKg(),
                    usuario.getEstaturaMetros()
            );

            URI createdUri = uriInfo.getAbsolutePathBuilder().path("detalle").path(String.valueOf(resultado.getRegistroPersistido().getIdRegistro())).build();
            return Response.created(createdUri).entity(resultado.getRegistroPersistido()).build();
        } catch (IllegalArgumentException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 400);
            error.put("error", "Bad Request");
            error.put("mensaje", ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error al registrar medición vía REST", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
}
