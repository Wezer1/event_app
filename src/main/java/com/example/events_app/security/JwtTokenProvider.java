package com.example.events_app.security;

import com.example.events_app.entity.User;
import com.example.events_app.model.Role;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;
import java.util.stream.Collectors;

import java.util.Base64;
import java.util.Date;
import java.util.Objects;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.header}")
    private String header;

    @Value("${jwt.expiration}")
    private long validitySeconds;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }


    public String createToken(User user) {

        Claims claims = Jwts.claims().setSubject(user.getLogin());
        claims.put("role", user.getRole().name());
        claims.put("authorities", user.getRole().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        claims.put("user_id", user.getId());

        Date now = new Date();
        Date validity = new Date(now.getTime() + validitySeconds * 1000);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        StringBuilder stringBuilder = new StringBuilder(token);
        stringBuilder.insert(0, "Bearer ");
        return stringBuilder.toString();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public String resolveToken(HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader(header);
        if (Objects.isNull(bearerToken)) {
            return null;
        }

        if (bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".getBytes().length);
        } else {
            return bearerToken;
        }
    }

    public Authentication getAuthentication(String token) {
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();

        String login = claims.getSubject();
        String roleString = claims.get("role", String.class);
        Integer userId = claims.get("user_id", Integer.class);

        // Получаем список разрешений из токена
        List<String> authoritiesFromToken = claims.get("authorities", List.class);

        // Преобразуем в Spring GrantedAuthority
        List<SimpleGrantedAuthority> grantedAuthorities = authoritiesFromToken.stream()
                .map(auth -> new SimpleGrantedAuthority((String) auth))
                .collect(Collectors.toList());

        // Создаём UserDetails с правами из токена
        UserDetails userDetails = SecurityUser.fromToken(
                Role.valueOf(roleString), // преобразуем строку в enum Role
                login,
                userId,
                grantedAuthorities
        );

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                token,
                userDetails.getAuthorities()
        );
    }
}
