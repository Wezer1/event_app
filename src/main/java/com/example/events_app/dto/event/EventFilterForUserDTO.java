package com.example.events_app.dto.event;

import com.example.events_app.model.SortDirection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventFilterForUserDTO {
    private String title;
    private String description;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;

    private Integer userIdForEventFilter;

    @Schema(description = "Поле для сортировки", example = "startTime")
    private String sortBy = "title"; // Поле по умолчанию

    @Schema(description = "Направление сортировки", example = "DESC")
    private SortDirection sortOrder = SortDirection.DESC;

    private int page = 0;
    private int size = 10;
}
