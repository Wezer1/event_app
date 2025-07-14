package com.example.events_app.filter;

import com.example.events_app.dto.event.EventFilterForUserDTO;
import com.example.events_app.entity.Event;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class EventWithUserSpecification {
    public static Specification<Event> withFilter(EventFilterForUserDTO filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getTitle() != null && !filter.getTitle().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + filter.getTitle().toLowerCase() + "%"));
            }

            if (filter.getDescription() != null && !filter.getDescription().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("description")), "%" + filter.getDescription().toLowerCase() + "%"));
            }

            if (filter.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), filter.getDateFrom()));
            }

            if (filter.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), filter.getDateTo()));
            }

            // Если указан userIdForEventFilter — получаем только события этого пользователя
            if (filter.getUserIdForEventFilter() != null) {
                predicates.add(root.get("id").in(filter.getUserIdForEventFilter()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
