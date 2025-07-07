package com.example.events_app.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Средняя информация о пользователе")
public class UserMediumDTO {

    private Integer id;
    private String fullName;
    private Integer registeredEventsCount ;
    private Integer totalBonusPoints;
    private String phoneNumber;
    private String email;
}
