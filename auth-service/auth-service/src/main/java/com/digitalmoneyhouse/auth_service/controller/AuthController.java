package com.digitalmoneyhouse.auth_service.controller;

import com.digitalmoneyhouse.auth_service.client.UsuarioClient;
import com.digitalmoneyhouse.auth_service.model.LoginRequest;
import com.digitalmoneyhouse.auth_service.dto.UsuarioAuthDTO;
import com.digitalmoneyhouse.auth_service.security.JwtUtil;
import com.digitalmoneyhouse.auth_service.service.BlacklistedTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioClient usuarioClient;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BlacklistedTokenService blacklistedTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            UsuarioAuthDTO user = usuarioClient.getUsuarioByEmail(loginRequest.getEmail());

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }

            if (!user.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Contraseña incorrecta");
            }

            String token = jwtUtil.generateToken(user.getEmail());
            return ResponseEntity.ok(Map.of("token", token));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token no provisto o inválido");
            }

            String token = authHeader.substring(7);
            String email = jwtUtil.extractUsername(token);

            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token inválido");
            }

            blacklistedTokenService.blacklist(token);

            return ResponseEntity.ok("Logout exitoso. Token invalidado.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }
}
