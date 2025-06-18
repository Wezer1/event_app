package com.example.events_app.service;

import com.example.events_app.dto.EventParticipantDTO;
import com.example.events_app.entity.EventParticipant;
import com.example.events_app.entity.EventParticipantId;
import com.example.events_app.exceptions.AlreadyExistsException;
import com.example.events_app.exceptions.NoSuchException;
import com.example.events_app.mapper.EventParticipantMapper;
import com.example.events_app.repository.EventParticipantRepository;
import com.example.events_app.repository.EventRepository;
import com.example.events_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventParticipantService {

    private final EventParticipantRepository eventParticipantRepository;
    private final EventParticipantMapper eventParticipantMapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;


    /**
     * 1) Получение всей таблицы
     */
    @Transactional(readOnly = true)
    public List<EventParticipantDTO> getAllParticipants() {
        log.info("Get all event participants");
        if (eventParticipantRepository.findAll().isEmpty()) {
            throw new NoSuchException("No event participants found");
        }
        return eventParticipantRepository.findAll()
                .stream()
                .map(eventParticipantMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 2) Получение только по id пользователя
     */
    @Transactional(readOnly = true)
    public List<EventParticipantDTO> getParticipantsByUserId(Integer userId) {
        log.info("Get event participants by user ID: {}", userId);
        List<EventParticipant> participants = eventParticipantRepository.findById_UserId(userId);
        if (participants.isEmpty()) {
            throw new NoSuchException("No event participants found for user with ID = " + userId);
        }
        return participants.stream()
                .map(eventParticipantMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 3) Получение только по id события
     */
    @Transactional(readOnly = true)
    public List<EventParticipantDTO> getParticipantsByEventId(Integer eventId) {
        log.info("Get event participants by event ID: {}", eventId);
        List<EventParticipant> participants = eventParticipantRepository.findById_EventId(eventId);
        if (participants.isEmpty()) {
            throw new NoSuchException("No participants found for event with ID = " + eventId);
        }
        return participants.stream()
                .map(eventParticipantMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 4) Изменение состояния события (например, пользователь поменял статус участия)
     * Предположим, что статус может быть "PENDING", "APPROVED", "DECLINED"
     */
    @Transactional
    public EventParticipantDTO updateParticipantStatus(Integer userId, Integer eventId, String newStatus) {
        log.info("Updating participant status for user {} and event {}: {}", userId, eventId, newStatus);

        EventParticipantId id = new EventParticipantId();
        id.setUserId(userId);
        id.setEventId(eventId);

        EventParticipant participant = eventParticipantRepository.findById(id)
                .orElseThrow(() -> new NoSuchException("Participant not found for user " + userId + " and event " + eventId));

        participant.setStatus(newStatus);
        EventParticipant updatedParticipant = eventParticipantRepository.save(participant);

        return eventParticipantMapper.toDto(updatedParticipant);
    }

    /**
     * 5) Удаление записи (пользователь отменил заявку)
     */
    @Transactional
    public void removeParticipant(Integer userId, Integer eventId) {
        log.info("Removing participant for user {} and event {}", userId, eventId);

        EventParticipantId id = new EventParticipantId();
        id.setUserId(userId);
        id.setEventId(eventId);

        if (!eventParticipantRepository.existsById(id)) {
            throw new NoSuchException("Participant not found for user " + userId + " and event " + eventId);
        }

        eventParticipantRepository.deleteById(id);
    }

    @Transactional
    public EventParticipantDTO registerUserForEvent(Integer userId, Integer eventId) {
        log.info("Registering user {} for event {}", userId, eventId);

        // Проверяем, что такой пользователь еще не записан на это событие
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchException("There is no event with ID = " + eventId + " in DB"));
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchException("There is no user with ID = "+ userId + " in Database"));
        EventParticipantId id = new EventParticipantId();
        id.setUserId(userId);
        id.setEventId(eventId);

        if (eventParticipantRepository.existsById(id)) {
            throw new AlreadyExistsException("User is already registered for this event");
        }

        // Создаем новую запись
        EventParticipant participant = new EventParticipant();
        participant.setId(id);

        // Устанавливаем значения в сервисе
        participant.setStatus("Пойду"); // или APPROVED — как тебе нужно
        participant.setCreatedAt(LocalDateTime.now());

        // Сохраняем
        EventParticipant savedParticipant = eventParticipantRepository.save(participant);

        return eventParticipantMapper.toDto(savedParticipant);
    }
}
