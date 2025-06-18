package com.example.events_app.exceptions;

public class IsNotAvailableException extends RuntimeException{
    public IsNotAvailableException(String message) {
        super(message);
    }
}