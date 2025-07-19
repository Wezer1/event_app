package com.example.events_app.dto.event;

import com.example.events_app.dto.user.UserMediumDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для ответа события")
public class EventResponseMediumWithOutImagesDTO {

    private Integer id;

    private String title;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String location;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean conducted;

    private EventTypeDTO eventType;

    @JsonProperty("owner")
    private UserMediumDTO user;

    private String preview; // Здесь будем хранить путь к изображению или base64 строку

    private Integer totalVisitors;
}
