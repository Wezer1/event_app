package com.example.events_app.repository;

import com.example.events_app.entity.Event;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event,Integer>, JpaSpecificationExecutor<Event> {
    @Query("SELECT COUNT(e) FROM Event e WHERE e.eventType.id = :id")
    Integer countByEventTypeId(@Param("id") Integer id);

    @EntityGraph(attributePaths = {"images"})
    @Query("SELECT e FROM Event e WHERE e.id = :eventId")
    Optional<Event> findByIdWithImages(@Param("eventId") Integer eventId);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.user.id = :userId")
    int countByUserId(@Param("userId") Integer userId);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.user.id = :userId AND e.conducted = true")
    int countByUserIdAndConducted(@Param("userId") Integer userId, @Param("conducted") boolean conducted);
    @Query("SELECT COUNT(e) FROM Event e WHERE e.startTime <= :currentTime AND e.endTime >= :currentTime")
    long countActiveEvents(@Param("currentTime") LocalDateTime currentTime);

    // Количество предстоящих событий (start_time в будущем)
    @Query("SELECT COUNT(e) FROM Event e WHERE e.startTime > :currentTime")
    long countUpcomingEvents(@Param("currentTime") LocalDateTime currentTime);

    // Количество завершенных событий (end_time в прошлом ИЛИ conducted = true)
    @Query("SELECT COUNT(e) FROM Event e WHERE e.endTime < :currentTime OR e.conducted = true")
    long countCompletedEvents(@Param("currentTime") LocalDateTime currentTime);
}
