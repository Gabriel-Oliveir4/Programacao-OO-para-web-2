package com.projeto.la_couro.security;

import com.projeto.la_couro.model.repo.UsuarioRepository;
import com.projeto.la_couro.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    private final UsuarioRepository usuarios;

    public JwtAuthFilter(JwtService jwt, UsuarioRepository usuarios) {
        this.jwt = jwt; this.usuarios = usuarios;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws IOException, jakarta.servlet.ServletException {

        String auth = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                String sub = jwt.validateAndGetSubject(token);
                var userId = UUID.fromString(sub);
                var user = usuarios.findById(userId).orElse(null);
                if (user != null && user.isAtivo()) {
                    var authTok = new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(authTok);
                }
            } catch (Exception ignored) {}
        }
        chain.doFilter(req, res);
    }
}
