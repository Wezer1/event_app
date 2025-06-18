package com.example.events_app.mapper;

import com.example.events_app.dto.UserRegistrationRequestDto;
import com.example.events_app.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public abstract class UserRegisterRequestMapper {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Named("encodePassword")
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "password", target = "password", qualifiedByName = "encodePassword")
    public abstract User toEntity(UserRegistrationRequestDto userRegistrationRequestDto);

    @Mapping(target = "password", ignore = true)
    public abstract UserRegistrationRequestDto userRegistrationRequestDto(User user);
}
