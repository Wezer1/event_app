package com.example.events_app.controller;

import com.example.events_app.dto.bonus.BonusTypeDTO;
import com.example.events_app.dto.bonus.BonusTypeFilterDTO;
import com.example.events_app.service.BonusTypeService;
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
@RequestMapping("/api/bonus-types")
@RequiredArgsConstructor
@Tag(name = "Типы бонусов", description = "CRUD операции над типами бонусов")
public class BonusTypeController {

    private final BonusTypeService bonusTypeService;

    @GetMapping("/")
    @Operation(summary = "Получить все типы бонусов", description = "Возвращает список всех типов бонусов")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BonusTypeDTO.class)))
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<List<BonusTypeDTO>> getAllBonusTypes() {
        return ResponseEntity.ok(bonusTypeService.getAllBonusTypes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить тип бонуса по ID", description = "Возвращает тип бонуса по его ID")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BonusTypeDTO.class)))
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<BonusTypeDTO> getBonusTypeById(@PathVariable Integer id) {
        return ResponseEntity.ok(bonusTypeService.getBonusTypeById(id));
    }

    @PostMapping("/")
    @Operation(summary = "Создать тип бонуса", description = "Создаёт новый тип бонуса")
    @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BonusTypeDTO.class)))
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<BonusTypeDTO> createBonusType(@Valid @RequestBody BonusTypeDTO dto) {
        return ResponseEntity.ok(bonusTypeService.createBonusType(dto));
    }

    @PostMapping("/{id}")
    @Operation(summary = "Обновить тип бонуса", description = "Обновляет данные о типе бонуса по ID")
    @ApiResponse(responseCode = "200", description = "Updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BonusTypeDTO.class)))
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<BonusTypeDTO> updateBonusType(
            @PathVariable Integer id,
            @Valid @RequestBody BonusTypeDTO dto) {
        return ResponseEntity.ok(bonusTypeService.updateBonusType(id, dto));
    }


    @GetMapping("/search")
    @Operation(
            summary = "Search bonus types by filters",
            description = "Allows searching bonus types by name or description. Supports pagination.",
            externalDocs = @ExternalDocumentation(
                    description = "Example request",
                    url = "http://localhost:8080/api/bonus-types/search?name=referral&description=bonus&page=0&size=10"
            ),
            parameters = {
                    @Parameter(name = "name", description = "Part of the bonus type name", example = "referral"),
                    @Parameter(name = "description", description = "Part of the bonus type description", example = "bonus"),
                    @Parameter(name = "page", description = "Page number for pagination", example = "0"),
                    @Parameter(name = "size", description = "Number of results per page", example = "10")
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BonusTypeDTO.class),
                    examples = @ExampleObject(
                            value = "{ 'content': [ { " +
                                    "'id': 1, " +
                                    "'name': 'Referral Bonus', " +
                                    "'description': 'Bonus for inviting a friend' } ], " +
                                    "'totalElements': 1, " +
                                    "'totalPages': 1, " +
                                    "'size': 10, " +
                                    "'number': 0 }"
                    )
            )
    )
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<Page<BonusTypeDTO>> searchBonusTypes(@ModelAttribute BonusTypeFilterDTO filter) {
        Page<BonusTypeDTO> result = bonusTypeService.findWithFilter(filter);
        return ResponseEntity.ok(result);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить тип бонуса", description = "Удаляет тип бонуса по ID")
    @ApiResponse(responseCode = "204", description = "No Content")
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<Void> deleteBonusType(@PathVariable Integer id) {
        bonusTypeService.deleteBonusType(id);
        return ResponseEntity.noContent().build();
    }
}
