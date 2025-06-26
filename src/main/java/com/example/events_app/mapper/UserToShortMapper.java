package com.example.events_app.mapper;

import com.example.events_app.dto.UserDTO;
import com.example.events_app.dto.UserShortDTO;
import com.example.events_app.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserToShortMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "firstName", target = "firstName"),
            @Mapping(source = "lastName", target = "lastName")
    })
    UserShortDTO userToUserShortDTO(User user);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "firstName", target = "firstName"),
            @Mapping(source = "lastName", target = "lastName")
    })
    UserShortDTO userDtoToUserShortDTO(UserDTO userDTO);
}
