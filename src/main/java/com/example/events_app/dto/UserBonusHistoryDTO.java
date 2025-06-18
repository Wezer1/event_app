package com.example.events_app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "История начисления бонуса")
public class UserBonusHistoryDTO {
    private Integer id;

    @NotNull(message = "userId cannot be null")
    private Integer userId;

    @NotNull(message = "bonusTypeId cannot be null")
    private Integer bonusTypeId;

    private Integer amount;

    private String reason;

    private LocalDateTime createdAt;

    private boolean isActive;
}
