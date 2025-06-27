package com.example.events_app.dto.event;

import com.example.events_app.dto.user.UserShortDTO;
import com.example.events_app.model.EventParticipantStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Участник мероприятия с расширенной информацией")
public class EventParticipantDTO {
    private UserShortDTO userId;
    private EventShortDTO eventId;
    private EventParticipantStatus status;
    private LocalDateTime createdAt;
}
