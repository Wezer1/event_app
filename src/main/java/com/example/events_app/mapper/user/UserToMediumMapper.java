package com.example.events_app.mapper.user;

import com.example.events_app.dto.user.UserMediumDTO;
import com.example.events_app.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserToMediumMapper {
    UserToMediumMapper INSTANCE = Mappers.getMapper(UserToMediumMapper.class);

    UserMediumDTO userToUserMediumDTO(User user);
}
