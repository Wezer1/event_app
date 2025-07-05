package com.example.events_app.mapper.event;

import com.example.events_app.dto.event.EventResponseMediumDTO;
import com.example.events_app.entity.Event;
import com.example.events_app.mapper.user.UserToMediumMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {EventTypeMapper.class, UserToMediumMapper.class})
public abstract class EventResponseMediumMapper {

    @Mappings({
            @Mapping(source = "eventType", target = "eventType"),
            @Mapping(source = "user", target = "user")
    })
    public abstract EventResponseMediumDTO toDto(Event event);

    @Mappings({
            @Mapping(source = "eventType", target = "eventType"),
            @Mapping(source = "user.id", target = "user.id") // или просто user, если передается UserMediumDTO
    })
    public abstract Event toEntity(EventResponseMediumDTO eventDTO);
}
