package com.digitalmoneyhouse.auth_service.service;

import com.digitalmoneyhouse.auth_service.client.UsuarioClient;
import com.digitalmoneyhouse.auth_service.dto.UsuarioAuthDTO;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioClient usuarioClient;

    public AuthService(UsuarioClient usuarioClient) {
        this.usuarioClient = usuarioClient;
    }

    public UsuarioAuthDTO login(String email, String password) {
        UsuarioAuthDTO user = usuarioClient.getUsuarioByEmail(email);

        System.out.println("[DEBUG] Usuario recibido desde user-service: " + user.getEmail() + " / " + user.getPassword());

        if (password.equals(user.getPassword())) {
            return user;
        } else {
            throw new RuntimeException("Credenciales incorrectas");
        }
    }
}