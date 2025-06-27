package com.example.events_app.mapper.event;

import com.example.events_app.dto.event.EventDTO;
import com.example.events_app.entity.Event;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = EventTypeMapper.class)
public abstract class EventMapper {

    public abstract Event toEntity(EventDTO eventDTO);

    public abstract EventDTO toDto(Event event);
}
