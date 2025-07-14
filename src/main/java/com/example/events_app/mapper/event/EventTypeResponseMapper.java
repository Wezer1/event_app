package com.example.events_app.mapper.event;

import com.example.events_app.dto.event.EventTypeResponseDTO;
import com.example.events_app.entity.EventType;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EventTypeResponseMapper {
    EventTypeMapper INSTANCE = Mappers.getMapper(EventTypeMapper.class);

    EventTypeResponseDTO toDto(EventType eventType);
}
