package com.example.events_app.dto.user;

import com.example.events_app.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class AuthResponseDTO {
    private Integer id;

    @NotBlank
    private String fullName;

    @NotBlank
    private String login;

    private Role role;

    private String phoneNumber;

    private String email;

    private String token;

    public AuthResponseDTO(Integer id, String fullName, String login, Role role, String token, String email, String phoneNumber) {
        this.id = id;
        this.fullName = fullName;
        this.login = login;
        this.role = role;
        this.token = token;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
