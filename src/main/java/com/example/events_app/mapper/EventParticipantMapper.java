package com.example.events_app.mapper;

import org.mapstruct.*;
import com.example.events_app.dto.EventParticipantDTO;
import com.example.events_app.entity.EventParticipant;

@Mapper(componentModel = "spring", uses = {UserMapper.class, EventMapper.class})
public abstract class EventParticipantMapper {

    @Mappings({
            @Mapping(source = "userId", target = "id.userId"),
            @Mapping(source = "eventId", target = "id.eventId"),
            @Mapping(source = "status", target = "status"),
            @Mapping(source = "createdAt", target = "createdAt")
    })
    public abstract EventParticipant toEntity(EventParticipantDTO dto);

    @Mappings({
            @Mapping(source = "id.userId", target = "userId"),
            @Mapping(source = "id.eventId", target = "eventId"),
            @Mapping(source = "status", target = "status"),
            @Mapping(source = "createdAt", target = "createdAt"),

            // ← Новые поля из связанных сущностей:
            @Mapping(source = "user.firstName", target = "firstName"),  // ← firstName + lastName
            @Mapping(source = "user.lastName", target = "lastName"),   // ← используется в fullName
            @Mapping(source = "event.title", target = "eventName")     // ← название события
    })
    public abstract EventParticipantDTO toDto(EventParticipant entity);
}
