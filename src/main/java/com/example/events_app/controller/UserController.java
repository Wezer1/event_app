package com.example.events_app.controller;

import com.example.events_app.dto.user.UserRegistrationRequestDto;
import com.example.events_app.dto.user.UserRegistrationResponseDto;
import com.example.events_app.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Работа с пользователями")
public class UserController {

    private final UserService userService;

    @PostMapping("/registration")
    @Operation(summary = "Регистрация пользователя", description = "Создаёт нового пользователя")
    @ApiResponse(responseCode = "200", description = "User created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserRegistrationResponseDto.class)))
    public ResponseEntity<UserRegistrationResponseDto> registrationUser(@RequestBody UserRegistrationRequestDto userRegistrationRequestDto){
        return ResponseEntity.ok(userService.saveUser(userRegistrationRequestDto));
    }

    @PostMapping("/")
    @PreAuthorize("hasAuthority('users:write')")
    @Operation(summary = "Добавить пользователя", description = "Создаёт пользователя с правами администратора")
    @ApiResponse(responseCode = "200", description = "User created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserRegistrationResponseDto.class)))
    public ResponseEntity<UserRegistrationResponseDto> addUser(@RequestBody UserRegistrationRequestDto userRegistrationRequestDto){
        return ResponseEntity.ok(userService.saveUser(userRegistrationRequestDto));
    }

    @GetMapping("/")
    @PreAuthorize("hasAuthority('users:write')")
    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserRegistrationResponseDto.class)))
    public ResponseEntity<List<UserRegistrationResponseDto>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('users:write')")
    @Operation(summary = "Получить пользователя по ID", description = "Возвращает пользователя по его ID")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserRegistrationResponseDto.class)))
    public ResponseEntity<UserRegistrationResponseDto> getUserById(@PathVariable Integer userId){
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PostMapping("/{userId}")
    @PreAuthorize("hasAuthority('users:write')")
    @Operation(summary = "Обновить пользователя", description = "Обновляет данные пользователя по ID")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserRegistrationResponseDto.class)))
    public ResponseEntity<UserRegistrationResponseDto> changeUser(
            @PathVariable Integer userId,
            @RequestBody UserRegistrationRequestDto userRegistrationRequestDto){
        return ResponseEntity.ok(userService.changeUser(userId, userRegistrationRequestDto));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('users:write')")
    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя по ID")
    @ApiResponse(responseCode = "204", description = "No Content")
    public ResponseEntity<UserRegistrationResponseDto> deleteUser(@PathVariable Integer userId){
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}