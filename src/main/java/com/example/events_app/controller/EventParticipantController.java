package com.example.events_app.controller;

import com.example.events_app.dto.event.EventParticipantDTO;
import com.example.events_app.dto.RegisterOrUnregisterRequest;
import com.example.events_app.dto.event.EventParticipantFilterDTO;
import com.example.events_app.dto.event.EventParticipantResponseDTO;
import com.example.events_app.service.EventParticipantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    @PreAuthorize("hasAuthority('users:read')")
    public List<EventParticipantDTO> getByEvent(@PathVariable Integer eventId) {
        return eventParticipantService.getParticipantsByEventId(eventId);
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search event participants by filters",
            description = "Allows searching event participants by user ID, event ID, status, membership status and registration date range. Supports pagination.",
            externalDocs = @ExternalDocumentation(
                    description = "Example request",
                    url = "http://localhost:8080/api/participants/search?userId=5&eventId=100&status=CONFIRMED&membershipStatus=VALID&createdAtFrom=2025-01-01T00:00:00&createdAtTo=2025-04-30T23:59:59&page=0&size=10"
            ),
            parameters = {
                    @Parameter(name = "userId", description = "ID of the participant user", example = "5"),
                    @Parameter(name = "eventId", description = "ID of the event", example = "100"),
                    @Parameter(name = "status", description = "Registration status of the participant", example = "CONFIRMED"),
                    @Parameter(name = "membershipStatus", description = "Validity status of the participation", example = "VALID"),
                    @Parameter(name = "createdAtFrom", description = "Filter by registration date (from)", example = "2025-01-01T00:00:00"),
                    @Parameter(name = "createdAtTo", description = "Filter by registration date (to)", example = "2025-04-30T23:59:59"),
                    @Parameter(name = "page", description = "Page number for pagination", example = "0"),
                    @Parameter(name = "size", description = "Number of results per page", example = "10")
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = EventParticipantDTO.class),
                    examples = @ExampleObject(
                            value = "{ 'content': [ { " +
                                    "'userId': { 'id': 5, 'fullName': 'Иван Иванов' }, " +
                                    "'eventId': { 'id': 100, 'title': 'AI Конференция' }, " +
                                    "'status': 'CONFIRMED', " +
                                    "'membershipStatus': 'VALID', " +
                                    "'createdAt': '2025-04-05T12:00:00' } ], " +
                                    "'totalElements': 1, " +
                                    "'totalPages': 1, " +
                                    "'size': 10, " +
                                    "'number': 0 }"
                    )
            )
    )
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<Page<EventParticipantResponseDTO>> searchParticipants(EventParticipantFilterDTO filter) {
        Page<EventParticipantResponseDTO> result = eventParticipantService.findWithFilter(filter);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/users/{userId}/cancelled-events/count")
    @Operation(
            summary = "Get count of user's cancelled events",
            description = "Returns total count of events that user has cancelled",
            parameters = {
                    @Parameter(name = "userId", description = "ID of the user", example = "5")
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Long.class),
                    examples = @ExampleObject(value = "3")
            )
    )
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<Long> getCancelledEventsCount(
            @PathVariable Integer userId) {

        long count = eventParticipantService.countCancelledEventsByUser(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/users/{userId}/events/count")
    @Operation(
            summary = "Get count of user's confirmed events",
            description = "Returns total count of events where user has CONFIRMED participation status",
            parameters = {
                    @Parameter(name = "userId", description = "ID of the user", example = "5")
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Long.class),
                    examples = @ExampleObject(value = "42")
            )
    )
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<Long> getUserConfirmedEventsCount(
            @PathVariable Integer userId) {

        long count = eventParticipantService.countUserConfirmedEvents(userId);
        return ResponseEntity.ok(count);
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
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<Void> unregister(@RequestBody RegisterOrUnregisterRequest request) {
        eventParticipantService.removeParticipant(request.getUserId(), request.getEventId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    @Operation(summary = "Записаться на событие", description = "Позволяет пользователю подать заявку на участие в событии")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventParticipantDTO.class)))
    @PreAuthorize("hasAuthority('users:read')")
    public EventParticipantDTO register(@RequestBody RegisterOrUnregisterRequest request) {
        return eventParticipantService.registerUserForEvent(request.getUserId(), request.getEventId());
    }
}