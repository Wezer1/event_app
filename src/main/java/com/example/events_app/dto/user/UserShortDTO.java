package com.example.events_app.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Краткая информация о пользователе")
public class UserShortDTO {
    private Integer id;
    private String firstName;
    private String lastName;
}
