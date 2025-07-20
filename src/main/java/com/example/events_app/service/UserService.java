package com.example.events_app.service;


import com.example.events_app.dto.user.*;
import com.example.events_app.entity.User;
import com.example.events_app.exceptions.AlreadyExistsException;
import com.example.events_app.exceptions.NoSuchException;
import com.example.events_app.filter.UserSpecification;
import com.example.events_app.mapper.user.UserMapper;
import com.example.events_app.mapper.user.UserRegisterRequestMapper;
import com.example.events_app.mapper.user.UserRegisterResponseMapper;
import com.example.events_app.model.SortDirection;
import com.example.events_app.repository.EventParticipantRepository;
import com.example.events_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRegisterRequestMapper userRegisterRequestMapper;
    private final UserRegisterResponseMapper userRegisterResponseMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder; // Добавьте это

    @Transactional
    public List<UserRegistrationResponseDto> getAllUsers() {
        log.info("Get all Users");
        if (userRepository.findAll().isEmpty()) {
            throw new NoSuchException("No users");
        }
        return userRepository.findAll().stream().map(userRegisterResponseMapper::toRegistrationResponseDto).collect(Collectors.toList());
    }

    @Transactional
    public UserRegistrationResponseDto getUserById(Integer userId) {
        log.info("Get user by id: {} ", userId);
        Optional<User> userOptional = Optional.ofNullable(userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchException("There is no user with ID = " + userId + " in Database")));
        return userRegisterResponseMapper.toRegistrationResponseDto(userOptional.get());
    }

    @Transactional
    public UserRegistrationResponseDto saveUser(UserRegistrationRequestDto userRegistrationRequestDto) {
        log.info("Saving user: {}", userRegistrationRequestDto);
        User saveUser = userRepository.save(userRegisterRequestMapper.toEntity(userRegistrationRequestDto));
        return userRegisterResponseMapper.toRegistrationResponseDto(saveUser);

    }

    @Transactional
    public UserRegistrationResponseDto changeUser(Integer userId, UserUpdateRequestDTO updateDto) {
        // 1. Находим пользователя
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchException("There is no user with ID = " + userId + " in Database"));

        // 2. Проверка и обновление логина (если передан)
        if (updateDto.getLogin() != null && !updateDto.getLogin().isEmpty()) {
            if (!user.getLogin().equals(updateDto.getLogin())) {
                if (userRepository.findByLogin(updateDto.getLogin()).isPresent()) {
                    throw new AlreadyExistsException("Login '" + updateDto.getLogin() + "' is already taken");
                }
                user.setLogin(updateDto.getLogin());
            }
        }

        // 3. Обновляем остальные поля, если они переданы
        if (updateDto.getFullName() != null) {
            user.setFullName(updateDto.getFullName());
        }


        if (updateDto.getPassword() != null && !updateDto.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(updateDto.getPassword());
            user.setPassword(encodedPassword);
        }

        if (updateDto.getRole() != null) {
            user.setRole(updateDto.getRole());
        }

        if (updateDto.getPhoneNumber() != null) {
            user.setPhoneNumber(updateDto.getPhoneNumber());
        }

        if (updateDto.getEmail() != null) {
            user.setEmail(updateDto.getEmail());
        }

        // 4. Сохраняем обновленного пользователя
        User updatedUser = userRepository.save(user);

        // 5. Сохраняем и возвращаем результат
        return userRegisterResponseMapper.toRegistrationResponseDto(userRepository.save(updatedUser));
    }

    @Transactional
    public void deleteUser(Integer userId) {
        log.info("Delete user");
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchException("There is no user with ID = " + userId + " in Database");
        }
        userRepository.deleteById(userId);
    }

    public Page<UserDTO> searchUsers(UserFilterDTO filter) {
        Sort sort = filter.getSortOrder() == SortDirection.ASC
                ? Sort.by(filter.getSortBy()).ascending()
                : Sort.by(filter.getSortBy()).descending();

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);
        return userRepository.findAll(UserSpecification.withFilter(filter), pageable)
                .map(userMapper::toDto);
    }
}
