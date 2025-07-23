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
@Schema(description = "Фильтр для поиска событий")
public class EventFilterDTO {

    @Schema(description = "Ключевое слово для поиска в title, description, location и названии организации")
    private String keyword;

    @Schema(description = "ID типа мероприятия")
    private Integer eventTypeId;


    @Schema(description = "Дата начала события (от)")
    private LocalDateTime startDateFrom;

    @Schema(description = "Дата начала события (до)")
    private LocalDateTime startDateTo;

    @Schema(description = "Поле для сортировки", example = "startTime")
    private String sortBy = "title"; // Поле по умолчанию

    @Schema(description = "Направление сортировки", example = "DESC")
    private SortDirection sortOrder = SortDirection.DESC; // Направление по умолчанию

    @Schema(description = "Номер страницы", example = "0")
    private int page = 0;

    @Schema(description = "Размер страницы", example = "50")
    private int size = 50;

    private Integer userId;

    @Schema(description = "Дата окончания события (от)")
    private LocalDateTime endDateFrom;

    @Schema(description = "Дата окончания события (до)")
    private LocalDateTime endDateTo;
    // Геттеры и сеттеры
}
