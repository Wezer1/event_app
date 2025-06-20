package com.example.events_app.dto;

import com.example.events_app.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Пользователь")
public class UserDTO {

    private Integer id;

    private String firstName;

    private String lastName;

    private String patronymic;

    private String login;

    private Role role;
}
