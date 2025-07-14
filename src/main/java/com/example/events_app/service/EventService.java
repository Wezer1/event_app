package com.example.events_app.service;

import com.example.events_app.dto.event.*;
import com.example.events_app.entity.Event;
import com.example.events_app.entity.EventType;
import com.example.events_app.entity.User;
import com.example.events_app.exceptions.NoSuchException;
import com.example.events_app.filter.EventSpecification;
import com.example.events_app.filter.EventWithUserSpecification;
import com.example.events_app.mapper.event.EventRequestMapper;
import com.example.events_app.mapper.event.EventResponseMediumMapper;
import com.example.events_app.mapper.event.EventResponseShortMapper;
import com.example.events_app.mapper.event.EventTypeMapper;
import com.example.events_app.model.SortDirection;
import com.example.events_app.repository.EventParticipantRepository;
import com.example.events_app.repository.EventRepository;
import com.example.events_app.repository.EventTypeRepository;
import com.example.events_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventResponseMediumMapper eventResponseMediumMapper;
    private final EventTypeRepository eventTypeRepository;
    private final EventResponseShortMapper eventResponseShortMapper;
    private final EventRequestMapper eventRequestMapper;
    private final UserRepository userRepository;
    private final EventTypeMapper eventTypeMapper;
    private final EventParticipantRepository eventParticipantRepository;

    @Transactional
    public List<EventResponseMediumDTO> getAllEvents() {
        log.info("Get all events");
        if (eventRepository.findAll().isEmpty()) {
            throw new NoSuchException("No events");
        }
        return eventRepository.findAll().stream().map(eventResponseMediumMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public EventResponseMediumDTO getEventById(Integer eventId) {
        log.info("Get event by id: {} ", eventId);
        Optional<Event> airplaneOptional = Optional.ofNullable(eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchException("There is no event with ID = " + eventId + " in DB")));
        return eventResponseMediumMapper.toDto(airplaneOptional.get());
    }

    @Transactional
    public EventResponseShortDTO saveEvent(EventRequestDTO eventDTO) {
        log.info("Saving event: {}", eventDTO);

        LocalDateTime now = LocalDateTime.now();

        EventType dbType = null;
        if (eventDTO.getEventTypeId() != null) {
            dbType = eventTypeRepository.findById(eventDTO.getEventTypeId())
                    .orElseThrow(() -> new NoSuchException("EventType not found"));
        }

        // ✅ Загружаем полный объект User по userId из DTO
        User user = userRepository.findById(eventDTO.getUserId())
                .orElseThrow(() -> new NoSuchException("User not found"));

        Event eventToSave = eventRequestMapper.toEntity(eventDTO);
        eventToSave.setUser(user); // ✅ Устанавливаем полностью заполненный объект User
        eventToSave.setCreatedAt(now);
        eventToSave.setUpdatedAt(now);
        if (dbType != null) {
            eventToSave.setEventType(dbType);
        }

        Event savedEvent = eventRepository.save(eventToSave);

        return eventResponseShortMapper.toDto(savedEvent);
    }

    @Transactional
    public EventResponseShortDTO changeEvent(Integer eventId, EventRequestDTO eventDTO) {
        log.info("Updating event with ID {}: {}", eventId, eventDTO);

        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchException("There is no event with ID = " + eventId + " in Database"));

        // 🔁 Обновляем поля из DTO
        Event updatedEvent = eventRequestMapper.toEntity(eventDTO);
        updatedEvent.setId(existingEvent.getId());
        updatedEvent.setCreatedAt(existingEvent.getCreatedAt()); // оставляем дату создания
        updatedEvent.setUpdatedAt(LocalDateTime.now());

        // 🔁 Обновляем тип события
        if (eventDTO.getEventTypeId() != null) {
            EventType dbType = eventTypeRepository.findById(eventDTO.getEventTypeId())
                    .orElseThrow(() -> new NoSuchException("EventType not found"));
            updatedEvent.setEventType(dbType);
        }

        Event savedEvent = eventRepository.save(updatedEvent);
        return eventResponseShortMapper.toDto(savedEvent);
    }

    @Transactional
    public void deleteEvent(Integer eventId) {
        log.info("Delete event");
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchException("There is no event with ID = " + eventId + " in Database"));

        // Удаляем событие
        eventRepository.delete(event);
    }

    public void updateConductedStatus(Integer eventId, boolean conducted) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchException("Event not found"));

        event.setConducted(conducted);
        eventRepository.save(event);
    }

    public Page<EventResponseMediumDTO> searchEvents(EventFilterDTO filter) {
        Sort sort = filter.getSortOrder() == SortDirection.ASC
                ? Sort.by(filter.getSortBy()).ascending()
                : Sort.by(filter.getSortBy()).descending();

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);
        Page<Event> eventsPage = eventRepository.findAll(EventSpecification.withFilter(filter), pageable);

        // Используем маппер из MapStruct
        return eventsPage.map(eventResponseMediumMapper::toDto);
    }
    public Page<EventResponseMediumDTO> searchEventsWithUser(EventFilterForUserDTO filter) {
       userRepository.findById(filter.getUserIdForEventFilter())
                .orElseThrow(() -> new NoSuchException("User not found"));

        List<Integer> allowedEventIds = filter.getUserIdForEventFilter() != null
                ? eventParticipantRepository.findEventIdsByUserId(filter.getUserIdForEventFilter())
                : Collections.emptyList();

        Specification<Event> spec = (root, query, cb) -> {
            if (!allowedEventIds.isEmpty()) {
                return root.get("id").in(allowedEventIds); // ✅ Работает!
            }
            return cb.conjunction();
        };

        Sort sort = filter.getSortOrder() == SortDirection.ASC
                ? Sort.by(filter.getSortBy()).ascending()
                : Sort.by(filter.getSortBy()).descending();

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);
        Page<Event> eventsPage = eventRepository.findAll(spec, pageable);
        return eventsPage.map(eventResponseMediumMapper::toDto);
    }
}
