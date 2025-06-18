package com.example.events_app.mapper;

import com.example.events_app.dto.EventDTO;
import com.example.events_app.entity.Event;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = EventTypeMapper.class)
public abstract class EventMapper {

    public abstract Event toEntity(EventDTO eventDTO);

    public abstract EventDTO toDto(Event event);
}
