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
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
    }

    @Test
    void registrarUsuario_devuelveMensajeCorrecto() throws Exception {
        String json = """
            {"username":"newuser","password":"pass123"}
            """;

        mockMvc.perform(post("/usuarios/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Usuario registrado: newuser")));
    }

    @Test
    void login_correcto_devuelveToken() throws Exception {
        // REGISTRA EL USUARIO USANDO EL ENDPOINT (no con el service directamente)
        String registroJson = """
        {"username":"testlogin","password":"1234"}
        """;

        mockMvc.perform(post("/usuarios/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registroJson))
                .andExpect(status().isOk());

        // Luego realiza el login
        String loginJson = """
        {"username":"testlogin","password":"1234"}
        """;

        mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("."))); // Un JWT siempre tiene puntos
    }


    @Test
    void login_incorrecto_devuelveError() throws Exception {
        String json = """
            {"username":"notexists","password":"nope"}
            """;

        mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario o contraseña incorrectos"));
    }

    @Test
    void obtenerUsuarios_sinToken_devuelveNoAutorizado() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerUsuarios_conTokenInvalido_devuelveTokenInvalido() throws Exception {
        mockMvc.perform(get("/usuarios")
                        .header("Authorization", "Bearer invalid"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerUsuarios_conTokenValido_devuelveListaUsuarios() throws Exception {
        usuarioRepository.save(new Usuario("u1", "p1"));
        usuarioRepository.save(new Usuario("u2", "p2"));
        String token = jwtUtil.generateToken("admin");

        mockMvc.perform(get("/usuarios")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
