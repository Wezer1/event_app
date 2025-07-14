package com.example.events_app.dto.bonus;

import com.example.events_app.model.SortDirection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBonusHistoryFilterDTO {

    private Integer userId;
    private Integer bonusTypeId;
    private LocalDateTime createdAtFrom;
    private LocalDateTime createdAtTo;
    private Boolean isActive;

    private String sortBy = "isActive"; // Поле по умолчанию
    private SortDirection sortOrder = SortDirection.DESC; // Направление по умолчанию

    // Пагинация
    private int page = 0;
    private int size = 10;


}
