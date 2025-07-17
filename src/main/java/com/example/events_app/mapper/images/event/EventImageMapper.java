package com.example.events_app.mapper.images.event;

import com.example.events_app.dto.event_pictures.EventImageDTO;
import com.example.events_app.entity.EventImage;
import com.example.events_app.mapper.event.EventResponseMediumMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventImageMapper {
    @Mapping(source = "event.id", target = "eventId")
    EventImageDTO toDto(EventImage entity);

    @Mapping(target = "event", ignore = true)
    EventImage toEntity(EventImageDTO dto);
}