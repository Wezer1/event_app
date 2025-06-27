package com.example.events_app.controller;

import com.example.events_app.dto.bonus.BonusTypeDTO;
import com.example.events_app.service.BonusTypeService;
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

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить тип бонуса", description = "Удаляет тип бонуса по ID")
    @ApiResponse(responseCode = "204", description = "No Content")
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<Void> deleteBonusType(@PathVariable Integer id) {
        bonusTypeService.deleteBonusType(id);
        return ResponseEntity.noContent().build();
    }
}
