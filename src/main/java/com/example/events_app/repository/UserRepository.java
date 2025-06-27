package com.example.events_app.repository;

import com.example.events_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByLogin(String login);

    @Modifying
    @Query("UPDATE User u SET u.registeredEventsCount = u.registeredEventsCount + 1 WHERE u.id = :userId")
    void incrementRegisteredEventsCount(@Param("userId") Integer userId);

    @Modifying
    @Query("UPDATE User u SET u.totalBonusPoints = u.totalBonusPoints + :points WHERE u.id = :userId")
    void updateTotalBonusPoints(@Param("userId") Integer userId, @Param("points") int points);


}
