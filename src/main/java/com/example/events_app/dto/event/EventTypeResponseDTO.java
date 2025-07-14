package com.example.events_app.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventTypeResponseDTO {
    private Integer id;
    private String name;
    private String description;
}
