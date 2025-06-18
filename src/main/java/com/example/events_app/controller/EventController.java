package com.example.events_app.controller;

import com.example.events_app.dto.EventCreateDTO;
import com.example.events_app.dto.EventDTO;
import com.example.events_app.dto.EventFilterDTO;
import com.example.events_app.service.EventService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.Console;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            summary = "Поиск событий по фильтру",
            description = "Позволяет искать события по ключевым словам, типу мероприятия и диапазону дат"
    )
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = EventDTO.class)
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

    @PostMapping(path = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Создать событие", description = "Создаёт новое событие")
    @ApiResponse(responseCode = "200", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class)))
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<EventDTO> addEvent(
            @Parameter(description = "Данные события", required = true)
            @RequestPart("event") @Valid EventCreateDTO eventDTO,

            @Parameter(description = "Изображение для превью", required = true)
            @RequestPart("preview") MultipartFile preview) throws IOException {

        return ResponseEntity.ok(eventService.saveEvent(eventDTO, preview));
    }

    @PostMapping(path = "/{eventID}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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