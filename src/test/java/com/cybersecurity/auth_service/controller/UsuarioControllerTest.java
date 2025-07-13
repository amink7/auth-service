package com.cybersecurity.auth_service.controller;

import com.cybersecurity.auth_service.model.Usuario;
import com.cybersecurity.auth_service.service.UsuarioService;
import com.cybersecurity.auth_service.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registrarUsuario_llamaServiceYDevuelveMensaje() {
        Usuario usuario = new Usuario("user1", "pass1");

        String resultado = usuarioController.registrarUsuario(usuario);

        verify(usuarioService).registrarUsuario(usuario);
        assertTrue(resultado.contains("user1"));
    }

    @Test
    void login_usuarioCorrecto_devuelveToken() {
        Usuario usuario = new Usuario("user1", "pass1");
        when(usuarioService.buscarUsuario("user1", "pass1")).thenReturn(usuario);
        when(jwtUtil.generateToken("user1")).thenReturn("jwt-token");

        String resultado = usuarioController.login(usuario);

        assertEquals("jwt-token", resultado);
    }

    @Test
    void login_usuarioIncorrecto_devuelveMensajeError() {
        Usuario usuario = new Usuario("user1", "wrongpass");
        when(usuarioService.buscarUsuario("user1", "wrongpass")).thenReturn(null);

        String resultado = usuarioController.login(usuario);

        assertEquals("Usuario o contraseña incorrectos", resultado);
    }

    @Test
    void obtenerUsuarios_tokenFaltante_devuelveNoAutorizado() {
        Object respuesta = usuarioController.obtenerUsuarios(null);
        assertEquals("No autorizado: falta el token", respuesta);
    }

    @Test
    void obtenerUsuarios_tokenInvalido_devuelveTokenInvalido() {
        when(jwtUtil.validateToken("invalid")).thenReturn(false);

        Object respuesta = usuarioController.obtenerUsuarios("Bearer invalid");
        assertEquals("Token inválido", respuesta);
    }

    @Test
    void obtenerUsuarios_tokenValido_devuelveUsuarios() {
        when(jwtUtil.validateToken("validtoken")).thenReturn(true);
        when(usuarioService.obtenerUsuarios()).thenReturn(java.util.List.of(new Usuario("user1", "pass")));

        Object respuesta = usuarioController.obtenerUsuarios("Bearer validtoken");

        assertTrue(respuesta instanceof java.util.List);
        verify(usuarioService).obtenerUsuarios();
    }

    @Test
    void registrarUsuario_usuarioExistente_lanzaExcepcion() {
        Usuario usuario = new Usuario("yaexiste", "pass");
        doThrow(new IllegalArgumentException("Ya existe")).when(usuarioService).registrarUsuario(usuario);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            usuarioController.registrarUsuario(usuario);
        });
        assertEquals("Ya existe", ex.getMessage());
    }

    @Test
    void registrarUsuario_datosNulos_lanzaExcepcion() {
        Usuario usuario = new Usuario(null, null);
        doThrow(new IllegalArgumentException("Datos requeridos")).when(usuarioService).registrarUsuario(usuario);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            usuarioController.registrarUsuario(usuario);
        });
        assertEquals("Datos requeridos", ex.getMessage());
    }

    @Test
    void login_excepcionEnService_devuelveError() {
        Usuario usuario = new Usuario("alguien", "pw");
        when(usuarioService.buscarUsuario("alguien", "pw")).thenThrow(new RuntimeException("Fallo interno"));

        Exception ex = assertThrows(RuntimeException.class, () -> {
            usuarioController.login(usuario);
        });
        assertEquals("Fallo interno", ex.getMessage());
    }

    @Test
    void obtenerUsuarios_serviceLanzaExcepcion() {
        when(jwtUtil.validateToken("valido")).thenReturn(true);
        when(usuarioService.obtenerUsuarios()).thenThrow(new RuntimeException("Error BD"));

        Exception ex = assertThrows(RuntimeException.class, () -> {
            usuarioController.obtenerUsuarios("Bearer valido");
        });
        assertEquals("Error BD", ex.getMessage());
    }

    @Test
    void registrarUsuario_lanzaExcepcion_retornaMensajeDeError() {
        Usuario usuario = new Usuario("x", "y");
        doThrow(new IllegalArgumentException("Usuario ya existe"))
                .when(usuarioService).registrarUsuario(usuario);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            usuarioController.registrarUsuario(usuario);
        });
        assertEquals("Usuario ya existe", ex.getMessage());
    }

    @Test
    void obtenerUsuarios_tokenValido_serviceRetornaNull() {
        when(jwtUtil.validateToken("valido")).thenReturn(true);
        when(usuarioService.obtenerUsuarios()).thenReturn(null);

        Object result = usuarioController.obtenerUsuarios("Bearer valido");
        assertNull(result);
    }
}
