package com.example.events_app.mapper.event;

import com.example.events_app.dto.event.EventParticipantDTO;
import com.example.events_app.dto.event.EventParticipantResponseDTO;
import com.example.events_app.entity.EventParticipant;
import com.example.events_app.mapper.user.UserToShortMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {UserToShortMapper.class, EventForParticipantMapper.class})
public abstract class EventParticipantResponseMapper {

    // Преобразование DTO -> Entity
    @Mappings({
            @Mapping(source = "userId.id", target = "id.userId"),
            @Mapping(source = "eventId.id", target = "id.eventId"),
            @Mapping(source = "status", target = "status"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "membershipStatus", target = "membershipStatus") // ✅ Добавлено
    })
    public abstract EventParticipant toEntity(EventParticipantDTO dto);

    // Преобразование Entity -> DTO
    @Mappings({
            @Mapping(source = "id.userId", target = "userId.id"),
            @Mapping(source = "id.eventId", target = "eventId.id"),
            @Mapping(source = "user", target = "userId"),   // ← вызывает UserToShortMapper.userToUserShortDTO()
            @Mapping(source = "event", target = "eventId"), // ← вызывает EventToShortMapper.eventToEventShortDTO()
            @Mapping(source = "status", target = "status"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "membershipStatus", target = "membershipStatus") // ✅ Добавлено
    })
    public abstract EventParticipantResponseDTO toDto(EventParticipant entity);
}