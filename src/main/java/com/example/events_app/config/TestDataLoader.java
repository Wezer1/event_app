package com.example.events_app.config;

import com.example.events_app.entity.*;
import com.example.events_app.model.EventParticipantStatus;
import com.example.events_app.model.Role;
import com.example.events_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TestDataLoader implements CommandLineRunner {

    private final BonusTypeRepository bonusTypeRepository;
    private final EventTypeRepository eventTypeRepository;
    private final UserRepository userRepository;
    private final UserBonusHistoryRepository userBonusHistoryRepository;
    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;

    @Override
    public void run(String... args) throws Exception {
        LocalDateTime now = LocalDateTime.now();

        // 1. BonusType
        if (bonusTypeRepository.count() == 0) {
            List<BonusType> bonusTypes = new ArrayList<>();
            BonusType b1 = new BonusType();
            b1.setName("Приветственный бонус");
            b1.setDescription("За регистрацию");

            BonusType b2 = new BonusType();
            b2.setName("Бонус за участие");
            b2.setDescription("За посещение события");

            BonusType b3 = new BonusType();
            b3.setName("Активность");
            b3.setDescription("За активное участие");

            bonusTypes.add(b1);
            bonusTypes.add(b2);
            bonusTypes.add(b3);

            bonusTypeRepository.saveAll(bonusTypes);
        }

        // 2. EventType
        if (eventTypeRepository.count() == 0) {
            List<EventType> types = new ArrayList<>();

            for (int i = 0; i < 5; i++) {
                EventType type = new EventType();
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

        // 3. User
        if (userRepository.count() == 0) {
            List<User> users = new ArrayList<>();

            for (int i = 1; i <= 10; i++) {
                User user = new User();
                user.setFullName("Имя" + i);
                user.setLogin("user" + i);
                user.setPassword("password"); // можно хэшировать позже
                user.setRole(Role.USER);
                user.setRegisteredEventsCount(0);
                user.setTotalBonusPoints(0);

                users.add(user);
            }

            userRepository.saveAll(users);
        }

        // 4. UserBonusHistory
        if (userBonusHistoryRepository.count() == 0) {
            List<UserBonusHistory> histories = new ArrayList<>();
            List<User> allUsers = userRepository.findAll();
            List<BonusType> allBonusTypes = bonusTypeRepository.findAll();

            for (User user : allUsers) {
                for (int i = 0; i < 2; i++) {
                    UserBonusHistory history = new UserBonusHistory();
                    history.setUser(user);
                    history.setBonusType(allBonusTypes.get(i % allBonusTypes.size()));
                    history.setAmount((i + 1) * 10);
                    history.setReason("Тестовый бонус");
                    history.setCreatedAt(now.minusDays(i));
                    history.setActive(true);

                    histories.add(history);
                }
            }

            userBonusHistoryRepository.saveAll(histories);
        }

        // 5. Event
        if (eventRepository.count() == 0) {
            List<Event> events = new ArrayList<>();
            List<EventType> allTypes = eventTypeRepository.findAll();

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

        // 6. EventParticipant
        if (eventParticipantRepository.count() == 0) {
            List<EventParticipant> participants = new ArrayList<>();
            List<Event> allEvents = eventRepository.findAll();
            List<User> allUsers = userRepository.findAll();

            for (Event event : allEvents) {
                int participantsCount = (int) (Math.random() * 5) + 1;

                for (int j = 0; j < participantsCount; j++) {
                    User randomUser = allUsers.get((int) (Math.random() * allUsers.size()));

                    EventParticipant participant = new EventParticipant();
                    EventParticipantId id = new EventParticipantId();
                    id.setUserId(randomUser.getId());
                    id.setEventId(event.getId());

                    participant.setId(id);
                    participant.setUser(randomUser);
                    participant.setEvent(event);
                    participant.setStatus(EventParticipantStatus.CONFIRMED);
                    participant.setCreatedAt(now.minusDays(1));

                    participants.add(participant);
                }
            }

            eventParticipantRepository.saveAll(participants);
        }
    }
}