package com.example.events_app.mapper;

import com.example.events_app.dto.*;
import com.example.events_app.entity.Event;
import com.example.events_app.entity.EventParticipant;
import com.example.events_app.entity.EventType;
import com.example.events_app.entity.User;
import com.example.events_app.service.EventService;
import com.example.events_app.service.UserService;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {UserMapper.class, EventMapper.class})
public abstract class EventTypeMapper {

    public abstract EventType toEntity(EventTypeDTO dto);
    public abstract EventTypeDTO toDto(EventType entity);
}
