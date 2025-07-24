package com.example.events_app.repository;

import com.example.events_app.entity.UserBonusHistory;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserBonusHistoryRepository extends JpaRepository<UserBonusHistory, Integer>,
        JpaSpecificationExecutor<UserBonusHistory> {
    List<UserBonusHistory> findByUserId(Integer userId);
    List<UserBonusHistory> findByUserIdAndBonusTypeIdAndIsActive(Integer userId, Integer bonusTypeId, boolean isActive);
//    List<UserBonusHistory> findByUserIdAndBonusTypeAndActive(Integer userId, Integer bonusId, Boolean active);
}
