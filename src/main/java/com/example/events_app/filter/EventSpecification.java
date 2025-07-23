package com.example.events_app.filter;

import com.example.events_app.dto.event.EventFilterDTO;
import com.example.events_app.entity.Event;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class EventSpecification {

    public static Specification<Event> withFilter(EventFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Поиск по keyword в title, description, location
            if (filter.getKeyword() != null && !filter.getKeyword().isEmpty()) {
                String pattern = "%" + filter.getKeyword().toLowerCase() + "%";

                Predicate inTitle = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")), pattern);

                Predicate inDescription = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")), pattern);

                Predicate inLocation = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("location")), pattern);

                predicates.add(criteriaBuilder.or(inTitle, inDescription, inLocation));
            }

            // Фильтр по userId
            if (filter.getUserId() != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("user").get("id"), filter.getUserId())
                );
            }

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime defaultStart = now.minusYears(100);
            LocalDateTime defaultEnd = now.plusYears(100);

            LocalDateTime startFrom = filter.getStartDateFrom() != null ? filter.getStartDateFrom() : defaultStart;
            LocalDateTime startTo = filter.getStartDateTo() != null ? filter.getStartDateTo() : defaultEnd;
            predicates.add(criteriaBuilder.between(root.get("startTime"), startFrom, startTo));

            // Новый фильтр по дате окончания
            LocalDateTime endFrom = filter.getEndDateFrom() != null ? filter.getEndDateFrom() : defaultStart;
            LocalDateTime endTo = filter.getEndDateTo() != null ? filter.getEndDateTo() : defaultEnd;
            predicates.add(criteriaBuilder.between(root.get("endTime"), endFrom, endTo));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}