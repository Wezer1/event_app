package com.example.events_app.service;

import com.example.events_app.dto.event.*;
import com.example.events_app.dto.event_pictures.EventImageDTO;
import com.example.events_app.dto.event_pictures.EventImageShortDTO;
import com.example.events_app.dto.organizer.OrganizerStatsDTO;
import com.example.events_app.entity.*;
import com.example.events_app.exceptions.NoSuchException;
import com.example.events_app.filter.EventSpecification;
import com.example.events_app.filter.EventWithUserSpecification;
import com.example.events_app.mapper.event.*;
import com.example.events_app.model.EventParticipantStatus;
import com.example.events_app.model.MembershipStatus;
import com.example.events_app.model.SortDirection;
import com.example.events_app.repository.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
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
    private final EventParticipantRepository eventParticipantRepository;
    private final FileStorageService fileStorageService;
    private final EventImageRepository eventImageRepository;
    private final EventResponseMediumWithOutImagesMapper eventResponseMediumWithOutImagesMapper;
    private final EventParticipantRepository participantRepository;
    private final UserBonusHistoryRepository bonusHistoryRepository;
    private final BonusTypeRepository bonusTypeRepository;

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
            // Сохраняем превью (автоматически сохранит оригинал + сжатое превью)
            String previewName = fileStorageService.storePreview(preview);

            // Удаляем старое превью (если было)
            if (savedEvent.getPreview() != null) {
                try {
                    fileStorageService.deleteFile(savedEvent.getPreview());
                } catch (RuntimeException e) {
                    log.error("Failed to delete old preview: {}", e.getMessage());
                }
            }

            // Сохраняем путь к сжатому превью
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
        // Удаляем старое превью и оригинал
        if (event.getPreview() != null) {
            try {
                fileStorageService.deleteFile(event.getPreview());
            } catch (RuntimeException e) {
                log.error("Failed to delete old preview files: {}", e.getMessage());
            }
        }

        // Сохраняем новое превью
        String newPreviewName = fileStorageService.storePreview(newPreview);
        event.setPreview("uploads/previews/" + newPreviewName);
    }

    private void handleImagesUpdate(Event event,
                                    List<EventImageShortDTO> currentImagesDTO,
                                    List<MultipartFile> newImages) {
        List<EventImage> existingImages = new ArrayList<>(event.getImages());

        Set<String> currentImagePaths = currentImagesDTO.stream()
                .map(EventImageShortDTO::getFilePath)
                .collect(Collectors.toSet());

        // Удаляем изображения, которых нет в currentImagePaths
        existingImages.stream()
                .filter(image -> !currentImagePaths.contains(image.getFilePath()))
                .forEach(image -> {
                    try {
                        // Удаляем файл через FileStorageService
                        fileStorageService.deleteFile(image.getFilePath());

                        // Удаляем из коллекции и БД
                        event.getImages().remove(image);
                        eventImageRepository.delete(image);
                    } catch (RuntimeException e) {
                        log.error("Failed to delete image: {}", image.getFilePath(), e);
                    }
                });

        // Добавляем новые изображения
        if (newImages != null) {
            for (MultipartFile file : newImages) {
                if (file != null && !file.isEmpty()) {
                    String fileName = fileStorageService.storeAdditionalImage(file);
                    EventImage newImage = new EventImage();
                    newImage.setEvent(event);
                    newImage.setFilePath("uploads/images/" + fileName);
                    event.getImages().add(newImage);
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

        // 1. Удаляем превью и оригинал (если есть)
        if (event.getPreview() != null) {
            try {
                // Удаляем превью
                Path previewPath = Paths.get(event.getPreview());
                Files.deleteIfExists(previewPath);

                // Получаем имя оригинального файла (убираем .jpg и путь)
                String previewFilename = previewPath.getFileName().toString();
                String originalFilename = previewFilename.substring(0, previewFilename.lastIndexOf('.'));

                // Ищем оригинал в папке originals (с любым расширением)
                Path originalsDir = Paths.get(fileStorageService.getUploadRootDir().toString(), "originals");
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(originalsDir,
                        originalFilename + ".*")) {
                    for (Path originalPath : stream) {
                        Files.deleteIfExists(originalPath);
                        log.debug("Deleted original file: {}", originalPath);
                    }
                }
            } catch (IOException e) {
                log.error("Failed to delete preview files: {}", event.getPreview(), e);
                throw new RuntimeException("Failed to delete preview files", e);
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

        // 3. Удаляем само событие
        eventRepository.delete(event);
    }

    public BonusResponse updateConductedStatus(Integer eventId, boolean conducted) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchException("Event not found"));

        // Если статус не изменился
        if (event.isConducted() == conducted) {
            return null;
        }

        event.setConducted(conducted);
        eventRepository.save(event);

        if (conducted) {
            // Начисляем бонусы при установке conducted=true
            BonusResponse awardResponse = awardBonusesToEventParticipants(eventId);
            return new BonusResponse(
                    awardResponse.participantsCount,
                    awardResponse.totalBonuses,
                    "Bonuses awarded"
            );
        } else {
            // Отменяем бонусы при установке conducted=false
            BonusResponse revokeResponse = revokeBonusesFromEventParticipants(eventId);
            return new BonusResponse(
                    revokeResponse.participantsCount,
                    revokeResponse.totalBonuses,
                    "Bonuses revoked"
            );
        }
    }

    public Page<EventResponseMediumWithOutImagesDTO> searchEvents(EventFilterDTO filter) {
        Sort sort = filter.getSortOrder() == SortDirection.ASC
                ? Sort.by(filter.getSortBy()).ascending()
                : Sort.by(filter.getSortBy()).descending();

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);
        Page<Event> eventsPage = eventRepository.findAll(EventSpecification.withFilter(filter), pageable);

        return eventsPage.map(event -> {
            EventResponseMediumWithOutImagesDTO dto = eventResponseMediumWithOutImagesMapper.toDto(event);
            int validCount = eventParticipantRepository.countByEventIdAndMembershipStatus(
                    event.getId(), MembershipStatus.VALID);
            dto.setTotalVisitors(validCount);
            return dto;
        });
    }

    public Page<EventResponseMediumWithOutImagesDTO> searchEventsWithUser(EventFilterForUserDTO filter) {
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
            EventResponseMediumWithOutImagesDTO dto = eventResponseMediumWithOutImagesMapper.toDto(event);
            int validCount = eventParticipantRepository.countByEventIdAndMembershipStatus(
                    event.getId(), MembershipStatus.VALID);
            dto.setTotalVisitors(validCount);
            return dto;
        });
    }

    public BonusResponse awardBonusesToEventParticipants(Integer eventId) {
        String bonusName = "Бонус за участие";

        // 1. Получаем тип бонуса
        BonusType bonusType = bonusTypeRepository.findByName(bonusName)
                .orElseThrow(() -> new IllegalArgumentException("Bonus type not found: " + bonusName));

        // 2. Получаем и валидируем событие
        Event event = validateEvent(eventId);

        // 3. Получаем валидных участников
        List<EventParticipant> validParticipants = getValidParticipants(eventId);

        // 4. Если нет участников - возвращаем соответствующий результат
        if (validParticipants.isEmpty()) {
            log.info("No valid participants found for event {}", eventId);
            return new BonusResponse(
                    0,
                    0,
                    "No valid participants found for event"
            );
        }

        // 5. Начисляем бонусы
        int awardedCount = awardBonusesToParticipants(validParticipants, event, bonusType);

        return new BonusResponse(
                awardedCount,
                awardedCount * 30, // Используем amount из bonusType
                "Bonuses awarded successfully"
        );
    }

    private Event validateEvent(Integer eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + eventId));

        if (!event.isConducted()) {
            throw new IllegalStateException("Cannot award bonuses before event ends");
        }

        return event;
    }

    private List<EventParticipant> getValidParticipants(Integer eventId) {
        return participantRepository.findByEventIdAndMembershipStatus(
                eventId,
                MembershipStatus.VALID
        );
    }

    private int awardBonusesToParticipants(List<EventParticipant> participants, Event event, BonusType bonusType) {
        participants.forEach(participant -> {
            createBonusHistory(participant.getUser(), event, bonusType);
            updateUserBalance(participant.getUser(), 30);
        });

        return participants.size();
    }

    private void createBonusHistory(User user, Event event, BonusType bonusType) {
        UserBonusHistory bonus = new UserBonusHistory();
        bonus.setUser(user);
        bonus.setBonusType(bonusType);
        bonus.setAmount(30); // Используем сумму из типа бонуса
        bonus.setReason("Участие в мероприятии: " + event.getTitle());
        bonus.setCreatedAt(LocalDateTime.now());
        bonus.setActive(true);
        bonus.setEvent(event);
        bonusHistoryRepository.save(bonus);
    }

    private void updateUserBalance(User user, int amount) {
        user.setTotalBonusPoints(user.getTotalBonusPoints() + amount);
        userRepository.save(user);
    }

    public BonusResponse revokeBonusesFromEventParticipants(Integer eventId) {
        String bonusName = "Бонус за участие";

        // 1. Получаем тип бонуса
        BonusType bonusType = bonusTypeRepository.findByName(bonusName)
                .orElseThrow(() -> new IllegalArgumentException("Bonus type not found: " + bonusName));

        // 2. Получаем и валидируем событие
        Event event = validateEventForRevoke(eventId);

        // 3. Получаем историю начисленных бонусов
        List<EventParticipant> validParticipants = getValidParticipants(eventId);

        // 4. Если нет бонусов - возвращаем соответствующий результат
        if (validParticipants.isEmpty()) {
            log.info("No bonuses found for event {}", eventId);
            return new BonusResponse(
                    0,
                    0,
                    "No bonuses found for event"
            );
        }

        // 5. Отменяем бонусы
        int revokedCount = revokeBonusesForParticipants(validParticipants, bonusType, eventId);

        return new BonusResponse(
                revokedCount,
                revokedCount * 30,
                "Bonuses revoked successfully"
        );
    }

    private Event validateEventForRevoke(Integer eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + eventId));
    }

    private int revokeBonusesForParticipants(List<EventParticipant> participants, BonusType bonusType, Integer eventId) {

        int revokedCount = 0;

        for (EventParticipant participant : participants) {
            User user = participant.getUser();

            // 1. Находим активные бонусы пользователя для этого события
            List<UserBonusHistory> activeBonuses = bonusHistoryRepository.findByUserIdAndBonusTypeIdAndIsActiveAndEventId(
                    user.getId(),
                    bonusType.getId(),
                    true,
                    eventId);

            // 2. Если есть активные бонусы - отменяем их
            if (!activeBonuses.isEmpty()) {
                for (UserBonusHistory bonus : activeBonuses) {
                    // Помечаем бонус как неактивный
                    bonus.setActive(false);
                    bonusHistoryRepository.save(bonus);

                    // Вычитаем бонусы из баланса пользователя
                    user.setTotalBonusPoints(user.getTotalBonusPoints() - bonus.getAmount());
                    userRepository.save(user);

                    revokedCount++;
                }
            }
        }

        return revokedCount;
    }

    // DTO для ответа
    public record BonusResponse(
            int participantsCount,
            int totalBonuses,
            String message
    ) {}
}
