package com.cybersecurity.auth_service.controller;

import com.cybersecurity.auth_service.model.Usuario;
import com.cybersecurity.auth_service.service.UsuarioService;
import com.cybersecurity.auth_service.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    // ✅ Inyección por constructor
    public UsuarioController(UsuarioService usuarioService, JwtUtil jwtUtil) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/registrar")
    public String registrarUsuario(@RequestBody Usuario usuario) {
        usuarioService.registrarUsuario(usuario);
        return "Usuario registrado: " + usuario.getUsername();
    }

    @PostMapping("/login")
    public String login(@RequestBody Usuario usuario) {
        Usuario encontrado = usuarioService.buscarUsuario(usuario.getUsername(), usuario.getPassword());
        if (encontrado != null) {
            return jwtUtil.generateToken(encontrado.getUsername());
        }
        return "Usuario o contraseña incorrectos";
    }

    @GetMapping
    public Object obtenerUsuarios(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "No autorizado: falta el token";
        }
        String token = authHeader.replace("Bearer ", "");
        if (!jwtUtil.validateToken(token)) {
            return "Token inválido";
        }
        return usuarioService.obtenerUsuarios();
    }
}
