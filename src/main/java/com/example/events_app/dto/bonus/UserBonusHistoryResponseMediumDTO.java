package com.example.events_app.dto.bonus;

import com.example.events_app.dto.user.UserMediumDTO;
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
public class UserBonusHistoryResponseMediumDTO {
    private Integer id;

    @NotNull(message = "user cannot be null")
    private UserMediumDTO user;

    @NotNull(message = "bonusType cannot be null")
    private BonusTypeDTO bonusType;

    private Integer amount;
    private String reason;
    private LocalDateTime createdAt;
    private boolean isActive;
}
