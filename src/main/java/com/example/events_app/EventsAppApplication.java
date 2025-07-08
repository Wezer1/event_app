package com.example.events_app;

import com.example.events_app.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EventsAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventsAppApplication.class, args);
    }


}
