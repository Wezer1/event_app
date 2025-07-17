package com.example.events_app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "event_images")
@Data
public class EventImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false, length = 512)
    private String filePath;
}
