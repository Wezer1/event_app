package com.example.events_app.repository;

import com.example.events_app.entity.EventParticipant;
import com.example.events_app.entity.EventParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, EventParticipantId> {
    List<EventParticipant> findById_UserId(Integer userId);
    List<EventParticipant> findById_EventId(Integer userId);

    Integer countEventParticipantByUserId (Integer userId);
}
