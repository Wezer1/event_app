package com.example.events_app.service;

import com.example.events_app.dto.EventCreateDTO;
import com.example.events_app.dto.EventDTO;
import com.example.events_app.dto.EventFilterDTO;
import com.example.events_app.dto.EventTypeDTO;
import com.example.events_app.entity.Event;
import com.example.events_app.entity.EventType;
import com.example.events_app.exceptions.NoSuchException;
import com.example.events_app.filter.EventSpecification;
import com.example.events_app.mapper.EventCreateDTOMapper;
import com.example.events_app.mapper.EventMapper;
import com.example.events_app.mapper.EventTypeMapper;
import com.example.events_app.repository.EventRepository;
import com.example.events_app.repository.EventTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventCreateDTOMapper eventCreateDTOMapper;
    private final EventTypeRepository eventTypeRepository;
    private final EventTypeMapper eventTypeMapper;
    private static final String UPLOAD_DIR = "uploads";
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
    public EventDTO saveEvent(EventCreateDTO eventDTO, MultipartFile preview) throws IOException {
        log.info("Saving event: {}", eventDTO);

        LocalDateTime now = LocalDateTime.now();
        eventDTO.setCreatedAt(now);
        eventDTO.setUpdatedAt(now);

        // Преобразуем DTO в сущность один раз
        Event event = eventCreateDTOMapper.toEntity(eventDTO);

        if (!preview.isEmpty()) {
            // Создаем папку, если её нет
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Получаем оригинальное имя файла и расширение
            String originalFilename = preview.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                originalFilename = UUID.randomUUID() + fileExtension;
            }

            // Полный путь к файлу
            Path filePath = Paths.get(UPLOAD_DIR, originalFilename);

            // Сохраняем файл
            Files.write(filePath, preview.getBytes());

            // Формируем URL, который будем хранить в БД
            String previewPath = "/uploads/" + originalFilename;
            event.setPreview(previewPath); // ← устанавливаем preview в сущность
        }

        // 🔁 Если указан eventType.id — загружаем полный объект
        if (eventDTO.getEventType() != null && eventDTO.getEventType().getId() != null) {
            EventType dbType = eventTypeRepository.findById(eventDTO.getEventType().getId())
                    .orElseThrow(() -> new NoSuchException("EventType not found"));
            event.setEventType(dbType);
        }

        // ✅ СОХРАНЯЕМ event, а не toEntity(eventDTO)
        Event savedEvent = eventRepository.save(event);
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
        if (eventRepository.findById(eventId).isEmpty()) {
            throw new NoSuchException("There is no event with ID = " + eventId + " in Database");
        }
        eventRepository.deleteById(eventId);
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
