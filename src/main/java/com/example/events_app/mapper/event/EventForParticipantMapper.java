package com.example.events_app.mapper.event;

import com.example.events_app.dto.event.EventForParticipantDTO;
import com.example.events_app.dto.event.EventResponseMediumDTO;
import com.example.events_app.dto.event.EventShortDTO;
import com.example.events_app.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface EventForParticipantMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "title", target = "title"), // title -> name
            @Mapping(source = "location", target = "location"),
            @Mapping(source = "startTime", target = "startTime")
    })
    EventForParticipantDTO eventToEventShortDTO(Event event);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "title", target = "title"), // title -> name
            @Mapping(source = "location", target = "location"),
            @Mapping(source = "startTime", target = "startTime")
    })
    EventForParticipantDTO eventDtoToEventShortDTO(EventResponseMediumDTO eventDTO);
}
