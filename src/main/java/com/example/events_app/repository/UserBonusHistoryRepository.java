package com.example.events_app.repository;

import com.example.events_app.entity.UserBonusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserBonusHistoryRepository extends JpaRepository<UserBonusHistory, Integer>,
        JpaSpecificationExecutor<UserBonusHistory> {
    List<UserBonusHistory> findByUserId(Integer userId);
}
