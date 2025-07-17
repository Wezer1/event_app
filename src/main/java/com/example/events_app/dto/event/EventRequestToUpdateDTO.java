package com.example.events_app.dto.event;

import com.example.events_app.dto.event_pictures.EventImageShortDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestToUpdateDTO {
    private Integer id;

    private String title;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String location;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean conducted;

    private Integer eventTypeId;

    private Integer userId;

    private String preview;

    private List<EventImageShortDTO> currentImages;
}
