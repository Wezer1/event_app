package com.example.events_app.repository;

import com.example.events_app.entity.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventTypeRepository extends JpaRepository<EventType, Integer>,
        JpaSpecificationExecutor<EventType> {
    boolean existsByName(String name);

    @Query("SELECT DISTINCT et.name FROM EventType et " +
            "JOIN Event e ON et.id = e.eventType.id " +
            "WHERE e.user.id = :organizerId")
    List<String> findEventTypeNamesByOrganizer(@Param("organizerId") Integer organizerId);
}
