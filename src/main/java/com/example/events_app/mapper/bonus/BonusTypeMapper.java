package com.example.events_app.mapper.bonus;

import com.example.events_app.dto.bonus.BonusTypeDTO;
import com.example.events_app.entity.BonusType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class BonusTypeMapper {
    public abstract BonusType toEntity(BonusTypeDTO dto);
    public abstract BonusTypeDTO toDto(BonusType entity);
}
