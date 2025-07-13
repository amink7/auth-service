package com.cybersecurity.auth_service.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBadRequest_illegalArgument() {
        ResponseEntity<Map<String, Object>> response = handler.handleBadRequest(new IllegalArgumentException("Petición inválida"));
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Petición inválida", response.getBody().get("error"));
    }

    @Test
    void handleUnauthorized_badCredentials() {
        ResponseEntity<Map<String, Object>> response = handler.handleUnauthorized(new org.springframework.security.authentication.BadCredentialsException("fail"));
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Credenciales inválidas o sesión caducada", response.getBody().get("error"));
    }

    @Test
    void handleForbidden_accessDenied() {
        ResponseEntity<Map<String, Object>> response = handler.handleForbidden(new AccessDeniedException("denegado"));
        assertEquals(403, response.getStatusCodeValue());
        assertEquals("No tienes permisos para acceder a este recurso", response.getBody().get("error"));
    }

    @Test
    void handleNotFound_noHandler() {
        ResponseEntity<Map<String, Object>> response = handler.handleNotFound(new NoHandlerFoundException("GET", "/x", null));
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Recurso no encontrado", response.getBody().get("error"));
    }

    @Test
    void handleAllExceptions_generic() {
        ResponseEntity<Map<String, Object>> response = handler.handleAllExceptions(new Exception("fatal"));
        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().get("error").toString().contains("fatal"));
    }


}
