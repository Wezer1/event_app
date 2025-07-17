package com.example.events_app.repository;

import com.example.events_app.entity.Event;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event,Integer>, JpaSpecificationExecutor<Event> {
    @Query("SELECT COUNT(e) FROM Event e WHERE e.eventType.id = :id")
    Integer countByEventTypeId(@Param("id") Integer id);

    @EntityGraph(attributePaths = {"images"})
    @Query("SELECT e FROM Event e WHERE e.id = :eventId")
    Optional<Event> findByIdWithImages(@Param("eventId") Integer eventId);
}
