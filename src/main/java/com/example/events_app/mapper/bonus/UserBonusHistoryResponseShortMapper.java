package com.example.events_app.mapper.bonus;

import com.example.events_app.dto.bonus.UserBonusHistoryResponseShortDTO;
import com.example.events_app.entity.UserBonusHistory;
import com.example.events_app.mapper.user.UserToShortMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {UserToShortMapper.class, BonusTypeMapper.class})
public abstract class UserBonusHistoryResponseShortMapper {

    // Entity -> DTO
    @Mappings({
            @Mapping(source = "user", target = "user"),       // автоматически найдет UserMapper.userToUserShortDTO()
            @Mapping(source = "bonusType", target = "bonusType") // найдет BonusTypeMapper.bonusTypeToBonusTypeDTO()
    })
    public abstract UserBonusHistoryResponseShortDTO toDto(UserBonusHistory entity);

    // DTO -> Entity
    @Mappings({
            @Mapping(source = "user.id", target = "user.id"),         // user.id → entity.user.id
            @Mapping(source = "bonusType.id", target = "bonusType.id") // bonusType.id → entity.bonusType.id
    })
    public abstract UserBonusHistory toEntity(UserBonusHistoryResponseShortDTO dto);
}