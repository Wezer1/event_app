package com.example.events_app.repository;

import com.example.events_app.entity.BonusType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BonusTypeRepository extends JpaRepository<BonusType, Integer> {
    boolean existsByName(String name);
}
