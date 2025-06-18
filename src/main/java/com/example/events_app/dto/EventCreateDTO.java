package com.example.events_app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для создания события")
public class EventCreateDTO {

    private Integer id;

    private String title;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String location;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean conducted;

    private String preview;

    private EventTypeDTO eventType;

}
