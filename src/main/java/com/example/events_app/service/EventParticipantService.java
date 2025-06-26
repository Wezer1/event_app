package com.example.events_app.service;

import com.example.events_app.dto.EventParticipantDTO;
import com.example.events_app.dto.EventShortDTO;
import com.example.events_app.dto.UserShortDTO;
import com.example.events_app.entity.Event;
import com.example.events_app.entity.EventParticipant;
import com.example.events_app.entity.EventParticipantId;
import com.example.events_app.entity.User;
import com.example.events_app.exceptions.AlreadyExistsException;
import com.example.events_app.exceptions.NoSuchException;
import com.example.events_app.mapper.EventParticipantMapper;
import com.example.events_app.model.EventParticipantStatus;
import com.example.events_app.repository.EventParticipantRepository;
import com.example.events_app.repository.EventRepository;
import com.example.events_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

        EventParticipantId id = new EventParticipantId(userId, eventId);

        EventParticipant participant = eventParticipantRepository.findById(id)
                .orElseThrow(() -> new NoSuchException("Participant not found for user " + userId + " and event " + eventId));

        try {
            // Преобразование строки в enum
            EventParticipantStatus statusEnum = EventParticipantStatus.valueOf(newStatus.toUpperCase());
            participant.setStatus(statusEnum);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + newStatus +
                    ". Allowed values are: " + Arrays.toString(EventParticipantStatus.values()));
        }

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

//    @Transactional
//    public EventParticipantDTO registerUserForEvent(Integer userId, Integer eventId) {
//        log.info("Registering user {} for event {}", userId, eventId);
//
//        // Проверяем, что такой пользователь еще не записан на это событие
//        Event event =  eventRepository.findById(eventId)
//                .orElseThrow(() -> new NoSuchException("There is no event with ID = " + eventId + " in DB"));
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new NoSuchException("There is no user with ID = "+ userId + " in Database"));
//        EventParticipantId id = new EventParticipantId();
//        id.setUserId(userId);
//        id.setEventId(eventId);
//
//        if (eventParticipantRepository.existsById(id)) {
//            throw new AlreadyExistsException("User is already registered for this event");
//        }
//
//        // Создаем новую запись
//        EventParticipant participant = new EventParticipant();
//        participant.setId(id);
//
//        // Устанавливаем значения в сервисе
//        participant.setStatus(EventParticipantStatus.CONFIRMED); // предположим, что это enum
//        participant.setCreatedAt(LocalDateTime.now());
//
//        EventParticipant savedParticipant = eventParticipantRepository.save(participant);
//
//        // Обновляем счетчики пользователя
//        userRepository.incrementRegisteredEventsCount(userId);
//
//        // Если за регистрацию даются баллы, например +10
////        int bonusPoints = 10;
////        user.setTotalBonusPoints(user.getTotalBonusPoints() == null ? bonusPoints : user.getTotalBonusPoints() + bonusPoints);
//
//        // Сохраняем изменения пользователя
//        userRepository.save(user);
//        return eventParticipantMapper.toDto(savedParticipant);
//    }

    @Transactional
    public EventParticipantDTO registerUserForEvent(Integer userId, Integer eventId) {
        log.info("Registering user {} for event {}", userId, eventId);

        // Проверяем, что такой пользователь еще не записан на это событие
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchException("There is no event with ID = " + eventId + " in DB"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchException("There is no user with ID = " + userId + " in Database"));

        EventParticipantId id = new EventParticipantId(userId, eventId);

        if (eventParticipantRepository.existsById(id)) {
            throw new AlreadyExistsException("User is already registered for this event");
        }

        // Создаем новую запись
        EventParticipant participant = new EventParticipant();
        participant.setId(id);
        participant.setStatus(EventParticipantStatus.CONFIRMED);
        participant.setCreatedAt(LocalDateTime.now());

        EventParticipant savedParticipant = eventParticipantRepository.save(participant);

        // Обновляем счетчики пользователя
        userRepository.incrementRegisteredEventsCount(userId);

        // --- НАЧАЛО ВРЕМЕННОГО РЕШЕНИЯ ---
        UserShortDTO userShortDTO = new UserShortDTO();
        userShortDTO.setId(user.getId());
        userShortDTO.setFirstName(user.getFirstName()); // ← берём реальные данные из БД
        userShortDTO.setLastName(user.getLastName());

        EventShortDTO eventShortDTO = new EventShortDTO();
        eventShortDTO.setId(event.getId());
        eventShortDTO.setTitle(event.getTitle()); // ← title → name

        EventParticipantDTO dto = new EventParticipantDTO();
        dto.setUserId(userShortDTO);
        dto.setEventId(eventShortDTO);
        dto.setStatus(savedParticipant.getStatus());
        dto.setCreatedAt(savedParticipant.getCreatedAt());

        return dto;
        // --- КОНЕЦ ВРЕМЕННОГО РЕШЕНИЯ ---
    }
}
