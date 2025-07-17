package com.example.events_app.dto.event_pictures;

import com.example.events_app.dto.event.EventResponseMediumDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventImageDTO {

    private Long id;

    private Integer eventId;

    private String filePath;
}
