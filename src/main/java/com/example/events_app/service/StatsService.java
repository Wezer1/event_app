package com.example.events_app.service;

import com.example.events_app.dto.organizer.OrganizerStatsDTO;
import com.example.events_app.repository.EventParticipantRepository;
import com.example.events_app.repository.EventRepository;
import com.example.events_app.repository.EventTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Math.log;

@Service
@Slf4j
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
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);
        long activeEvents = eventRepository.countActiveEventsByUser(now, organizerId);
        long upcomingEvents = eventRepository.countUpcomingEvents(now, organizerId);
        long completedEvents = eventRepository.countCompletedEvents(now, organizerId);
        return new OrganizerStatsDTO(totalEvents, conductedEvents, totalParticipants, eventTypes, activeEvents, upcomingEvents, completedEvents);
    }
}
