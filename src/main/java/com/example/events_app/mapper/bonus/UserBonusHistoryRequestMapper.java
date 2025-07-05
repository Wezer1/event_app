package com.example.events_app.mapper.bonus;

import com.example.events_app.dto.bonus.UserBonusHistoryRequestDTO;
import com.example.events_app.entity.UserBonusHistory;
import com.example.events_app.mapper.bonus.BonusTypeMapper;
import com.example.events_app.mapper.user.UserToShortMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {UserToShortMapper.class, BonusTypeMapper.class})
public abstract class UserBonusHistoryRequestMapper {

    @Mappings({
            @Mapping(source = "userId", target = "user.id"),
            @Mapping(source = "bonusTypeId", target = "bonusType.id")
    })
    public abstract UserBonusHistory toEntity(UserBonusHistoryRequestDTO dto);

    @Mappings({
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "bonusType.id", target = "bonusTypeId")
    })
    public abstract UserBonusHistoryRequestDTO toDto(UserBonusHistory entity);
}
