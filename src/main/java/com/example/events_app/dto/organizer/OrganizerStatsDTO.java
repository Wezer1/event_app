package com.example.events_app.dto.organizer;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrganizerStatsDTO {
    private int totalEvents;
    private int conductedEvents;
    private int totalParticipants;
    private List<String> eventTypes;
    private long activeEvents;
    private long upcomingEvents;
    private long completedEvents;
}
