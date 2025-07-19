package com.example.events_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path originalsLocation;
    private final Path previewsLocation;
    private final Path additionalImagesLocation;

    @Value("${file.preview.max-width:512}")
    private int previewMaxWidth;

    @Value("${file.preview.quality:0.85}")
    private float previewQuality;

    @Autowired
    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) throws IOException {
        this.originalsLocation = Paths.get(uploadDir, "originals").toAbsolutePath().normalize();
        this.previewsLocation = Paths.get(uploadDir, "previews").toAbsolutePath().normalize();
        this.additionalImagesLocation = Paths.get(uploadDir, "images").toAbsolutePath().normalize();

        createDirectories();
    }

    private void createDirectories() throws IOException {
        Files.createDirectories(originalsLocation);
        Files.createDirectories(previewsLocation);
        Files.createDirectories(additionalImagesLocation);
    }

    public String storePreview(MultipartFile file) {
        try {
            // 1. Сохраняем оригинал
            String originalFilename = storeOriginalFile(file);

            // 2. Создаем и сохраняем превью
            String previewFilename = generatePreviewFilename(originalFilename);
            Path previewPath = previewsLocation.resolve(previewFilename);

            createResizedPreview(file.getInputStream(), previewPath);

            return previewFilename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store preview file", e);
        }
    }

    private String storeOriginalFile(MultipartFile file) throws IOException {
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID() + extension;

        Files.copy(file.getInputStream(), originalsLocation.resolve(newFilename),
                StandardCopyOption.REPLACE_EXISTING);

        return newFilename;
    }

    private String generatePreviewFilename(String originalFilename) {
        // Используем то же имя файла, но с расширением .jpg
        String baseName = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
        return baseName + ".jpg";
    }

    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath).toAbsolutePath().normalize();
            String filename = path.getFileName().toString();

            // Удаляем сам файл
            Files.deleteIfExists(path);

            // Определяем тип файла по пути и удаляем связанные файлы
            if (filePath.contains("/previews/")) {
                // Если удаляем превью - удаляем и оригинал
                String originalFilename = filename.substring(0, filename.lastIndexOf('.'));
                Path originalPath = originalsLocation.resolve(originalFilename);
                Files.deleteIfExists(originalPath);
            } else if (filePath.contains("/originals/")) {
                // Если удаляем оригинал - удаляем и превью
                String previewFilename = filename + ".jpg";
                Path previewPath = previewsLocation.resolve(previewFilename);
                Files.deleteIfExists(previewPath);
            }
            // Для additional images просто удаляем файл, так как у них нет превью
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + filePath, e);
        }
    }

    public String storeAdditionalImage(MultipartFile file) {
        try {
            return storeOriginalFile(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store additional image", e);
        }
    }

    private void createResizedPreview(InputStream inputStream, Path targetPath) throws IOException {
        BufferedImage originalImage = ImageIO.read(inputStream);
        if (originalImage == null) {
            throw new IOException("Unsupported image format");
        }

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        if (originalWidth <= previewMaxWidth) {
            // Если изображение меньше или равно - просто сохраняем как JPG
            saveAsJpg(originalImage, targetPath);
            return;
        }

        int newHeight = (originalHeight * previewMaxWidth) / originalWidth;

        // Создаем сжатое изображение
        BufferedImage resizedImage = new BufferedImage(previewMaxWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, previewMaxWidth, newHeight, null);
        g.dispose();

        // Сохраняем сжатое изображение
        saveWithCompression(resizedImage, targetPath);
    }

    private void saveAsJpg(BufferedImage image, Path targetPath) throws IOException {
        BufferedImage rgbImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );
        rgbImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
        saveWithCompression(rgbImage, targetPath);
    }

    private void saveWithCompression(BufferedImage image, Path targetPath) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IOException("No JPG writer found");
        }

        ImageWriter writer = writers.next();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(targetPath.toFile())) {
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(previewQuality);
            }

            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }
    }

    public Resource loadOriginal(String filename) {
        return loadResource(originalsLocation, filename);
    }

    public Resource loadPreview(String filename) {
        return loadResource(previewsLocation, filename);
    }

    public Resource loadAdditionalImage(String filename) {
        return loadResource(additionalImagesLocation, filename);
    }

    private Resource loadResource(Path location, String filename) {
        try {
            Path file = location.resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            throw new RuntimeException("Could not read file: " + filename);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }
}
