package com.example.events_app.config;

import com.example.events_app.dto.EventTypeDTO;
import com.example.events_app.entity.*;
import com.example.events_app.model.Role;
import com.example.events_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TestDataLoader implements CommandLineRunner {

    private final EventTypeRepository eventTypeRepository;
    private final EventRepository eventRepository;
    private final BonusTypeRepository bonusTypeRepository;
    private final UserRepository userRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final UserBonusHistoryRepository userBonusHistoryRepository;
    @Override
    public void run(String... args) throws Exception {

        if (userRepository.count() == 0) {
            List<User> users = new ArrayList<>();
            for (int i = 1; i <= 1000; i++) {
                User user = new User();
                user.setFirstName("Имя" + i);
                user.setLastName("Фамилия" + i);
                user.setPatronymic("Отчество" + i);
                user.setLogin("user" + i);
                user.setPassword("password"); // Обычный текстовый пароль
                user.setRole(Role.USER); // Предполагается, что Role.USER существует
                users.add(user);
            }
            userRepository.saveAll(users);
        }

        if (bonusTypeRepository.count() == 0) {
            BonusType bonusType = new BonusType();
            bonusType.setName("Бонус за участие");
            bonusType.setDescription("Начисляется за участие в мероприятии");
            bonusTypeRepository.save(bonusType);
        }

        if (eventParticipantRepository.count() == 0) {
            List<Event> events = eventRepository.findAll();
            List<User> users = userRepository.findAll();
            List<EventParticipant> participants = new ArrayList<>();

            LocalDateTime now = LocalDateTime.now();

            for (int i = 0; i < 1000; i++) {
                User user = users.get(i % users.size());
                Event event = events.get(i % events.size());

                EventParticipant participant = new EventParticipant();
                EventParticipantId id = new EventParticipantId();
                id.setUserId(user.getId());
                id.setEventId(event.getId());
                participant.setId(id);
                participant.setUser(user);
                participant.setEvent(event);
                participant.setStatus("REGISTERED");
                participant.setCreatedAt(now.minusDays((long) (Math.random() * 30)));
                participants.add(participant);
            }

            eventParticipantRepository.saveAll(participants);
        }

        if (userBonusHistoryRepository.count() == 0) {
            List<User> users = userRepository.findAll();
            List<BonusType> bonusTypes = bonusTypeRepository.findAll();
            List<UserBonusHistory> historyList = new ArrayList<>();

            LocalDateTime now = LocalDateTime.now();

            for (int i = 0; i < 1000; i++) {
                User user = users.get(i % users.size());
                BonusType bonusType = bonusTypes.get(0);

                UserBonusHistory history = new UserBonusHistory();
                history.setUser(user);
                history.setBonusType(bonusType);
                history.setAmount((int) (Math.random() * 100 + 50));
                history.setReason("Бонус за участие в мероприятии");
                history.setCreatedAt(now.minusDays((long) (Math.random() * 30)));
                history.setActive(true);

                historyList.add(history);
            }

            userBonusHistoryRepository.saveAll(historyList);
        }
        if (eventTypeRepository.count() == 0) {
            List<EventType> types = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                EventType type = new EventType();
                type.setId(null); // будет заполнено БД автоматически
                // или просто не вызывайте setId — оно может быть не нужно
                types.add(type);
            }

            types.get(0).setName("Конференция");
            types.get(0).setDescription("Мероприятие для обмена знаниями");

            types.get(1).setName("Семинар");
            types.get(1).setDescription("Обучающее мероприятие");

            types.get(2).setName("Воркшоп");
            types.get(2).setDescription("Практическое занятие");

            types.get(3).setName("Фестиваль");
            types.get(3).setDescription("Развлекательное массовое мероприятие");

            types.get(4).setName("Митап");
            types.get(4).setDescription("Неформальная встреча по интересам");

            eventTypeRepository.saveAll(types);
        }

        if (eventRepository.count() == 0) {
            List<EventType> allTypes = eventTypeRepository.findAll();
            List<Event> events = new ArrayList<>();

            LocalDateTime now = LocalDateTime.now();

            for (int i = 1; i <= 2000; i++) {
                EventType type = allTypes.get((int) (Math.random() * allTypes.size()));
                LocalDateTime start = now.plusDays((long) (Math.random() * 60));
                LocalDateTime end = start.plusHours(2);

                Event event = new Event();
                event.setTitle("Событие #" + i);
                event.setDescription("Описание события " + i);
                event.setStartTime(start);
                event.setEndTime(end);
                event.setLocation("Локация " + ((i % 10) + 1));
                event.setCreatedAt(now);
                event.setUpdatedAt(now);
                event.setConducted(false);
                event.setEventType(type);

                events.add(event);
            }

            eventRepository.saveAll(events);
        }
    }
}
