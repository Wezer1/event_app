package com.example.events_app.dto.event;

import com.example.events_app.dto.user.UserShortDTO;
import com.example.events_app.model.EventParticipantStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Участник мероприятия с расширенной информацией")
public class EventParticipantDTO {
    @JsonProperty("user")
    private UserShortDTO userId;

    @JsonProperty("event")
    private EventShortDTO eventId;
    private EventParticipantStatus status;
    private LocalDateTime createdAt;
}
