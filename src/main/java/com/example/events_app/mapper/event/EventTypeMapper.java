package com.example.events_app.mapper.event;

import com.example.events_app.dto.event.EventTypeDTO;
import com.example.events_app.entity.EventType;
import com.example.events_app.mapper.user.UserMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, EventMapper.class})
public abstract class EventTypeMapper {

    public abstract EventType toEntity(EventTypeDTO dto);
    public abstract EventTypeDTO toDto(EventType entity);
}
