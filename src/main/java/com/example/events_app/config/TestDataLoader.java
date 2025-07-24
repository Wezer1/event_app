package com.example.events_app.config;

import com.example.events_app.dto.user.UserRegistrationRequestDto;
import com.example.events_app.entity.*;
import com.example.events_app.mapper.user.UserRegisterRequestMapper;
import com.example.events_app.model.EventParticipantStatus;
import com.example.events_app.model.Role;
import com.example.events_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TestDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserRegisterRequestMapper userRegisterRequestMapper;
    private final BonusTypeRepository bonusTypeRepository;
    private final EventTypeRepository eventTypeRepository;
    private final UserBonusHistoryRepository userBonusHistoryRepository;
    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private static final Logger logger = LoggerFactory.getLogger(TestDataLoader.class);


    @Override
    public void run(String... args) throws Exception {

        // Создаем первого пользователя (USER)

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
            logger.info("Добавлено {} типов бонусов в базу данных.", bonusTypes.size());
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
            logger.info("Добавлено {} типов мероприятий в базу данных.", types.size());

        }

        // 3. User
        if (userRepository.count() == 0) {
            List<User> users = new ArrayList<>();

            // Реалистичные имена для пользователей
            String[] firstNames = {"Иван", "Петр", "Сергей", "Александр", "Дмитрий",
                    "Максим", "Евгений", "Олег", "Владимир", "Николай"};
            String[] lastNames = {"Иванов", "Петров", "Смирнов", "Кузнецов", "Соколов",
                    "Лебедев", "Новиков", "Григорьев", "Васильев", "Родионов"};

            // Создаем 10 пользователей с реальными именами
            for (int i = 0; i < 10; i++) {
                String fullName = firstNames[i] + " " + lastNames[i];
                String login = "user" + (i + 1);
                String email = login + "@example.com";
                String phone = "+790000000" + (i + 1);

                UserRegistrationRequestDto dto = new UserRegistrationRequestDto(
                        fullName,
                        login,
                        "password",
                        Role.USER,
                        email,
                        phone
                );

                User user = userRegisterRequestMapper.toEntity(dto);
                userRepository.save(user);
                users.add(user);

                System.out.println("Создан пользователь: " + dto.getLogin());
            }

            String[] organizationNames = {
                    "ООО \"Рога и Копыта\"",
                    "ПАО \"Газпром\"",
                    "ЗАО \"Сбербанк\"",
                    "ИП \"Иванов И.И.\"",
                    "ТД \"Строительство\"",
                    "ООО \"IT-Профи\"",
                    "ООО \"АвтоСервис\"",
                    "ООО \"Электроника\"",
                    "ООО \"ФудЛэнд\"",
                    "ООО \"Юридические услуги\""
            };

            // Создаем несколько организаций
            for (int i = 0; i < 5; i++) { // Например, 5 организаций
                String fullName = organizationNames[i];
                String login = "org" + (i + 1);
                String email = login + "@example.com";
                String phone = "+7987654321" + (i + 1);

                UserRegistrationRequestDto orgDto = new UserRegistrationRequestDto(
                        fullName,
                        login,
                        "orgpass",
                        Role.ORGANIZATION,
                        email,
                        phone
                );
                User organization = userRegisterRequestMapper.toEntity(orgDto);
                userRepository.save(organization);
                users.add(organization);
            }
            userRepository.saveAll(users);
            UserRegistrationRequestDto user1 = new UserRegistrationRequestDto(
                    "User Userov",   // full_name
                    "user",          // login
                    "user",          // password
                    Role.USER,        // role
                    "user",
                    "user"
            );

            // Создаем второго пользователя (ADMIN)
            UserRegistrationRequestDto user2 = new UserRegistrationRequestDto(
                    "Admin Adminov",
                    "admin",
                    "admin",
                    Role.ORGANIZATION,
                    "admin",
                    "admin"
            );

            // Сохраняем, если ещё не существует
            if (userRepository.findByLogin(user1.getLogin()).isEmpty()) {
                userRepository.save(userRegisterRequestMapper.toEntity(user1));
                System.out.println("Создан пользователь: " + user1.getLogin());
            }

            if (userRepository.findByLogin(user2.getLogin()).isEmpty()) {
                userRepository.save(userRegisterRequestMapper.toEntity(user2));
                System.out.println("Создан пользователь: " + user2.getLogin());
            }
            logger.info("Добавлено {} пользователей в базу данных.", users.size());

        }


        // 5. Event
        if (eventRepository.count() == 0) {
            List<Event> events = new ArrayList<>();
            List<EventType> allTypes = eventTypeRepository.findAll();

            // Получаем список пользователей с ролью ORGANIZATION
            List<User> organizationUsers = userRepository.findByRole(Role.ORGANIZATION)
                    .stream()
                    .collect(Collectors.toList());

            if (organizationUsers.isEmpty()) {
                logger.warn("Нет пользователей с ролью ORGANIZATION для создания мероприятий.");
            }


            String[] titleList = {
                    "Технологическая конференция",
                    "Финансовый форум",
                    "Медицинская выставка",
                    "Научный симпозиум",
                    "Культурный фестиваль",
                    "Спортивное мероприятие",
                    "Образовательный семинар",
                    "Экологическая конференция",
                    "Кинофестиваль",
                    "Книжная ярмарка",
                    "IT-Стартап-демодень",
                    "Бизнес-форум будущего",
                    "Архитектурный форум",
                    "Инвестиционный саммит",
                    "Фармацевтическая конференция",
                    "Автомобильный экспофорум",
                    "Детский образовательный лагерь",
                    "Выставка современного искусства",
                    "Фестиваль уличной еды",
                    "Хакатон по ИИ",
                    "Телекоммуникационный форум",
                    "Робототехнический чемпионат",
                    "Креативный кластер",
                    "Платформа инноваций",
                    "Цифровая трансформация",
                    "Лидерство и управление",
                    "HR-технологии завтрашнего дня",
                    "Тренды маркетинга 2026",
                    "Блокчейн и криптоиндустрия",
                    "Климатические технологии",
                    "Городская устойчивость",
                    "Умные города будущего",
                    "Энергетический переход",
                    "Зелёная экономика",
                    "Сельскохозяйственные инновации",
                    "Агротехнологии",
                    "Технологии питания",
                    "Биотехнологии",
                    "Генная инженерия",
                    "Нанотехнологии",
                    "Космические технологии",
                    "Будущее авиации",
                    "Индустрия развлечений",
                    "Музыкальный фестиваль",
                    "Фестиваль документального кино",
                    "Театральный фестиваль",
                    "Фестиваль уличного искусства",
                    "Фестиваль света и технологий",
                    "Мода и дизайн",
                    "Дизайн-выставка",
                    "Фестиваль моды",
                    "Текстильная выставка",
                    "Создание бренда",
                    "Рекламные технологии",
                    "Реклама будущего",
                    "Прямые продажи",
                    "E-commerce Summit",
                    "Мобильные технологии",
                    "Тренды мобильной связи",
                    "5G и далее",
                    "Умный дом",
                    "Умный город",
                    "Умный офис",
                    "Безопасность данных",
                    "Кибербезопасность",
                    "Защита персональных данных",
                    "Цифровые активы",
                    "Цифровые деньги",
                    "Финансовая грамотность",
                    "Бюджетирование для стартапов",
                    "Финансовые технологии",
                    "Стартап-акселератор",
                    "Будущее банковского дела",
                    "Управление рисками",
                    "Инвестиции в недвижимость",
                    "ПИФы и инвестиционные фонды",
                    "Краудфандинг",
                    "Краудсорсинг",
                    "Инвестиционные платформы",
                    "Финансовый анализ",
                    "Аналитика больших данных",
                    "Искусственный интеллект",
                    "Машинное обучение",
                    "Глубокое обучение",
                    "Робототехника",
                    "Автономные системы",
                    "Беспилотники",
                    "Автономные автомобили",
                    "Технологии дополненной реальности",
                    "Технологии виртуальной реальности",
                    "AR/VR в образовании",
                    "AR/VR в медицине",
                    "Геймификация",
                    "Технологии игр",
                    "Мобильные игры",
                    "Серверные решения",
                    "Облачные технологии",
                    "Облачные сервисы",
                    "Облачная аналитика",
                    "Системы хранения данных",
                    "Интернет вещей",
                    "IoT в быту",
                    "IoT в промышленности",
                    "Технологии безопасности",
                    "Шифрование",
                    "Открытые ключи",
                    "Блокчейн",
                    "Децентрализованные приложения",
                    "Криптокошельки",
                    "NFT",
                    "Цифровое искусство",
                    "Цифровые коллекции",
                    "Цифровые права",
                    "Технологии блокчейн",
                    "Технологии смарт-контрактов",
                    "Управление проектами",
                    "Agile",
                    "Scrum",
                    "DevOps",
                    "CI/CD",
                    "Тестирование ПО",
                    "Тестирование в облаке",
                    "QA-автоматизация",
                    "Технические стартапы",
                    "Инкубатор стартапов",
                    "Акселератор стартапов",
                    "Стартап-чемпионат",
                    "Инвестиционный пул",
                    "Технопарк",
                    "Научно-исследовательский центр",
                    "Технополис",
                    "Инновационный кластер",
                    "Технологическое партнерство",
                    "Технологии будущего",
                    "Футурология",
                    "Будущее образования",
                    "Будущее здравоохранения",
                    "Будущее финансов",
                    "Будущее транспорта",
                    "Будущее коммуникаций",
                    "Будущее производства",
                    "Будущее энергетики",
                    "Будущее сельского хозяйства",
                    "Будущее торговли",
                    "Будущее строительства",
                    "Будущее медиа",
                    "Будущее культуры",
                    "Будущее спорта",
                    "Будущее туризма",
                    "Будущее экологии",
                    "Будущее работы",
                    "Будущее управления",
                    "Будущее взаимодействия",
                    "Будущее монетизации",
                    "Будущее рекламы",
                    "Будущее дизайна",
                    "Будущее архитектуры",
                    "Будущее музыки",
                    "Будущее кино",
                    "Будущее театра",
                    "Будущее литературы",
                    "Будущее соцсетей",
                    "Будущее искусственного интеллекта",
                    "Будущее цифровых валют",
                    "Будущее интернета",
                    "Будущее связи",
                    "Будущее телевидения",
                    "Будущее радио",
                    "Будущее печати",
                    "Будущее контента",
                    "Будущее медиаобразования",
                    "Будущее цифровой культуры",
                    "Будущее цифрового общества",
                    "Будущее цифрового права",
                    "Будущее цифровой политики",
                    "Будущее цифровой экономики",
                    "Будущее цифрового бизнеса",
                    "Будущее цифрового рынка",
                    "Будущее цифровой торговли",
                    "Будущее цифрового потребления",
                    "Будущее цифровой жизни",
                    "Будущее цифровой среды",
                    "Будущее цифровой планеты"
            };


            String[] descriptions = {
                    "Конференция по последним технологиям в области IT.",
                    "Форум, посвященный финансовым инновациям и инвестициям.",
                    "Выставка медицинского оборудования и технологий.",
                    "Симпозиум, посвященный последним научным исследованиям.",
                    "Фестиваль, представляющий культурное разнообразие.",
                    "Спортивное мероприятие с участием международных команд.",
                    "Семинар по современным образовательным технологиям.",
                    "Конференция, посвященная вопросам экологии и устойчивого развития.",
                    "Фестиваль, демонстрирующий лучшие фильмы года.",
                    "Ярмарка, на которой представлены книги от ведущих издательств."
            };

            String[] locations = {
                    "Москва, Кремль", "Санкт-Петербург, Экспофорум", "Казань, Международный центр",
                    "Сочи, Олимпийский парк", "Новосибирск, Конгресс-холл", "Екатеринбург, Выставочный комплекс",
                    "Нижний Новгород, Конференц-зал", "Краснодар, Культурный центр",
                    "Владивосток, Морской терминал", "Калининград, Исторический музей"
            };

            for (int i = 0; i < 2000; i++) {
                EventType type = allTypes.get((int) (Math.random() * allTypes.size()));

                // Базовый день — сегодня + случайное число дней (до 60)
                LocalDateTime baseDay = now.plusDays((long) (Math.random() * 60));

                // Базовое время в этот день: например, от 9:00 до 21:00
                LocalTime baseTime = LocalTime.of(
                        9 + (int) (Math.random() * 12),   // часы от 9 до 20
                        (int) (Math.random() * 60),       // минуты
                        (int) (Math.random() * 60)        // секунды
                );

                // Собираем дату-время из дня и времени
                LocalDateTime baseStart = baseDay.with(baseTime);

                // Небольшое отклонение ±1 час (в минутах: -60 до +60)
                long deviationMinutes = (long) (Math.random() * 120) - 60;
                LocalDateTime start = baseStart.plusMinutes(deviationMinutes);

                // Длительность события — от 1 до 3 часов
                LocalDateTime end = start.plusHours(1 + (long) (Math.random() * 3));

                Event event = new Event();
                event.setTitle(titleList[((int) (Math.random() * titleList.length))]);
                event.setDescription(descriptions[(int) (Math.random() * descriptions.length)]);
                event.setStartTime(start);
                event.setEndTime(end);
                event.setLocation(locations[(int) (Math.random() * locations.length)]);
                event.setCreatedAt(now);
                event.setUpdatedAt(now);
                if (i % 2 == 0){
                    event.setConducted(false);
                }else{
                    event.setConducted(true);
                }
                event.setEventType(type);

                // Устанавливаем пользователя только из числа организаций
                if (!organizationUsers.isEmpty()) {
                    event.setUser(organizationUsers.get(i % organizationUsers.size()));
                } else {
                    event.setUser(null); // или удалите строку, если поле не nullable
                }

                events.add(event);
            }

            eventRepository.saveAll(events);
            logger.info("Добавлено {} мероприятий в базу данных.", events.size());
        }


        // 6. EventParticipant
        if (eventParticipantRepository.count() == 0) {

            // Убедитесь, что пользователи с логинами "user" и "admin" существуют
            User user = userRepository.findByLogin("user").get();
            User admin = userRepository.findByLogin("admin").get();

            List<EventParticipant> participants = new ArrayList<>();

            List<Event> allEvents = eventRepository.findAll();

            // Получаем только пользователей с ролью USER
            List<User> regularUsers = userRepository.findByRole(Role.USER)
                    .stream()
                    .collect(Collectors.toList());

            for (Event event : allEvents) {
                int participantsCount = (int) (Math.random() * 5) + 1;

                for (int j = 0; j < participantsCount; j++) {
                    // Выбираем только среди пользователей с ролью USER
                    User randomUser = regularUsers.get((int) (Math.random() * regularUsers.size()));

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

                // Добавляем пользователей "user" и "admin" на каждое событие
                if (user != null) {
                    EventParticipant userParticipant = new EventParticipant();
                    EventParticipantId userId = new EventParticipantId();
                    userId.setUserId(user.getId());
                    userId.setEventId(event.getId());
                    userParticipant.setId(userId);
                    userParticipant.setUser(user);
                    userParticipant.setEvent(event);
                    userParticipant.setStatus(EventParticipantStatus.CONFIRMED);
                    userParticipant.setCreatedAt(now.minusDays(1));
                    participants.add(userParticipant);
                }

                if (admin != null) {
                    EventParticipant adminParticipant = new EventParticipant();
                    EventParticipantId adminId = new EventParticipantId();
                    adminId.setUserId(admin.getId());
                    adminId.setEventId(event.getId());
                    adminParticipant.setId(adminId);
                    adminParticipant.setUser(admin);
                    adminParticipant.setEvent(event);
                    adminParticipant.setStatus(EventParticipantStatus.CONFIRMED);
                    adminParticipant.setCreatedAt(now.minusDays(1));
                    participants.add(adminParticipant);
                }
            }
            eventParticipantRepository.saveAll(participants);
            logger.info("Добавлено {} записи на мероприятие пользователя в базу данных.", participants.size());
        }
// 4. UserBonusHistory
        if (userBonusHistoryRepository.count() == 0) {
            List<UserBonusHistory> histories = new ArrayList<>();
            List<BonusType> allBonusTypes = bonusTypeRepository.findAll();

            // Получаем только проведенные события (isConducted = true)
            List<Event> conductedEvents = eventRepository.findByConductedTrue();

            // Получаем только пользователей с ролью USER
            List<User> usersWithUserRole = userRepository.findByRole(Role.USER)
                    .stream()
                    .collect(Collectors.toList());

            // Проверяем, что есть проведенные события
            if (!conductedEvents.isEmpty()) {
                for (User user : usersWithUserRole) {
                    for (int i = 0; i < 2; i++) {
                        UserBonusHistory history = new UserBonusHistory();
                        history.setUser(user);
                        history.setBonusType(allBonusTypes.get(i % allBonusTypes.size()));
                        history.setAmount((i + 1) * 10);
                        history.setReason("Бонус за участие");
                        history.setCreatedAt(now.minusDays(i));
                        history.setActive(true);

                        // Связываем с проведенным событием (циклически)
                        Event conductedEvent = conductedEvents.get(i % conductedEvents.size());
                        history.setEvent(conductedEvent);

                        histories.add(history);
                    }
                }

                userBonusHistoryRepository.saveAll(histories);
                logger.info("Добавлено {} записей истории бонусов для пользователей с ролью USER.", histories.size());
            } else {
                logger.warn("Не найдено проведенных событий (isConducted=true). Бонусы не были начислены.");
            }
        }

    }
}