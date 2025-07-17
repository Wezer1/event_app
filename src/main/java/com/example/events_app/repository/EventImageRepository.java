package com.example.events_app.repository;

import com.example.events_app.entity.EventImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventImageRepository extends JpaRepository<EventImage, Integer> {
        List<EventImage> findByEvent_Id(Integer eventId);
}
