package com.example.events_app.mapper.event;

import com.example.events_app.dto.event.EventResponseMediumDTO;
import com.example.events_app.dto.event.EventResponseMediumWithOutImagesDTO;
import com.example.events_app.entity.Event;
import com.example.events_app.mapper.images.event.EventImageMapper;
import com.example.events_app.mapper.user.UserToMediumMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring",
        uses = {EventTypeMapper.class, UserToMediumMapper.class, EventImageMapper.class})
public abstract class EventResponseMediumWithOutImagesMapper {

    @Mappings({
            @Mapping(source = "eventType", target = "eventType"),
            @Mapping(source = "user", target = "user"),
            @Mapping(source = "preview", target = "preview"),
    })
    public abstract EventResponseMediumWithOutImagesDTO toDto(Event event);

    @Mappings({
            @Mapping(source = "eventType", target = "eventType"),
            @Mapping(source = "user.id", target = "user.id"),
    })
    public abstract Event toEntity(EventResponseMediumWithOutImagesDTO eventDTO);
}
