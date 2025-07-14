package com.example.events_app.controller;

import com.example.events_app.dto.bonus.UserBonusHistoryFilterDTO;
import com.example.events_app.dto.bonus.UserBonusHistoryRequestDTO;
import com.example.events_app.dto.bonus.UserBonusHistoryResponseMediumDTO;
import com.example.events_app.dto.bonus.UserBonusHistoryResponseShortDTO;
import com.example.events_app.entity.UserBonusHistory;
import com.example.events_app.service.UserBonusHistoryService;
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
@RequestMapping("/api/user-bonus-history")
@RequiredArgsConstructor
@Tag(name = "История бонусов", description = "CRUD операции над историей начисления бонусов пользователям")
public class UserBonusHistoryController {

    private final UserBonusHistoryService userBonusHistoryService;

    @GetMapping("/user/{id}")
    @Operation(summary = "Получить историю бонусов пользователя", description = "Возвращает список бонусов для конкретного пользователя")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserBonusHistoryResponseShortDTO.class)))
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<List<UserBonusHistoryResponseShortDTO>> getAllByUserId(@PathVariable Integer id) {
        return ResponseEntity.ok(userBonusHistoryService.getAllByUserId(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить запись истории бонуса по ID", description = "Возвращает конкретную запись начисления бонуса")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserBonusHistoryResponseShortDTO.class)))
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<UserBonusHistoryResponseMediumDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(userBonusHistoryService.getById(id));
    }

    @PostMapping("/")
    @Operation(summary = "Создать запись истории бонуса", description = "Создаёт новую запись начисления бонуса пользователю")
    @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserBonusHistoryResponseShortDTO.class)))
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<UserBonusHistoryResponseShortDTO> create(@Valid @RequestBody UserBonusHistoryRequestDTO dto) {
        return ResponseEntity.ok(userBonusHistoryService.create(dto));
    }

    @PostMapping("/{id}")
    @Operation(summary = "Обновить запись истории бонуса", description = "Обновляет существующую запись начисления бонуса")
    @ApiResponse(responseCode = "200", description = "Updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserBonusHistoryResponseShortDTO.class)))
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<UserBonusHistoryResponseShortDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody UserBonusHistoryRequestDTO dto) {
        return ResponseEntity.ok(userBonusHistoryService.update(id, dto));
    }

    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserBonusHistory.class),
                    examples = @ExampleObject(
                            value = "{ 'content': [ { " +
                                    "'id': 1, " +
                                    "'amount': 100, " +
                                    "'reason': 'Referral bonus', " +
                                    "'createdAt': '2025-04-05T12:00:00', " +
                                    "'isActive': true } ], " +
                                    "'totalElements': 1, " +
                                    "'totalPages': 1, " +
                                    "'size': 10, " +
                                    "'number': 0 }"
                    )
            )
    )

    @GetMapping("/search")
    @Operation(
            summary = "Search user bonus history by filters",
            description = "Allows searching user bonus history by user ID, bonus type ID, activity status and date range. Supports pagination.",
            externalDocs = @ExternalDocumentation(
                    description = "Example request",
                    url = "http://localhost:8080/api/user-bonus/search?userId=5&bonusTypeId=10&isActive=true&createdAtFrom=2025-01-01T00:00:00&createdAtTo=2025-04-30T23:59:59&page=0&size=10"
            ),
            parameters = {
                    @Parameter(name = "userId", description = "ID of the user", example = "5"),
                    @Parameter(name = "bonusTypeId", description = "ID of the bonus type", example = "10"),
                    @Parameter(name = "isActive", description = "Activity status of the record", example = "true"),
                    @Parameter(name = "createdAtFrom", description = "Filter by creation date (from)", example = "2025-01-01T00:00:00"),
                    @Parameter(name = "createdAtTo", description = "Filter by creation date (to)", example = "2025-04-30T23:59:59"),
                    @Parameter(name = "page", description = "Page number for pagination", example = "0"),
                    @Parameter(name = "size", description = "Number of results per page", example = "10")
            }
    )
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<Page<UserBonusHistoryResponseShortDTO>> searchUserBonuses(@ModelAttribute UserBonusHistoryFilterDTO filter) {
        Page<UserBonusHistoryResponseShortDTO> result = userBonusHistoryService.findWithFilter(filter);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить запись истории бонуса", description = "Удаляет запись начисления бонуса по ID")
    @ApiResponse(responseCode = "204", description = "No Content")
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        userBonusHistoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/activate")
    @Operation(summary = "Активировать бонус", description = "Делает бонус активным")
    @ApiResponse(responseCode = "200", description = "OK")
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<Void> activate(@PathVariable Integer id) {
        userBonusHistoryService.activateBonus(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/deactivate")
    @Operation(summary = "Деактивировать бонус", description = "Делает бонус неактивным")
    @ApiResponse(responseCode = "200", description = "OK")
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<Void> deactivate(@PathVariable Integer id) {
        userBonusHistoryService.deactivateBonus(id);
        return ResponseEntity.ok().build();
    }
}
