package com.example.events_app.service;

import com.example.events_app.dto.organizer.OrganizerStatsDTO;
import com.example.events_app.repository.EventParticipantRepository;
import com.example.events_app.repository.EventRepository;
import com.example.events_app.repository.EventTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final EventTypeRepository eventTypeRepository;

    @Transactional(readOnly = true)
    public OrganizerStatsDTO getOrganizerStats(Integer organizerId) {
        // Всего событий организатора
        int totalEvents = eventRepository.countByUserId(organizerId);

        // Проведенных событий
        int conductedEvents = eventRepository.countByUserIdAndConducted(organizerId, true);

        // Всего посетителей (по всем событиям организатора с статусом VALID)
        int totalParticipants = eventParticipantRepository.countTotalValidParticipantsByOrganizer(organizerId);

        // Типы мероприятий организатора
        List<String> eventTypes = eventTypeRepository.findEventTypeNamesByOrganizer(organizerId);

        return new OrganizerStatsDTO(totalEvents, conductedEvents, totalParticipants, eventTypes);
    }
}
