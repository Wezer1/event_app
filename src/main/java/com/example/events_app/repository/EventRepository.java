package com.example.events_app.repository;

import com.example.events_app.entity.Event;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event,Integer>, JpaSpecificationExecutor<Event> {

    List<Event> findByConductedTrue();
    @Query("SELECT COUNT(e) FROM Event e WHERE e.eventType.id = :id")
    Integer countByEventTypeId(@Param("id") Integer id);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.user.id = :userId")
    int countByUserId(@Param("userId") Integer userId);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.user.id = :userId AND e.conducted = true")
    int countByUserIdAndConducted(@Param("userId") Integer userId, @Param("conducted") boolean conducted);
    @Query("SELECT COUNT(e) FROM Event e WHERE e.startTime <= :currentTime " +
            "AND e.endTime >= :currentTime AND e.user.id = :userId")
    long countActiveEventsByUser(
            @Param("currentTime") LocalDateTime currentTime,
            @Param("userId") Integer userId
    );

    // Количество предстоящих событий (start_time в будущем)
    @Query("SELECT COUNT(e) FROM Event e WHERE e.startTime > :currentTime AND e.user.id = :userId")
    long countUpcomingEvents(
            @Param("currentTime") LocalDateTime currentTime,
            @Param("userId") Integer userId
    );
    // Количество завершенных событий (end_time в прошлом ИЛИ conducted = true)
    @Query("SELECT COUNT(e) FROM Event e WHERE (e.endTime < :currentTime OR e.conducted = true) AND e.user.id = :userId")
    long countCompletedEvents(
            @Param("currentTime") LocalDateTime currentTime,
            @Param("userId") Integer userId
    );
}
