package com.example.events_app.mapper;

import com.example.events_app.dto.EventDTO;
import com.example.events_app.dto.EventShortDTO;
import com.example.events_app.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface EventToShortMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "title", target = "title") // title -> name
    })
    EventShortDTO eventToEventShortDTO(Event event);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "title", target = "title") // title -> name
    })
    EventShortDTO eventDtoToEventShortDTO(EventDTO eventDTO);
}
