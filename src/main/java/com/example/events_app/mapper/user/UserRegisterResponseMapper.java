package com.example.events_app.mapper.user;

import com.example.events_app.dto.user.UserRegistrationRequestDto;
import com.example.events_app.dto.user.UserRegistrationResponseDto;
import com.example.events_app.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class UserRegisterResponseMapper {

    public abstract User toEntity(UserRegistrationRequestDto userRegistrationRequestDto);

//    @Mapping(target = "password", ignore = true)
    public abstract UserRegistrationResponseDto toRegistrationResponseDto(User user);
}
