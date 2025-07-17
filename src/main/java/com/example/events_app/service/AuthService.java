package com.example.events_app.service;

import com.example.events_app.dto.user.AuthRequestDTO;
import com.example.events_app.dto.user.AuthResponseDTO;
import com.example.events_app.entity.User;
import com.example.events_app.repository.UserRepository;
import com.example.events_app.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public ResponseEntity<AuthResponseDTO> authenticate(@RequestBody AuthRequestDTO request) {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));//проводим аунтицикацию через email и пароль
            User user = userRepository.findByLogin(request.getLogin()).orElseThrow(() -> new UsernameNotFoundException("User doesn't exists"));//если аунтификация успешна, с помощью email ищем пользователя
            String token = jwtTokenProvider.createToken(user);//если пользователь есть, то создаем токен
            AuthResponseDTO authResponseDTO = new AuthResponseDTO(user.getId(),user.getFullName(),user.getLogin(), user.getRole(),token);
        return ResponseEntity.ok()
                .header("Authorization", token) // можно оставить как резерв
                .body(authResponseDTO);
    }
}
