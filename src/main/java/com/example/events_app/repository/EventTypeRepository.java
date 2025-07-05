package com.example.events_app.repository;

import com.example.events_app.entity.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventTypeRepository extends JpaRepository<EventType, Integer> {
    boolean existsByName(String name);
}
