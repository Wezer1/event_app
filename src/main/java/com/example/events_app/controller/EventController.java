package com.example.events_app.controller;

import com.example.events_app.dto.event.*;
import com.example.events_app.service.EventService;
import com.example.events_app.service.FileStorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "События", description = "CRUD операции над событиями")
public class EventController {

    private final EventService eventService;
    private final FileStorageService fileStorageService;

    @GetMapping("/")
    @Operation(summary = "Получить все события", description = "Возвращает список всех событий")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventResponseMediumDTO.class)))
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<List<EventResponseMediumDTO>> getEvents(){
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "Получить событие по ID", description = "Возвращает событие по его ID")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventResponseMediumDTO.class)))
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<EventResponseMediumDTO> getEventById(@PathVariable Integer eventId){
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
                    schema = @Schema(implementation = EventResponseMediumDTO.class),
                    examples = @ExampleObject(
                            value = "{ 'content': [ { 'id': 1, 'title': 'AI Conference', 'description': 'Discussion about AI future', 'startTime': '2025-04-10T10:00:00', 'endTime': '2025-04-10T18:00:00', 'location': 'Moscow', 'eventType': { 'id': 1, 'name': 'Conference' }, 'userId': 1 } ], 'totalElements': 1, 'totalPages': 1, 'size': 10, 'number': 0 }"
                    )
            )
    )
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<Page<EventResponseMediumDTO>> searchEvents(EventFilterDTO filter) {
        Page<EventResponseMediumDTO> eventPage = eventService.searchEvents(filter);
        return ResponseEntity.ok(eventPage);
    }

    @GetMapping("/user/search")
    @Operation(
            summary = "Search events participated by a specific user",
            description = "Returns paginated list of events that a specific user has participated in. Allows filtering by user ID.",
            externalDocs = @ExternalDocumentation(
                    description = "Example request",
                    url = "http://localhost:8080/api/events/user/search?userIdForEventFilter=5&page=0&size=10"
            ),
            parameters = {
                    @Parameter(name = "userIdForEventFilter", description = "ID of the user to find events they participate in", example = "5"),
                    @Parameter(name = "page", description = "Page number for pagination", example = "0"),
                    @Parameter(name = "size", description = "Number of results per page", example = "10")
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = EventResponseMediumDTO.class),
                    examples = @ExampleObject(
                            value = "{ 'content': [ { " +
                                    "'id': 1, " +
                                    "'title': 'AI Conference', " +
                                    "'date': '2025-04-10T10:00:00' } ], " +
                                    "'totalElements': 1, " +
                                    "'totalPages': 1, " +
                                    "'size': 10, " +
                                    "'number': 0 }"
                    )
            )
    )
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<Page<EventResponseMediumDTO>> searchEventsWithUser(@ModelAttribute EventFilterForUserDTO filter) {
        Page<EventResponseMediumDTO> result = eventService.searchEventsWithUser(filter);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{eventId}")
    @Operation(summary = "Удалить событие", description = "Удаляет событие по ID")
    @ApiResponse(responseCode = "204", description = "No Content")
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<EventResponseMediumDTO> deleteEvent(@PathVariable Integer eventId){
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Создать событие с превью", description = "Создаёт новое событие с возможностью загрузки превью")
    @ApiResponse(responseCode = "200", description = "Created")
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<EventResponseShortDTO> addEvent(
            @RequestPart("event") @Valid EventRequestDTO event,
            @RequestPart(value = "preview", required = false) MultipartFile preview) {

        if (preview != null && !preview.isEmpty()) {
            String fileName = fileStorageService.storeFile(preview);
            event.setPreview(fileName);
        }

        return ResponseEntity.ok(eventService.saveEvent(event));
    }

    @PostMapping("/{eventID}")
    @Operation(summary = "Обновить событие", description = "Обновляет данные о событии по ID")
    @ApiResponse(responseCode = "200", description = "Updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventResponseMediumDTO.class)))
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<EventResponseShortDTO> changeEvent(@PathVariable Integer eventID,
                                                             @Valid @RequestBody EventRequestDTO eventDTO) {
        return ResponseEntity.ok(eventService.changeEvent(eventID, eventDTO));
    }

    @PostMapping("/{id}/conduct")
    @Operation(summary = "Обновить статус 'проведено'", description = "Обновляет поле 'conducted'")
    @ApiResponse(responseCode = "200", description = "OK")
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<Void> updateConducted(@PathVariable Integer id, Boolean conducted) {
        eventService.updateConductedStatus(id, conducted);
        return ResponseEntity.ok().build();
    }
}