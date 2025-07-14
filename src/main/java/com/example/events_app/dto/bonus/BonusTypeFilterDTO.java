package com.example.events_app.dto.bonus;

import com.example.events_app.model.SortDirection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BonusTypeFilterDTO {
    private String name;
    private String description;

    private String sortBy = "name"; // Поле по умолчанию
    private SortDirection sortOrder = SortDirection.DESC; // Направление по умолчанию

    // Пагинация
    private int page = 0;
    private int size = 10;
}
