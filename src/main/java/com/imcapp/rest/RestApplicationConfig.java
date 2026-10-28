package com.imcapp.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Configuración de la aplicación JAX-RS RESTful.
 * Define la ruta base para todos los servicios web en /api/*
 */
@ApplicationPath("/api")
public class RestApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(HistoricoRestService.class);
        return classes;
    }
}
