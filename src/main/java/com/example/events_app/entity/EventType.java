package com.example.events_app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "event_types")
@Data
public class EventType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "events_count")
    private Integer eventsCount;
}
