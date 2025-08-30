package com.digitalmoneyhouse.auth_service.controller;

import com.digitalmoneyhouse.auth_service.security.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api")
public class TestController {

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "Endpoint protegido", description = "Devuelve acceso autorizado si el token JWT es válido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Acceso autorizado"),
            @ApiResponse(responseCode = "401", description = "Token inválido o no provisto")
    })
    @GetMapping("/protegido")
    public ResponseEntity<String> protegido(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no provisto o inválido");
        }

        String token = authHeader.substring(7);

        try {
            String email = jwtUtil.extractEmail(token);
            return ResponseEntity.ok("Acceso autorizado para el usuario: " + email);
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }
    }
}