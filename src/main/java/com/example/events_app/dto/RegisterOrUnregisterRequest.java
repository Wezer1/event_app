package com.example.events_app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на регистрацию или удаление записи на событие")
public class RegisterOrUnregisterRequest {
    private Integer userId;
    private Integer eventId;
}
