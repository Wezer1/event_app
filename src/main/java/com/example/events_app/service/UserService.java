package com.example.events_app.service;


import com.example.events_app.dto.UserDTO;
import com.example.events_app.dto.UserRegistrationRequestDto;
import com.example.events_app.dto.UserRegistrationResponseDto;
import com.example.events_app.entity.User;
import com.example.events_app.exceptions.NoSuchException;
import com.example.events_app.mapper.UserMapper;
import com.example.events_app.mapper.UserRegisterRequestMapper;
import com.example.events_app.mapper.UserRegisterResponseMapper;
import com.example.events_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional
    public List<UserRegistrationResponseDto> getAllUsers() {
        log.info("Get all Users");
        if(userRepository.findAll().isEmpty()){
            throw new NoSuchException("No users");
        }
        return userRepository.findAll().stream().map(userRegisterResponseMapper :: toRegistrationResponseDto).collect(Collectors.toList());
    }

    @Transactional
    public UserRegistrationResponseDto getUserById(Integer userId) {
        log.info("Get user by id: {} ", userId);
        Optional<User> userOptional = Optional.ofNullable(userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchException("There is no user with ID = "+ userId + " in Database")));
        return userRegisterResponseMapper.toRegistrationResponseDto(userOptional.get());
    }

    @Transactional
    public UserRegistrationResponseDto saveUser(UserRegistrationRequestDto userRegistrationRequestDto) {
        log.info("Saving user: {}", userRegistrationRequestDto);
        User saveUser = userRepository.save(userRegisterRequestMapper.toEntity(userRegistrationRequestDto));
        return userRegisterResponseMapper.toRegistrationResponseDto(saveUser);

    }

    @Transactional
    public UserRegistrationResponseDto changeUser(Integer userId, UserRegistrationRequestDto userRegistrationRequestDto){
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()){
            throw new NoSuchException("There is no user with ID = "+ userId + " in Database");
        }else{
            User existingUser = optionalUser.get();
            existingUser.setFirstName(userRegistrationRequestDto.getFirstName());
            existingUser.setLastName(userRegistrationRequestDto.getLastName());
            existingUser.setPatronymic(userRegistrationRequestDto.getPatronymic());
            existingUser.setLogin(userRegistrationRequestDto.getLogin());
            existingUser.setPassword(userRegistrationRequestDto.getPassword());
            existingUser.setRole(userRegistrationRequestDto.getRole());

            return userRegisterResponseMapper.toRegistrationResponseDto(userRepository.save(existingUser));
        }
    }

    @Transactional
    public void deleteUser(Integer userId) {
        log.info("Delete user");
        if(userRepository.findById(userId).isEmpty()){
            throw new NoSuchException("There is no user with ID = "+ userId + " in Database");
        }
        userRepository.deleteById(userId);
    }
}
