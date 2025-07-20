package com.example.events_app.filter;

import com.example.events_app.dto.event.EventParticipantFilterDTO;
import com.example.events_app.entity.EventParticipant;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventParticipantSpecification {

    public static Specification<EventParticipant> withFilter(EventParticipantFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getUserId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id").get("userId"), filter.getUserId()));
            }

            if (filter.getEventId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id").get("eventId"), filter.getEventId()));
            }

            if (filter.getEventTitle() != null && !filter.getEventTitle().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("event").get("title")),
                        "%" + filter.getEventTitle().toLowerCase() + "%"
                ));
            }

            if (filter.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filter.getStatus()));
            }

            if (filter.getMembershipStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("membershipStatus"), filter.getMembershipStatus()));
            }

            if (filter.getCreatedAtFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), filter.getCreatedAtFrom()));
            }

            if (filter.getCreatedAtTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), filter.getCreatedAtTo()));
            }

            if (filter.getEventStartTimeFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("event").get("startTime"),
                        filter.getEventStartTimeFrom()
                ));
            }

            if (filter.getEventStartTimeTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("event").get("startTime"),
                        filter.getEventStartTimeTo()
                ));
            }

            if (filter.getEventConducted() != null) {
                if (filter.getEventConducted()) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(
                            root.get("event").get("startTime"),
                            LocalDateTime.now()
                    ));
                } else {
                    predicates.add(criteriaBuilder.greaterThan(
                            root.get("event").get("startTime"),
                            LocalDateTime.now()
                    ));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
