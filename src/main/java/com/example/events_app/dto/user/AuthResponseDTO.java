package com.example.events_app.dto.user;

import com.example.events_app.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private Integer id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String patronymic;

    @NotBlank
    private String login;

    private Role role;

    private String token;

    public AuthResponseDTO(Integer id, String firstName, String lastName, String login, Role role, String token) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.role = role;
        this.token = token;
    }
}
