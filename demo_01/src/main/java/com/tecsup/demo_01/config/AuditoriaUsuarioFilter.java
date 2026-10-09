package com.tecsup.demo_01.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Lee el encabezado "X-Usuario" (si viene) y lo deja disponible para la
 * auditoría durante la petición. Si no viene, AuditoriaService usa el
 * usuario por defecto configurado (auditoria.usuario).
 */
@Component
public class AuditoriaUsuarioFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        UsuarioActualHolder.set(request.getHeader("X-Usuario"));
        try {
            filterChain.doFilter(request, response);
        } finally {
            UsuarioActualHolder.clear();
        }
    }
}