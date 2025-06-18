package com.example.events_app.repository;

import com.example.events_app.entity.UserBonusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBonusHistoryRepository extends JpaRepository<UserBonusHistory, Integer> {
    List<UserBonusHistory> findByUserId(Integer userId);
}
