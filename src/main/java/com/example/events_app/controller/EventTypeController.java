package com.example.events_app.controller;

import com.example.events_app.dto.event.EventTypeDTO;
import com.example.events_app.service.EventTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
