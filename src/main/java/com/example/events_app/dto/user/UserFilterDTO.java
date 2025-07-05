package com.example.events_app.dto.user;

import com.example.events_app.model.Role;
import lombok.Data;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserFilterDTO {

    @Schema(description = "ФИО пользователя", example = "Ivanov Ivan")
    private String fullName;

    @Schema(description = "Логин пользователя", example = "ivan_123")
    private String login;

    @Schema(description = "Количество зарегистрированных событий", example = "5")
    private Integer registeredEventsCount;

    @Schema(description = "Общее количество бонусных баллов", example = "100")
    private Integer totalBonusPoints;

    @Schema(description = "Роль пользователя: USER или ADMIN", example = "USER")
    private Role role;

    @Schema(description = "Номер страницы", example = "0")
    private int page = 0;

    @Schema(description = "Размер страницы", example = "50")
    private int size = 50;
}