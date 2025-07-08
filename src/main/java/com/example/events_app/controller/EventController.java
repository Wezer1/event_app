package com.example.events_app.controller;

import com.example.events_app.dto.event.*;
import com.example.events_app.service.EventService;
import com.example.events_app.service.FileStorageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/{eventId}")
    @Operation(summary = "Удалить событие", description = "Удаляет событие по ID")
    @ApiResponse(responseCode = "204", description = "No Content")
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<EventResponseMediumDTO> deleteEvent(@PathVariable Integer eventId){
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузить превью события", description = "Принимает файл изображения и сохраняет его на сервере")
    @ApiResponse(responseCode = "200", description = "Файл успешно загружен", content = {
            @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "/uploads/previews/event_123456789.png"))
    })
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<String> uploadPreview(@RequestPart("preview") MultipartFile file) {
        String previewUrl = fileStorageService.store(file, "event"); // ← добавлен "event" как префикс
        return ResponseEntity.ok(previewUrl);
    }

    /**
     * Создаёт новое событие с возможностью загрузки превью
     * @param dto - DTO события
     * @param preview - опциональное изображение для превью
     * @return DTO созданного события
     */
    @PostMapping(path = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Создать событие с превью", description = "Создаёт событие и прикрепляет к нему изображение")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Событие успешно создано", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = EventResponseDTO.class))
    })
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<EventResponseDTO> createEvent(
            @RequestPart("event") @Valid EventRequestDTO dto,
            @RequestPart("preview") MultipartFile preview) {

        String previewUrl = fileStorageService.store(preview, "event");
        dto.setPreview(previewUrl);

        EventResponseDTO created = eventService.saveEvent(dto);
        return ResponseEntity.ok(created);
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