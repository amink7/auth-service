package com.cybersecurity.auth_service.repository;

import com.cybersecurity.auth_service.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void saveAndFindById_funciona() {
        Usuario usuario = new Usuario("repoUser", "pw123");
        usuarioRepository.save(usuario);

        Optional<Usuario> result = usuarioRepository.findById("repoUser");
        assertTrue(result.isPresent());
        assertEquals("repoUser", result.get().getUsername());
    }

    @Test
    void findById_notFound() {
        Optional<Usuario> result = usuarioRepository.findById("noexiste");
        assertTrue(result.isEmpty());
    }
}
