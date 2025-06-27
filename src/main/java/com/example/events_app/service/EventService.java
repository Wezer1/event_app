package com.example.events_app.service;

import com.example.events_app.dto.event.EventDTO;
import com.example.events_app.dto.event.EventFilterDTO;
import com.example.events_app.entity.Event;
import com.example.events_app.entity.EventType;
import com.example.events_app.exceptions.NoSuchException;
import com.example.events_app.filter.EventSpecification;
import com.example.events_app.mapper.event.EventMapper;
import com.example.events_app.mapper.event.EventTypeMapper;
import com.example.events_app.repository.EventRepository;
import com.example.events_app.repository.EventTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventTypeRepository eventTypeRepository;
    private final EventTypeMapper eventTypeMapper;

    @Transactional
    public List<EventDTO> getAllEvents() {
        log.info("Get all events");
        if (eventRepository.findAll().isEmpty()) {
            throw new NoSuchException("No events");
        }
        return eventRepository.findAll().stream().map(eventMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public EventDTO getEventById(Integer eventId) {
        log.info("Get event by id: {} ", eventId);
        Optional<Event> airplaneOptional = Optional.ofNullable(eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchException("There is no event with ID = " + eventId + " in DB")));
        return eventMapper.toDto(airplaneOptional.get());
    }

    @Transactional
    public EventDTO saveEvent(EventDTO eventDTO) {
        log.info("Saving event: {}", eventDTO);

        LocalDateTime now = LocalDateTime.now();
        eventDTO.setCreatedAt(now);
        eventDTO.setUpdatedAt(now);

        EventType dbType = null;

        // 🔁 Если указан eventType.id — загружаем полный объект
        if (eventDTO.getEventType() != null && eventDTO.getEventType().getId() != null) {
            dbType = eventTypeRepository.findById(eventDTO.getEventType().getId())
                    .orElseThrow(() -> new NoSuchException("EventType not found"));
        }

        // Создаём сущность вручную, чтобы точно указать eventType
        Event eventToSave = eventMapper.toEntity(eventDTO);
        if (dbType != null) {
            eventToSave.setEventType(dbType); // Убедимся, что связь установлена
        }

        Event savedEvent = eventRepository.save(eventToSave);

        // ✅ Увеличиваем счётчик только после успешного сохранения
        if (dbType != null) {
            eventTypeRepository.incrementEventsCountById(dbType.getId());
        }

        return eventMapper.toDto(savedEvent);
    }

    @Transactional
    public EventDTO changeEvent(Integer eventId, EventDTO eventDTO) {
        log.info("Updating event with ID {}: {}", eventId, eventDTO);

        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchException("There is no event with ID = " + eventId + " in Database"));

        // 🔁 Обновляем все поля через маппер
        Event updatedEvent = eventMapper.toEntity(eventDTO);
        updatedEvent.setId(existingEvent.getId()); // сохраняем старый ID
        updatedEvent.setCreatedAt(existingEvent.getCreatedAt()); // оставляем дату создания
        updatedEvent.setUpdatedAt(LocalDateTime.now()); // обновляем время изменения

        // 🔁 Загружаем тип события из БД, если указан
        if (eventDTO.getEventType() != null && eventDTO.getEventType().getId() != null) {
            EventType dbType = eventTypeRepository.findById(eventDTO.getEventType().getId())
                    .orElseThrow(() -> new NoSuchException("EventType not found"));
            updatedEvent.setEventType(dbType);
        }

        Event savedEvent = eventRepository.save(updatedEvent);
        return eventMapper.toDto(savedEvent);
    }

    @Transactional
    public void deleteEvent(Integer eventId) {
        log.info("Delete event");
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchException("There is no event with ID = " + eventId + " in Database"));

        // Удаляем событие
        eventRepository.delete(event);

        // Уменьшаем счётчик у типа события
        EventType eventType = event.getEventType();
        if (eventType != null) {
            eventTypeRepository.decrementEventsCountById(eventType.getId());
        }
    }

    public void updateConductedStatus(Integer eventId, boolean conducted) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchException("Event not found"));

        event.setConducted(conducted);
        eventRepository.save(event);
    }

    public Page<EventDTO> searchEvents(EventFilterDTO filter) {
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());
        Page<Event> eventsPage = eventRepository.findAll(EventSpecification.withFilter(filter), pageable);

        // Используем маппер из MapStruct
        return eventsPage.map(eventMapper::toDto);
    }
}
