package com.example.events_app.exceptions;


public class NoSuchException extends RuntimeException{
    public NoSuchException(String message) {
        super(message);
    }
}