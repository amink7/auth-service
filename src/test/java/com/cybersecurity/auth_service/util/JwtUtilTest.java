package com.cybersecurity.auth_service.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil();

    @Test
    void generateToken_y_validateToken_funciona() {
        String token = jwtUtil.generateToken("usuarioX");

        assertTrue(jwtUtil.validateToken(token));
        assertEquals("usuarioX", jwtUtil.getUsernameFromToken(token));
    }

    @Test
    void validateToken_tokenInvalido_retornaFalse() {
        assertFalse(jwtUtil.validateToken("abcdefg.invalid.token"));
    }

    @Test
    void getUsernameFromToken_tokenCorrupto_lanzaExcepcion() {
        JwtUtil localJwtUtil = new JwtUtil();
        assertThrows(Exception.class, () -> {
            localJwtUtil.getUsernameFromToken("abcdefg.invalid.token");
        });
    }
}
