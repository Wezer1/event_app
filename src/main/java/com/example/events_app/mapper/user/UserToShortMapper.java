package com.example.events_app.mapper.user;

import com.example.events_app.dto.user.UserDTO;
import com.example.events_app.dto.user.UserShortDTO;
import com.example.events_app.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserToShortMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "fullName", target = "fullName"),

    })
    UserShortDTO userToUserShortDTO(User user);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "fullName", target = "fullName"),
    })
    UserShortDTO userDtoToUserShortDTO(UserDTO userDTO);
}
