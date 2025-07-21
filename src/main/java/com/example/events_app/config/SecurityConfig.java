package com.example.events_app.config;

import com.example.events_app.security.JwtConfigurer;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtConfigurer jwtConfigurer;

    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtConfigurer jwtConfigurer, UserDetailsService userDetailsService) {
        this.jwtConfigurer = jwtConfigurer;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
                    //configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*", "http://127.0.0.1:*"));
                    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));
                    configuration.setExposedHeaders(Collections.singletonList("Authorization"));
                    configuration.setAllowCredentials(true); // если используете cookies или credentials
                    return configuration;
                }))
                .authorizeHttpRequests()
                // Публичные маршруты
//                .requestMatchers("/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers(HttpMethod.POST,"/api/users/registration","/api/auth/login").permitAll()
                .requestMatchers("/upload").permitAll()
                .requestMatchers("/api/ping").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .apply(jwtConfigurer) // Добавляет JWT фильтр перед UsernamePasswordAuthenticationFilter
                .and()
                .logout()
                .logoutUrl("/api/auth/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                });

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    // Создаем компонент отвечающий за конфигурацию пользователя AuthenticationManager
    @Bean
    public AuthenticationManager daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();//используется для проверки аутентификационных данных пользователя в базе данных
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());//передаем пароль
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);//указываем от куда получить инфу о пользователе
        return new ProviderManager(daoAuthenticationProvider);//выполняем аутентификацию пользователя
    }
}
