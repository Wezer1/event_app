package com.example.events_app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Тип мероприятия")
public class EventTypeDTO {
    private Integer id;

    @NotBlank(message = "Name is required")
    @Size(max = 255)
    private String name;
    private String description;
}
