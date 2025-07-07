package com.example.events_app.config;

import com.example.events_app.dto.user.UserRegistrationRequestDto;
import com.example.events_app.mapper.user.UserRegisterRequestMapper;
import com.example.events_app.model.Role;
import com.example.events_app.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserDataLoader implements ApplicationRunner {

    private final UserRepository userRepository;
    private final UserRegisterRequestMapper userRegisterRequestMapper;

    @Override
    public void run(ApplicationArguments args) {
        // Создаем первого пользователя (USER)
        UserRegistrationRequestDto user1 = new UserRegistrationRequestDto(
                "User Userov",   // full_name
                "user",          // login
                "user",          // password
                Role.USER,        // role
                "user",
                "user"
        );

        // Создаем второго пользователя (ADMIN)
        UserRegistrationRequestDto user2 = new UserRegistrationRequestDto(
                "Admin Adminov",
                "admin",
                "admin",
                Role.ADMIN,
                "admin",
                "admin"
        );

        // Сохраняем, если ещё не существует
        if (userRepository.findByLogin(user1.getLogin()).isEmpty()) {
            userRepository.save(userRegisterRequestMapper.toEntity(user1));
            System.out.println("Создан пользователь: " + user1.getLogin());
        }

        if (userRepository.findByLogin(user2.getLogin()).isEmpty()) {
            userRepository.save(userRegisterRequestMapper.toEntity(user2));
            System.out.println("Создан пользователь: " + user2.getLogin());
        }
    }
}