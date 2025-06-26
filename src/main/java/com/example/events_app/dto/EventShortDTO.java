package com.example.events_app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Краткая информация о мероприятии")
public class EventShortDTO {
    private Integer id;
    private String title;
}
