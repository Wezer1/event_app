package com.example.events_app.exception_handling;


import com.example.events_app.exceptions.AlreadyExistsException;
import com.example.events_app.exceptions.IsNotAvailableException;
import com.example.events_app.exceptions.NoSuchException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Component
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @PostConstruct
    public void init() {
        log.info("⚡️ GlobalExceptionHandler загружен!");
    }

    // ========== Существующие исключения ==========

    @ExceptionHandler(NoSuchException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchException(
            NoSuchException exception,
            WebRequest request) {
        log.error("Handled NoSuchException: {}", exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                exception.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(IsNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleIsNotAvailableException(
            IsNotAvailableException exception,
            WebRequest request) {
        log.error("Handled IsNotAvailableException: {}", exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.IM_USED.value(),
                "Already Used",
                exception.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.IM_USED).body(errorResponse);
    }

    // ========== Новое исключение ==========

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistsException(
            AlreadyExistsException exception,
            WebRequest request) {
        log.error("Handled AlreadyExistsException: {}", exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Conflict",
                exception.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}
