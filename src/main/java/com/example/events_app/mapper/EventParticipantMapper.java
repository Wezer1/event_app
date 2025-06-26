package com.example.events_app.mapper;

import org.mapstruct.*;
import com.example.events_app.dto.EventParticipantDTO;
import com.example.events_app.entity.EventParticipant;

@Mapper(componentModel = "spring", uses = {UserToShortMapper.class, EventToShortMapper.class})
public abstract class EventParticipantMapper {

    // Преобразование DTO -> Entity (если нужно)
    @Mappings({
            @Mapping(source = "userId.id", target = "id.userId"),
            @Mapping(source = "eventId.id", target = "id.eventId"),
            @Mapping(source = "status", target = "status"),
            @Mapping(source = "createdAt", target = "createdAt")
    })
    public abstract EventParticipant toEntity(EventParticipantDTO dto);

    // Преобразование Entity -> DTO
    @Mappings({
            @Mapping(source = "id.userId", target = "userId.id"),
            @Mapping(source = "id.eventId", target = "eventId.id"),
            @Mapping(source = "user", target = "userId"),   // ← вызывает UserToShortMapper.userToUserShortDTO()
            @Mapping(source = "event", target = "eventId"), // ← вызывает EventToShortMapper.eventToEventShortDTO()
            @Mapping(source = "status", target = "status"),
            @Mapping(source = "createdAt", target = "createdAt")
    })
    public abstract EventParticipantDTO toDto(EventParticipant entity);
}
