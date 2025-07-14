package com.example.events_app.repository;

import com.example.events_app.entity.BonusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BonusTypeRepository extends JpaRepository<BonusType, Integer>
        , JpaSpecificationExecutor<BonusType> {
    boolean existsByName(String name);
}
