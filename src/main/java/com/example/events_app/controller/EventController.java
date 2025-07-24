package com.example.events_app.controller;

import com.example.events_app.dto.event.*;
import com.example.events_app.service.EventService;
import com.example.events_app.service.FileStorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
            description = "Allows searching events by various criteria including keywords, event type, date range. Supports pagination and sorting.",
            externalDocs = @ExternalDocumentation(
                    description = "Example request with all parameters",
                    url = "http://localhost:8080/api/events/search?" +
                            "keyword=AI&eventTypeId=1&startDateFrom=2025-01-01T00:00:00&" +
                            "startDateTo=2025-12-31T23:59:59&sortBy=startTime&sortOrder=ASC&page=0&size=10"
            ),
            parameters = {
                    @Parameter(name = "keyword",
                            description = "Keyword to search in title, description, location and organization name",
                            example = "AI Conference"),
                    @Parameter(name = "eventTypeId",
                            description = "ID of the event type",
                            example = "1"),
                    @Parameter(name = "startDateFrom",
                            description = "Start date filter (from)",
                            example = "2025-01-01T00:00:00"),
                    @Parameter(name = "startDateTo",
                            description = "Start date filter (to)",
                            example = "2025-12-31T23:59:59"),
                    @Parameter(name = "endDateFrom",
                            description = "End date filter (from)",
                            example = "2025-01-01T00:00:00"),
                    @Parameter(name = "endDateTo",
                            description = "End date filter (to)",
                            example = "2025-12-31T23:59:59"),
                    @Parameter(name = "userId",
                            description = "Optional user ID filter",
                            example = "5"),
                    @Parameter(name = "sortBy",
                            description = "Field to sort by (default: title)",
                            example = "startTime"),
                    @Parameter(name = "sortOrder",
                            description = "Sort direction (default: DESC)",
                            example = "ASC"),
                    @Parameter(name = "page",
                            description = "Page number (0-based)",
                            example = "0"),
                    @Parameter(name = "size",
                            description = "Number of items per page (default: 50)",
                            example = "10")
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Paginated list of events with participant counts",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                        {
                          "content": [
                            {
                              "id": 1,
                              "title": "AI Conference",
                              "description": "Discussion about AI future",
                              "startTime": "2025-04-10T10:00:00",
                              "endTime": "2025-04-10T18:00:00",
                              "location": "Moscow",
                              "createdAt": "2024-01-15T09:30:00",
                              "eventType": {
                                "id": 1,
                                "name": "Conference"
                              },
                              "owner": {
                                "id": 1,
                                "fullName": "Ivan",
                              },
                              "preview": "/images/events/1/preview.jpg",
                              "totalVisitors": 150,
                              "conducted": false
                            }
                          ],
                          "totalElements": 1,
                          "totalPages": 1,
                          "size": 10,
                          "number": 0
                        }
                        """
                    )
            )
    )
    @ApiResponse(
            responseCode = "403",
            description = "Forbidden - missing required permissions"
    )
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<Page<EventResponseMediumWithOutImagesDTO>> searchEvents(EventFilterDTO filter) {
        Page<EventResponseMediumWithOutImagesDTO> eventPage = eventService.searchEvents(filter);
        return ResponseEntity.ok(eventPage);
    }

    @GetMapping("/user/search")
    @Operation(
            summary = "Search events participated by a specific user",
            description = "Returns paginated list of events that a specific user has participated in. Allows filtering by event criteria and user ID.",
            externalDocs = @ExternalDocumentation(
                    description = "Example request",
                    url = "http://localhost:8080/api/events/user/search?userIdForEventFilter=5&page=0&size=10"
            ),
            parameters = {
                    @Parameter(name = "title", description = "Filter by event title (partial match)", example = "Conference"),
                    @Parameter(name = "description", description = "Filter by event description (partial match)", example = "Annual"),
                    @Parameter(name = "dateFrom", description = "Filter events starting after this date", example = "2025-01-01T00:00:00"),
                    @Parameter(name = "dateTo", description = "Filter events starting before this date", example = "2025-12-31T23:59:59"),
                    @Parameter(name = "userIdForEventFilter", description = "ID of the user to find events they participate in", example = "5", required = true),
                    @Parameter(name = "sortBy", description = "Field to sort by (default: title)", example = "startTime"),
                    @Parameter(name = "sortOrder", description = "Sort direction (default: DESC)", example = "ASC"),
                    @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
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
                            value = """
                        {
                            "id": 1,
                            "title": "AI Conference",
                            "description": "Annual AI technologies conference",
                            "startTime": "2025-04-10T10:00:00",
                            "endTime": "2025-04-12T18:00:00",
                            "location": "Convention Center",
                            "createdAt": "2024-01-15T09:30:00",
                            "conducted": false,
                            "eventType": {
                                "id": 1,
                                "name": "Conference"
                            },
                            "owner": {
                                "id": 5,
                                "firstName": "John",
                                "lastName": "Doe"
                            },
                            "preview": "/images/events/1/preview.jpg",
                            "totalVisitors": 42
                        }
                        """
                    )
            )
    )
    @ApiResponse(
            responseCode = "403",
            description = "Forbidden - missing required permissions"
    )
    @ApiResponse(
            responseCode = "404",
            description = "User not found when userIdForEventFilter is provided"
    )
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<Page<EventResponseMediumWithOutImagesDTO>> searchEventsWithUser(@ModelAttribute EventFilterForUserDTO filter) {
        Page<EventResponseMediumWithOutImagesDTO> result = eventService.searchEventsWithUser(filter);
        return ResponseEntity.ok(result);
    }

    // Добавьте этот класс для лучшего отображения структуры ответа в Swagger

    @DeleteMapping("/{eventId}")
    @Operation(summary = "Удалить событие", description = "Удаляет событие по ID")
    @ApiResponse(responseCode = "204", description = "No Content")
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<EventResponseMediumDTO> deleteEvent(@PathVariable Integer eventId){
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Создать событие с изображениями",
            description = "Создаёт новое событие с превью и до 10 дополнительными изображениями")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Событие успешно создано",
                    content = @Content(schema = @Schema(implementation = EventResponseShortDTO.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<EventResponseShortDTO> addEventWithImages(
            @Parameter(description = "Данные события в формате JSON", required = true,
                    examples = {
                            @ExampleObject(
                                    name = "Пример запроса",
                                    value = "{\n" +
                                            "\"title\":\"Конференция по Spring Boot\",\n" +
                                            "\"description\":\"Ежегодная конференция для разработчиков\",\n" +
                                            "\"startTime\":\"2025-12-15T09:00:00\",\n" +
                                            "\"endTime\":\"2025-12-15T18:00:00\",\n" +
                                            "\"location\":\"Москва, Крокус Сити Холл\",\n" +
                                            "\"eventTypeId\":24,\n" +
                                            "\"userId\":55\n" +
                                            "}"
                            )
                    })
            @RequestPart("event") @Valid EventRequestDTO event,

            @Parameter(description = "Превью-изображение события (необязательное)")
            @RequestPart(value = "preview", required = false) MultipartFile preview,

            @Parameter(description = "Дополнительные изображения (максимум 10)")
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        if (images != null && images.size() > 10) {
            throw new IllegalArgumentException("Maximum 10 additional images allowed");
        }

        EventResponseShortDTO result = eventService.saveEventWithImages(event, preview, images);
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/{eventID}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Обновить событие",
            description = "Обновляет данные о событии по ID, включая изображения")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Событие успешно обновлено",
                    content = @Content(schema = @Schema(implementation = EventResponseMediumDTO.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "404", description = "Событие не найдено"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<EventResponseShortDTO> changeEvent(
            @Parameter(description = "ID события для обновления", required = true)
            @PathVariable Integer eventID,

            @Parameter(description = "Обновленные данные события в формате JSON", required = true,
                    examples = {
                            @ExampleObject(
                                    name = "Пример запроса",
                                    value = "{\n" +
                                            "\"title\":\"Обновленная конференция по Spring Boot\",\n" +
                                            "\"description\":\"Новое описание конференции\",\n" +
                                            "\"startTime\":\"2025-12-16T10:00:00\",\n" +
                                            "\"endTime\":\"2025-12-16T19:00:00\",\n" +
                                            "\"location\":\"Москва, Сколково\",\n" +
                                            "\"eventTypeId\":25,\n" +
                                            "\"userId\":55,\n" +
                                            "\"currentImages\":[\"uploads/images/3d03ad21-7e0d-425a-9b10-811318391c80.jpg\"]\n" +
                                            "}"
                            )
                    })
            @RequestPart("event") @Valid EventRequestToUpdateDTO eventDTO,

            @Parameter(description = "Новое превью-изображение (необязательное)")
            @RequestPart(value = "preview", required = false) MultipartFile preview,

            @Parameter(description = "Новые дополнительные изображения (максимум 10)")
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        if (images != null && images.size() > 10) {
            throw new IllegalArgumentException("Maximum 10 additional images allowed");
        }

        return ResponseEntity.ok(eventService.changeEvent(eventID, eventDTO, preview, images));
    }

    @PostMapping("/{id}/conduct")
    @Operation(summary = "Обновить статус 'проведено'", description = "Обновляет поле 'conducted'")
    @ApiResponse(responseCode = "200", description = "OK")
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<?> updateConducted(@PathVariable Integer id,
                                                                           @RequestParam boolean conducted) {
        EventService.BonusResponse response = eventService.updateConductedStatus(id, conducted);

        if (response == null) {
            return ResponseEntity.ok().body(Collections.singletonMap("message", "Status not changed"));
        }

        return ResponseEntity.ok(response);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleNotFound(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleBadRequest(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}