package com.cybersecurity.auth_service.service;

import com.cybersecurity.auth_service.model.Usuario;
import com.cybersecurity.auth_service.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registrarUsuario_guardaUsuarioEncriptado() {
        Usuario usuario = new Usuario("amin", "1234");
        when(passwordEncoder.encode("1234")).thenReturn("hashed_1234");

        usuarioService.registrarUsuario(usuario);

        assertEquals("hashed_1234", usuario.getPassword());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void buscarUsuario_devuelveUsuarioSiCoincidePassword() {
        Usuario usuario = new Usuario("amin", "hashed_1234");
        when(usuarioRepository.findById("amin")).thenReturn(java.util.Optional.of(usuario));
        when(passwordEncoder.matches("1234", "hashed_1234")).thenReturn(true);

        Usuario result = usuarioService.buscarUsuario("amin", "1234");

        assertNotNull(result);
        assertEquals("amin", result.getUsername());
    }

    @Test
    void buscarUsuario_devuelveNullSiNoCoincidePassword() {
        Usuario usuario = new Usuario("amin", "hashed_1234");
        when(usuarioRepository.findById("amin")).thenReturn(java.util.Optional.of(usuario));
        when(passwordEncoder.matches("badpassword", "hashed_1234")).thenReturn(false);

        Usuario result = usuarioService.buscarUsuario("amin", "badpassword");

        assertNull(result);
    }
}

