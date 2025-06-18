package com.example.events_app.mapper;

import com.example.events_app.dto.UserBonusHistoryDTO;
import com.example.events_app.entity.UserBonusHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = BonusTypeMapper.class)
public abstract class UserBonusHistoryMapper {

    @Mappings({
            @Mapping(source = "userId", target = "user.id"), // <-- маппинг userId → user.id
            @Mapping(source = "bonusTypeId", target = "bonusType.id")
    })
    public abstract UserBonusHistory toEntity(UserBonusHistoryDTO dto);

    @Mappings({
            @Mapping(source = "user.id", target = "userId"), // <-- маппинг user.id → userId
            @Mapping(source = "bonusType.id", target = "bonusTypeId")
    })
    public abstract UserBonusHistoryDTO toDto(UserBonusHistory entity);
}