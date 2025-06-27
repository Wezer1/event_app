package com.example.events_app.service;

import com.example.events_app.dto.event.EventTypeDTO;
import com.example.events_app.entity.EventType;
import com.example.events_app.exceptions.NoSuchException;
import com.example.events_app.mapper.event.EventTypeMapper;
import com.example.events_app.repository.EventTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventTypeService {

    private final EventTypeRepository eventTypeRepository;
    private final EventTypeMapper eventTypeMapper;

    @Transactional
    public List<EventTypeDTO> getAllEventTypes() {
        log.info("Get all event types");
        if (eventTypeRepository.findAll().isEmpty()) {
            throw new NoSuchException("No event types found");
        }
        return eventTypeRepository.findAll().stream()
                .map(eventTypeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventTypeDTO getEventTypeById(Integer eventTypeId) {
        log.info("Get event type by ID: {}", eventTypeId);

        EventType eventType = eventTypeRepository.findById(eventTypeId)
                .orElseThrow(() -> new NoSuchException("There is no event type with ID = " + eventTypeId + " in DB"));

        return eventTypeMapper.toDto(eventType);
    }

    @Transactional
    public EventTypeDTO saveEventType(EventTypeDTO eventTypeDTO) {
        log.info("Saving event type: {}", eventTypeDTO);

        // Проверяем, есть ли уже EventType с таким name
        if (eventTypeRepository.existsByName(eventTypeDTO.getName())) {
            throw new DataIntegrityViolationException("Event type with name '" + eventTypeDTO.getName() + "' already exists.");
        }

        EventType savedEventType = eventTypeRepository.save(eventTypeMapper.toEntity(eventTypeDTO));

        return eventTypeMapper.toDto(savedEventType);
    }

    @Transactional
    public EventTypeDTO updateEventType(Integer eventTypeId, EventTypeDTO eventTypeDTO) {
        log.info("Updating event type with ID {}: {}", eventTypeId, eventTypeDTO);

        EventType existingEventType = eventTypeRepository.findById(eventTypeId)
                .orElseThrow(() -> new NoSuchException("There is no event type with ID = " + eventTypeId + " in Database"));

        // Если имя изменилось — проверяем на уникальность
        if (!existingEventType.getName().equals(eventTypeDTO.getName())) {
            if (eventTypeRepository.existsByName(eventTypeDTO.getName())) {
                throw new DataIntegrityViolationException("Event type with name '" + eventTypeDTO.getName() + "' already exists.");
            }
        }

        EventType updatedEventType = eventTypeMapper.toEntity(eventTypeDTO);
        updatedEventType.setId(existingEventType.getId()); // сохраняем старый ID

        EventType savedEventType = eventTypeRepository.save(updatedEventType);

        return eventTypeMapper.toDto(savedEventType);
    }

    @Transactional
    public void deleteEventType(Integer eventTypeId) {
        log.info("Delete event type with ID: {}", eventTypeId);

        if (!eventTypeRepository.existsById(eventTypeId)) {
            throw new NoSuchException("There is no event type with ID = " + eventTypeId + " in Database");
        }

        eventTypeRepository.deleteById(eventTypeId);
    }
}