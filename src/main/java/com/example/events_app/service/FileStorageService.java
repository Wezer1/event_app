package com.example.events_app.service;

import com.example.events_app.config.FileStorageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path previewsLocation;
    private final Path imagesLocation;

    @Autowired
    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.previewsLocation = Paths.get(uploadDir, "previews").toAbsolutePath().normalize();
        this.imagesLocation = Paths.get(uploadDir, "images").toAbsolutePath().normalize();

        try {
            Files.createDirectories(previewsLocation);
            Files.createDirectories(imagesLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directories", e);
        }
    }

    public String storeFile(MultipartFile file) {
        // Убрали сохранение в превью по умолчанию
        throw new UnsupportedOperationException("Use storePreview() or storeAdditionalImage() explicitly");
    }

    public String storePreview(MultipartFile file) {
        return storeFile(file, this.previewsLocation);
    }

    public String storeAdditionalImage(MultipartFile file) {
        return storeFile(file, this.imagesLocation);
    }
    private String storeFile(MultipartFile file, Path location) {
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID() + extension;

        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file " + originalFilename);
            }
            if (originalFilename.contains("..")) {
                throw new RuntimeException("Cannot store file with relative path outside current directory " + originalFilename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, location.resolve(newFilename),
                        StandardCopyOption.REPLACE_EXISTING);
            }
            return newFilename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + originalFilename, e);
        }
    }

    public void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath).toAbsolutePath().normalize();
        if (!Files.exists(path)) {
            throw new FileNotFoundException("File not found: " + filePath);
        }
        Files.delete(path);
    }
}
