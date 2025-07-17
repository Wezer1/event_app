package com.example.events_app.mapper.images.event;

import com.example.events_app.dto.event_pictures.EventImageDTO;
import com.example.events_app.entity.EventImage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface ImageMappingSupport {
    default List<EventImageDTO> mapImages(List<EventImage> images, EventImageMapper imageMapper) {
        if (images == null) {
            return Collections.emptyList();
        }
        return images.stream()
                .map(imageMapper::toDto)
                .collect(Collectors.toList());
    }
}