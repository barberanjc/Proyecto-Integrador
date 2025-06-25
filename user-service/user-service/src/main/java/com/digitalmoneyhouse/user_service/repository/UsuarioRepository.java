package com.digitalmoneyhouse.user_service.repository;

import com.digitalmoneyhouse.user_service.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByDni(String dni);
    boolean existsByCvu(String cvu);
    boolean existsByAlias(String alias);
}