package com.example.events_app.dto.event;

import com.example.events_app.dto.event_pictures.EventImageDTO;
import com.example.events_app.dto.user.UserMediumDTO;
import com.example.events_app.dto.user.UserShortDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для создания и обновления события")
public class EventResponseShortDTO {
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
    private UserShortDTO user;

    private String preview; // Здесь будем хранить путь к изображению или base64 строку

    @Schema(description = "Список путей к дополнительным изображениям")
    private List<EventImageDTO> images;

}
