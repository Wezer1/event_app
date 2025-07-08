package com.example.events_app.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${upload.dir}")
    private String uploadDir;

    public String store(MultipartFile file, String prefix) {
        try {
            if (file.isEmpty()) {
                return null;
            }

            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String storedFilename = prefix + "_" + System.currentTimeMillis() + fileExtension;
            Path destinationFile = Paths.get(uploadDir).resolve(storedFilename).normalize();

            Files.createDirectories(destinationFile.getParent());
            file.transferTo(destinationFile);

            return "/uploads/previews/" + storedFilename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }
    }
}