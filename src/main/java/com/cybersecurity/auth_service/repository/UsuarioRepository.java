package com.cybersecurity.auth_service.repository;

import com.cybersecurity.auth_service.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {
}
