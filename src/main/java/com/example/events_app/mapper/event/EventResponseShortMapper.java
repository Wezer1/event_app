package com.example.events_app.mapper.event;

import com.example.events_app.dto.event.EventResponseMediumDTO;
import com.example.events_app.dto.event.EventResponseShortDTO;
import com.example.events_app.dto.event_pictures.EventImageDTO;
import com.example.events_app.entity.Event;
import com.example.events_app.entity.EventImage;
import com.example.events_app.mapper.images.event.EventImageMapper;
import com.example.events_app.mapper.images.event.ImageMappingSupport;
import com.example.events_app.mapper.user.UserToMediumMapper;
import com.example.events_app.mapper.user.UserToShortMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = {EventTypeMapper.class, UserToShortMapper.class, EventImageMapper.class})
public abstract class EventResponseShortMapper {

    @Mappings({
            @Mapping(source = "eventType", target = "eventType"),
            @Mapping(source = "user", target = "user"),
            @Mapping(source = "preview", target = "preview"),
            @Mapping(source = "images", target = "images") // Добавили маппинг изображений
    })
    public abstract EventResponseShortDTO toDto(Event event);

    @Mappings({
            @Mapping(source = "eventType", target = "eventType"),
            @Mapping(source = "user.id", target = "user.id"),
            @Mapping(target = "images", ignore = true) // Игнорируем при обратном маппинге
    })
    public abstract Event toEntity(EventResponseShortDTO eventDTO);
}