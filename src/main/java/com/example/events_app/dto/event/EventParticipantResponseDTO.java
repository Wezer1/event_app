package com.example.events_app.dto.event;

import com.example.events_app.dto.user.UserShortDTO;
import com.example.events_app.model.EventParticipantStatus;
import com.example.events_app.model.MembershipStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Участник мероприятия с расширенной информацией")
public class EventParticipantResponseDTO {
    @JsonProperty("user")
    private UserShortDTO userId;

    @JsonProperty("event")
    private EventForParticipantDTO eventId;
    private EventParticipantStatus status;
    private LocalDateTime createdAt;

    private MembershipStatus membershipStatus;
}