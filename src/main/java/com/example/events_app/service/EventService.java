package com.example.events_app.service;

import com.example.events_app.dto.event.*;
import com.example.events_app.dto.event_pictures.EventImageDTO;
import com.example.events_app.dto.event_pictures.EventImageShortDTO;
import com.example.events_app.entity.Event;
import com.example.events_app.entity.EventImage;
import com.example.events_app.entity.EventType;
import com.example.events_app.entity.User;
import com.example.events_app.exceptions.NoSuchException;
import com.example.events_app.filter.EventSpecification;
import com.example.events_app.filter.EventWithUserSpecification;
import com.example.events_app.mapper.event.EventRequestMapper;
import com.example.events_app.mapper.event.EventResponseMediumMapper;
import com.example.events_app.mapper.event.EventResponseShortMapper;
import com.example.events_app.mapper.event.EventTypeMapper;
import com.example.events_app.model.MembershipStatus;
import com.example.events_app.model.SortDirection;
import com.example.events_app.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final FileStorageService fileStorageService;
    private final EventImageRepository eventImageRepository;

    @Transactional
    public List<EventResponseMediumDTO> getAllEvents() {
        log.info("Get all events");
        List<Event> events = eventRepository.findAll();
        if (events.isEmpty()) {
            throw new NoSuchException("No events");
        }

        return events.stream()
                .map(event -> {
                    EventResponseMediumDTO dto = eventResponseMediumMapper.toDto(event);
                    // Получаем количество участников с VALID статусом для данного события
                    int validCount = eventParticipantRepository.countByEventIdAndMembershipStatus(
                            event.getId(), MembershipStatus.VALID);
                    dto.setTotalVisitors(validCount);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public EventResponseMediumDTO getEventById(Integer eventId) {
        log.info("Get event by id: {} ", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchException("There is no event with ID = " + eventId + " in DB"));

        EventResponseMediumDTO dto = eventResponseMediumMapper.toDto(event);
        // Получаем количество участников с VALID статусом для данного события
        int validCount = eventParticipantRepository.countByEventIdAndMembershipStatus(
                eventId, MembershipStatus.VALID);
        dto.setTotalVisitors(validCount);

        return dto;
    }

    @Transactional
    public EventResponseShortDTO saveEventWithImages(
            EventRequestDTO eventDTO,
            MultipartFile preview,
            List<MultipartFile> additionalImages) {

        // 1. Сначала сохраняем событие без изображений
        LocalDateTime now = LocalDateTime.now();
        Event event = eventRequestMapper.toEntity(eventDTO);
        event.setCreatedAt(now);
        event.setUpdatedAt(now);

        EventType dbType = eventTypeRepository.findById(eventDTO.getEventTypeId()).orElse(null);
        User user = userRepository.findById(eventDTO.getUserId())
                .orElseThrow(() -> new NoSuchException("User not found"));

        event.setEventType(dbType);
        event.setUser(user);

        // 2. Сохраняем событие в БД (теперь у него есть ID)
        Event savedEvent = eventRepository.save(event);

        // 3. Обработка превью
        if (preview != null && !preview.isEmpty()) {
            String previewName = fileStorageService.storePreview(preview); // ← Изменили здесь
            savedEvent.setPreview("uploads/previews/" + previewName);
        }

        // 4. Обработка дополнительных изображений (используем правильный метод)
        if (additionalImages != null) {
            for (MultipartFile image : additionalImages) {
                if (image != null && !image.isEmpty()) {
                    String fileName = fileStorageService.storeAdditionalImage(image); // ← И здесь
                    EventImage eventImage = new EventImage();
                    eventImage.setEvent(savedEvent);
                    eventImage.setFilePath("uploads/images/" + fileName);
                    eventImageRepository.save(eventImage);
                }
            }
        }

        // 5. Обновляем событие (если изменилось превью)
        return eventResponseShortMapper.toDto(eventRepository.save(savedEvent));
    }

    @Transactional
    public EventResponseShortDTO changeEvent(Integer eventId,
                                             EventRequestToUpdateDTO eventDTO,
                                             MultipartFile preview,
                                             List<MultipartFile> newImages) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchException("Event not found"));

        // 1. Обработка превью
        if (preview != null && !preview.isEmpty()) {
            handlePreviewUpdate(event, preview);
        }

        // 2. Обработка изображений
        if (eventDTO.getCurrentImages() != null) {
            handleImagesUpdate(event, eventDTO.getCurrentImages(), newImages);
        }

        // 3. Обновление полей события
        updateBasicEventFields(event, eventDTO);

        return eventResponseShortMapper.toDto(eventRepository.save(event));
    }

    private void handlePreviewUpdate(Event event, MultipartFile newPreview) {
        // Удаляем старое превью, если оно существует
        if (event.getPreview() != null) {
            try {
                Path oldPreviewPath = Paths.get(event.getPreview());
                Files.deleteIfExists(oldPreviewPath);
            } catch (IOException e) {
                log.error("Failed to delete old preview: {}", e.getMessage());
                throw new RuntimeException("Failed to delete old preview", e);
            }
        }

        // Сохраняем новое превью
        String newPreviewName = fileStorageService.storePreview(newPreview);
        event.setPreview("uploads/previews/" + newPreviewName);
    }

    private void handleImagesUpdate(Event event,
                                    List<EventImageShortDTO> currentImagesDTO,
                                    List<MultipartFile> newImages) {

        List<EventImage> existingImages = eventImageRepository.findByEvent_Id(event.getId());

        // Удаляем изображения, которых нет в currentImagesDTO
        existingImages.stream()
                .filter(existingImage -> currentImagesDTO.stream()
                        .noneMatch(dto -> dto.getFilePath().equals(existingImage.getFilePath())))
                .forEach(image -> {
                    try {
                        Files.deleteIfExists(Paths.get(image.getFilePath()));
                        eventImageRepository.delete(image);
                    } catch (IOException e) {
                        log.error("Failed to delete image file: {}", e.getMessage());
                        throw new RuntimeException("Failed to delete image file", e);
                    }
                });

        // Добавляем новые изображения
        if (newImages != null && !newImages.isEmpty()) {
            for (MultipartFile file : newImages) {
                if (file != null && !file.isEmpty()) {
                    String fileName = fileStorageService.storeAdditionalImage(file);
                    EventImage newImage = new EventImage();
                    newImage.setEvent(event);
                    newImage.setFilePath("uploads/images/" + fileName);
                    eventImageRepository.save(newImage);
                }
            }
        }
    }

    private void updateBasicEventFields(Event event, EventRequestToUpdateDTO dto) {
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getStartTime() != null) {
            event.setStartTime(dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            event.setEndTime(dto.getEndTime());
        }
        if (dto.getLocation() != null) {
            event.setLocation(dto.getLocation());
        }
        if (dto.getEventTypeId() != null) {
            EventType type = eventTypeRepository.findById(dto.getEventTypeId())
                    .orElseThrow(() -> new NoSuchException("EventType not found"));
            event.setEventType(type);
        }
        event.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public void deleteEvent(Integer eventId) {
        log.info("Delete event with ID: {}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchException("There is no event with ID = " + eventId + " in Database"));

        // 1. Удаляем превью (если есть)
        if (event.getPreview() != null) {
            try {
                Path previewPath = Paths.get(event.getPreview());
                Files.deleteIfExists(previewPath);
            } catch (IOException e) {
                log.error("Failed to delete preview file: {}", event.getPreview(), e);
                throw new RuntimeException("Failed to delete preview file", e);
            }
        }

        // 2. Удаляем дополнительные изображения (если есть)
        List<EventImage> eventImages = eventImageRepository.findByEvent_Id(eventId);
        for (EventImage image : eventImages) {
            try {
                // Удаляем файл с диска
                Path imagePath = Paths.get(image.getFilePath());
                Files.deleteIfExists(imagePath);

                // Удаляем запись из БД
                eventImageRepository.delete(image);
            } catch (IOException e) {
                log.error("Failed to delete image file: {}", image.getFilePath(), e);
                throw new RuntimeException("Failed to delete image file", e);
            }
        }

        // 3. Удаляем само событие (каскадно удалит связанные сущности, если настроено)
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

        return eventsPage.map(event -> {
            EventResponseMediumDTO dto = eventResponseMediumMapper.toDto(event);
            int validCount = eventParticipantRepository.countByEventIdAndMembershipStatus(
                    event.getId(), MembershipStatus.VALID);
            dto.setTotalVisitors(validCount);
            return dto;
        });
    }

    public Page<EventResponseMediumDTO> searchEventsWithUser(EventFilterForUserDTO filter) {
        userRepository.findById(filter.getUserIdForEventFilter())
                .orElseThrow(() -> new NoSuchException("User not found"));

        List<Integer> allowedEventIds = filter.getUserIdForEventFilter() != null
                ? eventParticipantRepository.findEventIdsByUserId(filter.getUserIdForEventFilter())
                : Collections.emptyList();

        Specification<Event> spec = (root, query, cb) -> {
            if (!allowedEventIds.isEmpty()) {
                return root.get("id").in(allowedEventIds);
            }
            return cb.conjunction();
        };

        Sort sort = filter.getSortOrder() == SortDirection.ASC
                ? Sort.by(filter.getSortBy()).ascending()
                : Sort.by(filter.getSortBy()).descending();

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);
        Page<Event> eventsPage = eventRepository.findAll(spec, pageable);

        return eventsPage.map(event -> {
            EventResponseMediumDTO dto = eventResponseMediumMapper.toDto(event);
            int validCount = eventParticipantRepository.countByEventIdAndMembershipStatus(
                    event.getId(), MembershipStatus.VALID);
            dto.setTotalVisitors(validCount);
            return dto;
        });
    }
}
