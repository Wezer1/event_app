package com.example.events_app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Участник мероприятия с расширенной информацией")
public class EventParticipantDTO {

    private Integer userId;
    private String firstName; // ← Новое поле
    private String lastName; // ← Новое поле
    private Integer eventId;
    private String eventName; // ← Новое поле
    private String status;
    private LocalDateTime createdAt;
}
