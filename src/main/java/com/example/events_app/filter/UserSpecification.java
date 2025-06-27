package com.example.events_app.filter;

import com.example.events_app.dto.user.UserFilterDTO;
import com.example.events_app.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> withFilter(UserFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getFirstName() != null) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("firstName")),
                        "%" + filter.getFirstName().toLowerCase() + "%"));
            }

            if (filter.getLastName() != null) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("lastName")),
                        "%" + filter.getLastName().toLowerCase() + "%"));
            }

            if (filter.getLogin() != null) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("login")),
                        "%" + filter.getLogin().toLowerCase() + "%"));
            }

            if (filter.getRegisteredEventsCount() != null) {
                predicates.add(criteriaBuilder.equal(root.get("registeredEventsCount"), filter.getRegisteredEventsCount()));
            }

            if (filter.getTotalBonusPoints() != null) {
                predicates.add(criteriaBuilder.equal(root.get("totalBonusPoints"), filter.getTotalBonusPoints()));
            }

            if (filter.getRole() != null) {
                predicates.add(criteriaBuilder.equal(root.get("role"), filter.getRole()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
