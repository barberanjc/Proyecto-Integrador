package com.digitalmoney.account_service.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    private final String secretKey = "claveSecretaMuySegura123456789012345";
    private final Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

    public String extractUsername(String token) {
        System.out.println("🔍 Decodificando token en JwtUtil: " + token);
        String email = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        System.out.println("✅ Email extraído: " + email);
        return email;
    }
}