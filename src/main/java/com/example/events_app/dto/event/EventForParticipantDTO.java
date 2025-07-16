package com.example.events_app.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventForParticipantDTO {

    private Integer id;
    private String title;
    private String location;
    private LocalDateTime startTime;

}
