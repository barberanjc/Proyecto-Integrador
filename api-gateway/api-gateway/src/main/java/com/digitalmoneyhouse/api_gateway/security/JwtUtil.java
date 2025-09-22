package com.digitalmoneyhouse.api_gateway.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    private final String secretKey = "claveSecretaMuySegura123456789012345";
    private final Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

    public boolean validateToken(String token) {
        try {
            System.out.println("🔍 Validando token en JwtUtil: " + token);
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            System.out.println("✅ Token válido");
            return true;
        } catch (JwtException e) {
            System.out.println("❌ Error al validar token: " + e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        System.out.println("📤 Extrayendo email del token: " + token);
        String email = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        System.out.println("📧 Email extraído: " + email);
        return email;
    }
}