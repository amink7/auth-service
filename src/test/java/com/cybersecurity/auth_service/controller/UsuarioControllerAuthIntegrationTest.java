package com.cybersecurity.auth_service.controller;

import com.cybersecurity.auth_service.model.Usuario;
import com.cybersecurity.auth_service.repository.UsuarioRepository;
import com.cybersecurity.auth_service.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UsuarioControllerAuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void limpiarBD() {
        usuarioRepository.deleteAll();
        usuarioRepository.save(new Usuario("amin", passwordEncoder.encode("1234")));
    }

    @Test
    void accederAUsuarios_sinToken_debeDar401() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accederAUsuarios_conTokenValido_devuelveListaUsuarios() throws Exception {
        // Prepara un usuario
        Usuario usuario = new Usuario("amin", "1234");
        usuarioRepository.save(usuario);

        // Crea el JWT válido (simulando login)
        String token = jwtUtil.generateToken("amin");

        mockMvc.perform(get("/usuarios")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("amin")));
    }

    @Test
    void accederAUsuarios_conTokenInvalido_debeDar401() throws Exception {
        // Prepara un usuario
        Usuario usuario = new Usuario("amin", "1234");
        usuarioRepository.save(usuario);

        // Token inventado
        String fakeToken = "Bearer faketoken123";

        mockMvc.perform(get("/usuarios")
                        .header("Authorization", fakeToken))
                .andExpect(status().isUnauthorized());
    }

}
