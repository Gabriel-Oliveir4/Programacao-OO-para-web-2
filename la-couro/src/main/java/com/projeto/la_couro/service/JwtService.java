package com.projeto.la_couro.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    // Trocar por segredo vindo de config segura (env/Secret Manager).
    private final Key key = Keys.hmacShaKeyFor("mude-esse-segredo-super-seguro-para-32bytes-minimo!".getBytes());

    public String generate(UUID userId, String email, String role) {
        var now = Instant.now();
        return Jwts.builder()
            .setSubject(userId.toString())
            .claim("email", email)
            .claim("role", role)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusSeconds(60L * 60 * 12))) // 12h
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public String validateAndGetSubject(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
            .parseClaimsJws(token).getBody().getSubject();
    }
}
