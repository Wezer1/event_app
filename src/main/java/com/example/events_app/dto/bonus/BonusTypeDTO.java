package com.example.events_app.dto.bonus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Тип бонуса")
public class BonusTypeDTO {
    private Integer id;
    private String name;
    private String description;
}
