package com.example.events_app.dto;

import com.example.events_app.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRegistrationRequestDto {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String patronymic;

    @NotBlank
    private String login;

    @NotBlank
    private String password;

    private Role role;

}
