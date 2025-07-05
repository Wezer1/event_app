package com.example.events_app.dto.user;

import com.example.events_app.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRegistrationResponseDto {

    private Integer id;

    @NotBlank
    private String fullName;

    @NotBlank
    private String login;

    private Role role;
}
