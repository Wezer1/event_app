package com.example.events_app.controller;

import com.example.events_app.dto.event.EventTypeDTO;
import com.example.events_app.dto.event.EventTypeFilterDTO;
import com.example.events_app.dto.event.EventTypeResponseDTO;
import com.example.events_app.service.EventTypeService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event-types")
@RequiredArgsConstructor
@Tag(name = "Типы событий", description = "CRUD операции над типами событий")
public class EventTypeController {

    private final EventTypeService eventTypeService;

    @GetMapping("/")
    @Operation(summary = "Получить все типы событий", description = "Возвращает список всех типов событий")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventTypeDTO.class)))
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<List<EventTypeDTO>> getAllEventTypes() {
        return ResponseEntity.ok(eventTypeService.getAllEventTypes());
    }

    @GetMapping("/{eventTypeId}")
    @Operation(summary = "Получить тип события по ID", description = "Возвращает тип события по его ID")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventTypeDTO.class)))
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<EventTypeDTO> getEventTypeById(@PathVariable Integer eventTypeId) {
        return ResponseEntity.ok(eventTypeService.getEventTypeById(eventTypeId));
    }

    @PostMapping("/")
    @Operation(summary = "Создать тип события", description = "Создаёт новый тип события")
    @ApiResponse(responseCode = "200", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventTypeDTO.class)))
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<EventTypeDTO> addEventType(@Valid @RequestBody EventTypeDTO eventTypeDTO) {
        return ResponseEntity.ok(eventTypeService.saveEventType(eventTypeDTO));
    }
    @GetMapping("/search")
    @Operation(
            summary = "Search event types by filters",
            description = "Allows searching event types by name or description. Supports pagination.",
            externalDocs = @ExternalDocumentation(
                    description = "Example request",
                    url = "http://localhost:8080/api/event-types/search?name=conference&description=large&page=0&size=10"
            ),
            parameters = {
                    @Parameter(name = "name", description = "Part of the event type name", example = "conference"),
                    @Parameter(name = "description", description = "Part of the event type description", example = "large"),
                    @Parameter(name = "page", description = "Page number for pagination", example = "0"),
                    @Parameter(name = "size", description = "Number of results per page", example = "10")
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = EventTypeDTO.class),
                    examples = @ExampleObject(
                            value = "{ 'content': [ { " +
                                    "'id': 1, " +
                                    "'name': 'Conference', " +
                                    "'description': 'Large-scale event for professionals' } ], " +
                                    "'totalElements': 1, " +
                                    "'totalPages': 1, " +
                                    "'size': 10, " +
                                    "'number': 0 }"
                    )
            )
    )
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<Page<EventTypeResponseDTO>> searchEventTypes(@ModelAttribute EventTypeFilterDTO filter) {
        Page<EventTypeResponseDTO> result = eventTypeService.findWithFilter(filter);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{eventTypeId}")
    @Operation(summary = "Обновить тип события", description = "Обновляет данные о типе события по ID")
    @ApiResponse(responseCode = "200", description = "Updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventTypeDTO.class)))
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<EventTypeDTO> updateEventType(
            @PathVariable Integer eventTypeId,
            @Valid @RequestBody EventTypeDTO eventTypeDTO) {
        return ResponseEntity.ok(eventTypeService.updateEventType(eventTypeId, eventTypeDTO));
    }

    @DeleteMapping("/{eventTypeId}")
    @Operation(summary = "Удалить тип события", description = "Удаляет тип события по ID")
    @ApiResponse(responseCode = "204", description = "No Content")
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<Void> deleteEventType(@PathVariable Integer eventTypeId) {
        eventTypeService.deleteEventType(eventTypeId);
        return ResponseEntity.noContent().build();
    }
}
