package com.example.events_app.security;

import com.example.events_app.exceptions.JwtAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    // Список публичных маршрутов, которые НЕ требуют токена
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/users/registration",
            "/api/auth/login",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "swagger-ui/index.html",
            "/v3/api-docs"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String path = httpRequest.getRequestURI();

        // ✅ Пропускаем публичные маршруты
        if (PUBLIC_PATHS.contains(path)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String token = jwtTokenProvider.resolveToken(httpRequest);

        if (token != null) {
            if (jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    httpResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid token");
                    return;
                }
            } else {
                httpResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "Token is not valid");
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
