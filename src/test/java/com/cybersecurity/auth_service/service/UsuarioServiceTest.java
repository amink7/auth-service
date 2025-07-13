package com.cybersecurity.auth_service.service;

import com.cybersecurity.auth_service.model.Usuario;
import com.cybersecurity.auth_service.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

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
    void registrarUsuario_guardaUsuarioCifrado() {
        Usuario usuario = new Usuario("nuevo", "sinCifrar");
        when(passwordEncoder.encode("sinCifrar")).thenReturn("cifrado");
        usuarioService.registrarUsuario(usuario);

        assertEquals("cifrado", usuario.getPassword());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void buscarUsuario_credencialesCorrectas_devuelveUsuario() {
        Usuario usuario = new Usuario("admin", "cifrado");
        when(usuarioRepository.findById("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("plain", "cifrado")).thenReturn(true);

        Usuario resultado = usuarioService.buscarUsuario("admin", "plain");
        assertNotNull(resultado);
    }

    @Test
    void buscarUsuario_credencialesIncorrectas_devuelveNull() {
        Usuario usuario = new Usuario("admin", "cifrado");
        when(usuarioRepository.findById("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("malpass", "cifrado")).thenReturn(false);

        Usuario resultado = usuarioService.buscarUsuario("admin", "malpass");
        assertNull(resultado);
    }

    @Test
    void buscarUsuario_noExisteUsuario_devuelveNull() {
        when(usuarioRepository.findById("nadie")).thenReturn(Optional.empty());

        Usuario resultado = usuarioService.buscarUsuario("nadie", "pw");
        assertNull(resultado);
    }

    @Test
    void obtenerUsuarios_devuelveLista() {
        when(usuarioRepository.findAll()).thenReturn(List.of(new Usuario("a", "b")));

        List<Usuario> lista = usuarioService.obtenerUsuarios();
        assertEquals(1, lista.size());
    }
}
