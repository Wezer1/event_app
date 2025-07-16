package com.example.events_app.repository;

import com.example.events_app.entity.Event;
import com.example.events_app.entity.EventParticipant;
import com.example.events_app.entity.EventParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, EventParticipantId>, JpaSpecificationExecutor<EventParticipant> {
    @Query("SELECT ep.id.eventId FROM EventParticipant ep WHERE ep.id.userId = :userId")
    List<Integer> findEventIdsByUserId(@Param("userId") Integer userId);

    @Query("SELECT COUNT(ep) FROM EventParticipant ep WHERE ep.id.userId = :userId AND ep.status = 'CONFIRMED'")
    long countByUserIdAndConfirmedStatus(@Param("userId") Integer userId);


    @Query("SELECT COUNT(ep) FROM EventParticipant ep WHERE ep.id.userId = :userId AND ep.status = 'CANCELLED'")
    long countCancelledEventsByUser(@Param("userId") Integer userId);
    List<EventParticipant> findById_UserId(Integer userId);
    List<EventParticipant> findById_EventId(Integer userId);

    Integer countEventParticipantByUserId (Integer userId);
}
