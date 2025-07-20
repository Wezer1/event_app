package com.example.events_app.dto.event;

import com.example.events_app.model.EventParticipantStatus;
import com.example.events_app.model.MembershipStatus;
import com.example.events_app.model.SortDirection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipantFilterDTO {

    private Integer userId;
    private Integer eventId;
    private String eventTitle; // Добавлено новое поле для фильтрации по названию события

    private EventParticipantStatus status;
    private MembershipStatus membershipStatus;

    private LocalDateTime createdAtFrom;
    private LocalDateTime createdAtTo;

    // Фильтр по полям события (event)
    private LocalDateTime eventStartTimeFrom;
    private LocalDateTime eventStartTimeTo;
    private Boolean eventConducted; // true - прошло, false - ещё не прошло

    // Сортировка
    private String sortBy = "membershipStatus"; // Поле по умолчанию
    private SortDirection sortOrder = SortDirection.DESC; // Направление по умолчанию

    // Пагинация
    private int page = 0;
    private int size = 30;
}