package com.example.events_app.dto.user;

import com.example.events_app.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Пользователь")
public class UserDTO {

    private Integer id;

    private String fullName;

    private String login;

    private Role role;

    private Integer registeredEventsCount;

    private Integer totalBonusPoints;
}
