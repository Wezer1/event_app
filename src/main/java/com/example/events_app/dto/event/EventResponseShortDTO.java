package com.example.events_app.dto.event;

import com.example.events_app.dto.user.UserMediumDTO;
import com.example.events_app.dto.user.UserShortDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    private UserShortDTO user;
}
