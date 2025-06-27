package com.example.events_app.controller;

import com.example.events_app.dto.event.EventParticipantDTO;
import com.example.events_app.dto.RegisterOrUnregisterRequest;
import com.example.events_app.service.EventParticipantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/participants")
@Tag(name = "Запись на мероприятия", description = "Методы для управления записью пользователей на мероприятия")
public class EventParticipantController {

    private final EventParticipantService eventParticipantService;

    @GetMapping("/")
    @Operation(summary = "Получить всех участников", description = "Возвращает список всех заявок на мероприятия")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventParticipantDTO.class)))
    @PreAuthorize("hasAuthority('users:write')")
    public List<EventParticipantDTO> getAll() {
        return eventParticipantService.getAllParticipants();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Получить заявки пользователя", description = "Возвращает список мероприятий, на которые записан пользователь")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventParticipantDTO.class)))
    @PreAuthorize("hasAuthority('users:read')")
    public List<EventParticipantDTO> getByUser(@PathVariable Integer userId) {
        return eventParticipantService.getParticipantsByUserId(userId);
    }

    @GetMapping("/event/{eventId}")
    @Operation(summary = "Получить участников события", description = "Возвращает список пользователей, записавшихся на событие")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventParticipantDTO.class)))
    @PreAuthorize("hasAuthority('users:write')")
    public List<EventParticipantDTO> getByEvent(@PathVariable Integer eventId) {
        return eventParticipantService.getParticipantsByEventId(eventId);
    }

    @PostMapping("/{userId}/{eventId}")
    @Operation(summary = "Обновить статус заявки", description = "Обновляет статус заявки пользователя на событие")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventParticipantDTO.class)))
    @PreAuthorize("hasAuthority('users:write')")
    public EventParticipantDTO updateStatus(
            @PathVariable Integer userId,
            @PathVariable Integer eventId,
            @RequestParam String status) {
        return eventParticipantService.updateParticipantStatus(userId, eventId, status);
    }

    @PostMapping("/unregister")
    @Operation(summary = "Отменить запись на событие", description = "Отменяет запись пользователя на мероприятие")
    @ApiResponse(responseCode = "200", description = "OK")
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<Void> unregister(@RequestBody RegisterOrUnregisterRequest request) {
        eventParticipantService.removeParticipant(request.getUserId(), request.getEventId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    @Operation(summary = "Записаться на событие", description = "Позволяет пользователю подать заявку на участие в событии")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventParticipantDTO.class)))
    @PreAuthorize("hasAuthority('users:write')")
    public EventParticipantDTO register(@RequestBody RegisterOrUnregisterRequest request) {
        return eventParticipantService.registerUserForEvent(request.getUserId(), request.getEventId());
    }
}