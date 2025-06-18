package com.example.events_app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "bonus_types")
@Data
public class BonusType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;
}
