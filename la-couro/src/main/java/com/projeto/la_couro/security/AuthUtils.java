package com.projeto.la_couro.security;

/*
 * Utilitário para extrair informações do usuário autenticado (ID) do SecurityContext a partir do JWT.
 */

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class AuthUtils {
    private AuthUtils() {}

    public static UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof UUID u) return u;
        if (principal instanceof String s) {
            try { return UUID.fromString(s); } catch (Exception ignored) {}
        }
        return null;
    }
}
