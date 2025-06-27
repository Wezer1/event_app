package com.example.events_app.mapper.user;

import com.example.events_app.dto.user.UserDTO;
import com.example.events_app.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    public abstract User toEntity(UserDTO userDTO);

    public abstract UserDTO toDto(User user);
}
