package com.example.events_app.dto.user;

import com.example.events_app.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserUpdateRequestDTO {
    private String fullName;

    private String login;

    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    private Role role;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    @Email(message = "Email should be valid")
    private String email;
}
