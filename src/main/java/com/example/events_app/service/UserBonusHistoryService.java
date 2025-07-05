package com.example.events_app.service;

import com.example.events_app.dto.bonus.UserBonusHistoryRequestDTO;
import com.example.events_app.dto.bonus.UserBonusHistoryResponseMediumDTO;
import com.example.events_app.dto.bonus.UserBonusHistoryResponseShortDTO;
import com.example.events_app.entity.BonusType;
import com.example.events_app.entity.User;
import com.example.events_app.entity.UserBonusHistory;
import com.example.events_app.exceptions.NoSuchException;
import com.example.events_app.mapper.bonus.UserBonusHistoryRequestMapper;
import com.example.events_app.mapper.bonus.UserBonusHistoryResponseMediumMapper;
import com.example.events_app.mapper.bonus.UserBonusHistoryResponseShortMapper;
import com.example.events_app.repository.BonusTypeRepository;
import com.example.events_app.repository.UserBonusHistoryRepository;
import com.example.events_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserBonusHistoryService {

    private final UserBonusHistoryRepository repository;
    private final BonusTypeRepository bonusTypeRepository;
    private final UserBonusHistoryResponseShortMapper userBonusHistoryResponseShortMapper;
    private final UserBonusHistoryResponseMediumMapper userBonusHistoryResponseMediumMapper;
    private final UserBonusHistoryRequestMapper userBonusHistoryRequestMapper;
    private final UserRepository userRepository;


    @Transactional(readOnly = true)
    public List<UserBonusHistoryResponseShortDTO> getAllByUserId(Integer userId) {
        log.info("Getting bonus history for user ID: {}", userId);
        List<UserBonusHistory> list = repository.findByUserId(userId);
        if (list.isEmpty()) {
            throw new NoSuchException("No bonus history found for user ID: " + userId);
        }
        return list.stream()
                .map(userBonusHistoryResponseShortMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserBonusHistoryResponseMediumDTO getById(Integer id) {
        log.info("Getting bonus history record by ID: {}", id);
        UserBonusHistory entity = repository.findById(id)
                .orElseThrow(() -> new NoSuchException("Bonus history not found with ID: " + id));
        return userBonusHistoryResponseMediumMapper.toDto(entity);
    }

    @Transactional
    public UserBonusHistoryResponseShortDTO create(UserBonusHistoryRequestDTO dto) {
        log.info("Creating user bonus history: {}", dto);

        Integer userId = dto.getUserId();
        Integer bonusTypeId = dto.getBonusTypeId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchException("User not found with ID: " + userId));

        BonusType bonusType = bonusTypeRepository.findById(bonusTypeId)
                .orElseThrow(() -> new NoSuchException("Bonus type not found"));

        UserBonusHistory entity = userBonusHistoryRequestMapper.toEntity(dto);
        entity.setUser(user);
        entity.setBonusType(bonusType);
        entity.setCreatedAt(LocalDateTime.now());

        UserBonusHistory saved = repository.save(entity);

        return userBonusHistoryResponseShortMapper.toDto(saved);
    }

    @Transactional
    public UserBonusHistoryResponseShortDTO update(Integer id, UserBonusHistoryRequestDTO dto) {
        log.info("Updating bonus history with ID {}: {}", id, dto);

        UserBonusHistory existing = repository.findById(id)
                .orElseThrow(() -> new NoSuchException("Bonus history not found with ID: " + id));

        LocalDateTime now = LocalDateTime.now();
        dto.setCreatedAt(now);
        UserBonusHistory updated = userBonusHistoryRequestMapper.toEntity(dto);
        updated.setId(existing.getId()); // сохраняем ID

        UserBonusHistory saved = repository.save(updated);
        return userBonusHistoryResponseShortMapper.toDto(saved);
    }

    @Transactional
    public void delete(Integer id) {
        log.info("Deleting bonus history with ID: {}", id);

        UserBonusHistory history = repository.findById(id)
                .orElseThrow(() -> new NoSuchException("Bonus history not found with ID: " + id));

        Integer userId = history.getUser().getId();
        int bonusValue = Optional.ofNullable(history.getAmount()).orElse(0);

        // Удаляем запись
        repository.deleteById(id);

        // Уменьшаем общий баланс
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchException("There is no user with ID = " + userId));
        user.setTotalBonusPoints(user.getTotalBonusPoints()-bonusValue);

    }


    @Transactional
    public void activateBonus(Integer id) {
        log.info("Activating bonus with ID: {}", id);
        UserBonusHistory history = repository.findById(id)
                .orElseThrow(() -> new NoSuchException("Bonus history not found with ID: " + id));
        history.setActive(true);
        repository.save(history);
    }

    @Transactional
    public void deactivateBonus(Integer id) {
        log.info("Deactivating bonus with ID: {}", id);
        UserBonusHistory history = repository.findById(id)
                .orElseThrow(() -> new NoSuchException("Bonus history not found with ID: " + id));
        history.setActive(false);
        repository.save(history);
    }
}
