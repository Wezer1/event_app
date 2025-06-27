package com.example.events_app.config;

import com.example.events_app.entity.Event;
import com.example.events_app.entity.EventType;
import com.example.events_app.repository.EventRepository;
import com.example.events_app.repository.EventTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TestDataLoader implements CommandLineRunner {

    private final EventTypeRepository eventTypeRepository;
    private final EventRepository eventRepository;

    @Override
    public void run(String... args) throws Exception {
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
                event.setUserId(1);
                events.add(event);
            }

            eventRepository.saveAll(events);
        }
    }
}
