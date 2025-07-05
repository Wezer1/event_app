package com.example.events_app.mapper.event;

import com.example.events_app.dto.event.EventRequestDTO;
import com.example.events_app.entity.Event;
import com.example.events_app.mapper.user.UserToShortMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {EventTypeMapper.class, UserToShortMapper.class})
public abstract class EventRequestMapper {

    @Mappings({
            @Mapping(source = "eventTypeId", target = "eventType.id"),
            @Mapping(source = "userId", target = "user.id")
    })
    public abstract Event toEntity(EventRequestDTO dto);
}
