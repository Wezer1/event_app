package com.example.events_app.service;

import com.example.events_app.dto.UserBonusHistoryDTO;
import com.example.events_app.entity.User;
import com.example.events_app.entity.UserBonusHistory;
import com.example.events_app.exceptions.NoSuchException;
import com.example.events_app.mapper.UserBonusHistoryMapper;
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
    private final UserBonusHistoryMapper mapper;
    private final UserRepository userRepository;


    @Transactional(readOnly = true)
    public List<UserBonusHistoryDTO> getAllByUserId(Integer userId) {
        log.info("Getting bonus history for user ID: {}", userId);
        List<UserBonusHistory> list = repository.findByUserId(userId);
        if (list.isEmpty()) {
            throw new NoSuchException("No bonus history found for user ID: " + userId);
        }
        return list.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserBonusHistoryDTO getById(Integer id) {
        log.info("Getting bonus history record by ID: {}", id);
        UserBonusHistory entity = repository.findById(id)
                .orElseThrow(() -> new NoSuchException("Bonus history not found with ID: " + id));
        return mapper.toDto(entity);
    }

    @Transactional
    public UserBonusHistoryDTO create(UserBonusHistoryDTO dto) {
        log.info("Creating user bonus history: {}", dto);

        LocalDateTime now = LocalDateTime.now();
        dto.setCreatedAt(now);
        // Проверка на наличие userId
        if (dto.getUserId() == null) {
            throw new NoSuchException("userId is null");
        }
        Optional<User> userOptional = Optional.ofNullable(userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoSuchException("There is no user with ID = "+ dto.getUserId() + " in Database")));

        UserBonusHistory saved = repository.save(mapper.toEntity(dto));
        return mapper.toDto(saved);
    }

    @Transactional
    public UserBonusHistoryDTO update(Integer id, UserBonusHistoryDTO dto) {
        log.info("Updating bonus history with ID {}: {}", id, dto);

        UserBonusHistory existing = repository.findById(id)
                .orElseThrow(() -> new NoSuchException("Bonus history not found with ID: " + id));

        LocalDateTime now = LocalDateTime.now();
        dto.setCreatedAt(now);
        UserBonusHistory updated = mapper.toEntity(dto);
        updated.setId(existing.getId()); // сохраняем ID

        UserBonusHistory saved = repository.save(updated);
        return mapper.toDto(saved);
    }

    @Transactional
    public void delete(Integer id) {
        log.info("Deleting bonus history with ID: {}", id);
        if (!repository.existsById(id)) {
            throw new NoSuchException("Bonus history not found with ID: " + id);
        }
        repository.deleteById(id);
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
