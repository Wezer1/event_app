package com.example.events_app.mapper;

import com.example.events_app.dto.EventCreateDTO;
import com.example.events_app.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = EventTypeMapper.class)
public abstract class EventCreateDTOMapper {

    @Mapping(target = "preview", ignore = true) // игнорируем MultipartFile при маппинге
    public abstract Event toEntity(EventCreateDTO eventDTO);
}
