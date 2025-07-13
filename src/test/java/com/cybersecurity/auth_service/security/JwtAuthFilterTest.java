package com.cybersecurity.auth_service.security;

import com.cybersecurity.auth_service.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.*;

class JwtAuthFilterTest {

    @Test
    void doFilterInternal_loginOrRegister_noAuthRequired() throws Exception {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        JwtAuthFilter filter = new JwtAuthFilter(jwtUtil);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/usuarios/login");
        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_tokenInvalido_setUnauthorized() throws Exception {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        JwtAuthFilter filter = new JwtAuthFilter(jwtUtil);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer invalid");
        when(jwtUtil.validateToken("invalid")).thenReturn(false);
        when(request.getRequestURI()).thenReturn("/usuarios");

        filter.doFilterInternal(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        SecurityContextHolder.clearContext();
    }
}
