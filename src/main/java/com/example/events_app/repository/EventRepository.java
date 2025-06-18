package com.example.events_app.repository;

import com.example.events_app.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventRepository extends JpaRepository<Event,Integer>, JpaSpecificationExecutor<Event> {
}
