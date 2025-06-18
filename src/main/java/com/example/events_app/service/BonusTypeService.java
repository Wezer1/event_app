package com.example.events_app.service;

import com.example.events_app.dto.BonusTypeDTO;
import com.example.events_app.entity.BonusType;
import com.example.events_app.exceptions.NoSuchException;
import com.example.events_app.mapper.BonusTypeMapper;
import com.example.events_app.repository.BonusTypeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BonusTypeService {

    private final BonusTypeRepository bonusTypeRepository;
    private final BonusTypeMapper bonusTypeMapper;

    @Transactional(readOnly = true)
    public List<BonusTypeDTO> getAllBonusTypes() {
        log.info("Get all bonus types");
        if (bonusTypeRepository.findAll().isEmpty()) {
            throw new NoSuchException("No bonus types found");
        }
        return bonusTypeRepository.findAll().stream()
                .map(bonusTypeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BonusTypeDTO getBonusTypeById(Integer id) {
        log.info("Get bonus type by ID: {}", id);
        BonusType bonusType = bonusTypeRepository.findById(id)
                .orElseThrow(() -> new NoSuchException("Bonus type not found with ID: " + id));
        return bonusTypeMapper.toDto(bonusType);
    }

    @Transactional
    public BonusTypeDTO createBonusType(BonusTypeDTO dto) {
        log.info("Creating bonus type: {}", dto);

        // Проверка на уникальность имени
        if (bonusTypeRepository.existsByName(dto.getName())) {
            throw new DataIntegrityViolationException("Bonus type with name '" + dto.getName() + "' already exists.");
        }

        BonusType saved = bonusTypeRepository.save(bonusTypeMapper.toEntity(dto));
        return bonusTypeMapper.toDto(saved);
    }

    @Transactional
    public BonusTypeDTO updateBonusType(Integer id, BonusTypeDTO dto) {
        log.info("Updating bonus type with ID {}: {}", id, dto);

        BonusType existing = bonusTypeRepository.findById(id)
                .orElseThrow(() -> new NoSuchException("Bonus type not found with ID: " + id));

        // Проверка на изменение имени и его уникальность
        if (!existing.getName().equals(dto.getName()) &&
                bonusTypeRepository.existsByName(dto.getName())) {
            throw new DataIntegrityViolationException("Bonus type with name '" + dto.getName() + "' already exists.");
        }

        BonusType updated = bonusTypeMapper.toEntity(dto);
        updated.setId(existing.getId()); // сохраняем ID

        BonusType saved = bonusTypeRepository.save(updated);
        return bonusTypeMapper.toDto(saved);
    }

    @Transactional
    public void deleteBonusType(Integer id) {
        log.info("Deleting bonus type with ID: {}", id);
        if (!bonusTypeRepository.existsById(id)) {
            throw new NoSuchException("Bonus type not found with ID: " + id);
        }
        bonusTypeRepository.deleteById(id);
    }
}
