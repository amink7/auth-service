package com.cybersecurity.auth_service.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    // Usa el mismo secret que en application.properties (mínimo 32 caracteres)
    private static final String TEST_SECRET = "12345678901234567890123456789012";
    private final JwtUtil jwtUtil = new JwtUtil(TEST_SECRET);

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
        JwtUtil localJwtUtil = new JwtUtil(TEST_SECRET);
        assertThrows(Exception.class, () -> {
            localJwtUtil.getUsernameFromToken("abcdefg.invalid.token");
        });
    }
}

