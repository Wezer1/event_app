package com.example.events_app.mapper.bonus;

import com.example.events_app.dto.bonus.UserBonusHistoryResponseMediumDTO;
import com.example.events_app.entity.UserBonusHistory;
import com.example.events_app.mapper.user.UserToMediumMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {UserToMediumMapper.class, BonusTypeMapper.class})
public abstract class UserBonusHistoryResponseMediumMapper {

    // Entity -> DTO
    @Mappings({
            @Mapping(source = "user", target = "user"),       // автоматически найдет UserToMediumMapper.userToUserMediumDTO()
            @Mapping(source = "bonusType", target = "bonusType") // найдет BonusTypeMapper.bonusTypeToBonusTypeDTO()
    })
    public abstract UserBonusHistoryResponseMediumDTO toDto(UserBonusHistory entity);

    // DTO -> Entity
    @Mappings({
            @Mapping(source = "user.id", target = "user.id"),
            @Mapping(source = "bonusType.id", target = "bonusType.id")
    })
    public abstract UserBonusHistory toEntity(UserBonusHistoryResponseMediumDTO dto);
}
