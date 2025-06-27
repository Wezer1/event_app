package com.example.events_app.controller;

import com.example.events_app.dto.event.EventDTO;
import com.example.events_app.dto.event.EventFilterDTO;
import com.example.events_app.service.EventService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "События", description = "CRUD операции над событиями")
public class EventController {

    private final EventService eventService;

    @GetMapping("/")
    @Operation(summary = "Получить все события", description = "Возвращает список всех событий")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class)))
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<List<EventDTO>> getEvents(){
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "Получить событие по ID", description = "Возвращает событие по его ID")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class)))
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Integer eventId){
        return ResponseEntity.ok(eventService.getEventById(eventId));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search events by filters",
            description = "Allows searching events by keyword, event type ID, start date range. Supports pagination.",
            externalDocs = @ExternalDocumentation(description = "Example request", url = "http://localhost:8080/api/events/search?keyword=AI&eventTypeId=1&startDateFrom=2025-01-01T00:00:00&startDateTo=2025-12-31T23:59:59&page=0&size=10"),
            parameters = {
                    @Parameter(name = "keyword", description = "Keyword to search in title, description, location and organization name", example = "AI Conference"),
                    @Parameter(name = "eventTypeId", description = "ID of the event type", example = "1"),
                    @Parameter(name = "startDateFrom", description = "Start date filter (from)", example = "2025-01-01T00:00:00"),
                    @Parameter(name = "startDateTo", description = "Start date filter (to)", example = "2025-12-31T23:59:59"),
                    @Parameter(name = "page", description = "Page number", example = "0"),
                    @Parameter(name = "size", description = "Page size", example = "10")
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = EventDTO.class),
                    examples = @ExampleObject(
                            value = "{ 'content': [ { 'id': 1, 'title': 'AI Conference', 'description': 'Discussion about AI future', 'startTime': '2025-04-10T10:00:00', 'endTime': '2025-04-10T18:00:00', 'location': 'Moscow', 'eventType': { 'id': 1, 'name': 'Conference' }, 'userId': 1 } ], 'totalElements': 1, 'totalPages': 1, 'size': 10, 'number': 0 }"
                    )
            )
    )
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<Page<EventDTO>> searchEvents(EventFilterDTO filter) {
        Page<EventDTO> eventPage = eventService.searchEvents(filter);
        return ResponseEntity.ok(eventPage);
    }

    @DeleteMapping("/{eventId}")
    @Operation(summary = "Удалить событие", description = "Удаляет событие по ID")
    @ApiResponse(responseCode = "204", description = "No Content")
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<EventDTO> deleteEvent(@PathVariable Integer eventId){
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/")
    @Operation(summary = "Создать событие", description = "Создаёт новое событие")
    @ApiResponse(responseCode = "200", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class)))
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<EventDTO> addEvent(@Valid @RequestBody EventDTO eventDTO){
        return ResponseEntity.ok(eventService.saveEvent(eventDTO));
    }

    @PostMapping("/{eventID}")
    @Operation(summary = "Обновить событие", description = "Обновляет данные о событии по ID")
    @ApiResponse(responseCode = "200", description = "Updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class)))
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<EventDTO> changeEvent(@PathVariable Integer eventID,
                                                @Valid @RequestBody EventDTO eventDTO){
        return ResponseEntity.ok(eventService.changeEvent(eventID, eventDTO));
    }

    @PostMapping("/{id}/conducted")
    @Operation(summary = "Обновить статус 'проведено'", description = "Обновляет поле 'conducted'")
    @ApiResponse(responseCode = "200", description = "OK")
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<Void> updateConducted(@PathVariable Integer id, Boolean conducted) {
        eventService.updateConductedStatus(id, conducted);
        return ResponseEntity.ok().build();
    }
}