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
import java.nio.file.*;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path originalsLocation;
    private final Path previewsLocation;
    private final Path additionalImagesLocation;
    private final Path uploadRootDir;  // Добавьте это поле


    @Value("${file.preview.max-width:512}")
    private int previewMaxWidth;

    @Value("${file.preview.quality:0.85}")
    private float previewQuality;

    public Path getUploadRootDir() {
        return uploadRootDir;
    }

    @Autowired
    public FileStorageService(@Value("${spring.web.resources.static-locations[0]}") String uploadBaseDir)
            throws IOException {
        // Удаляем префикс "file:" если есть
        String cleanPath = uploadBaseDir.replace("file:", "");
        this.uploadRootDir = Paths.get(cleanPath).toAbsolutePath().normalize();

        this.originalsLocation = uploadRootDir.resolve("originals");
        this.previewsLocation = uploadRootDir.resolve("previews");
        this.additionalImagesLocation = uploadRootDir.resolve("images");

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

    private String getOriginalExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    public void deleteFile(String filePath) {
        try {
            // Определяем тип файла по пути
            if (filePath.contains("/previews/")) {
                // Удаляем превью и оригинал
                String previewFilename = Paths.get(filePath).getFileName().toString();
                String originalFilename = previewFilename.replace(".jpg", "");

                // Удаляем превью
                Path previewPath = previewsLocation.resolve(previewFilename);
                Files.deleteIfExists(previewPath);

                // Удаляем оригинал (ищем с оригинальным расширением)
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(originalsLocation,
                        originalFilename + ".*")) {
                    for (Path originalPath : stream) {
                        Files.deleteIfExists(originalPath);
                    }
                }

            } else if (filePath.contains("/originals/")) {
                // Удаляем оригинал и превью
                String originalFilename = Paths.get(filePath).getFileName().toString();
                String previewFilename = originalFilename.substring(0, originalFilename.lastIndexOf('.')) + ".jpg";

                // Удаляем оригинал
                Path originalPath = originalsLocation.resolve(originalFilename);
                Files.deleteIfExists(originalPath);

                // Удаляем превью
                Path previewPath = previewsLocation.resolve(previewFilename);
                Files.deleteIfExists(previewPath);

            } else if (filePath.contains("/images/")) {
                // Удаляем только дополнительное изображение
                Path imagePath = additionalImagesLocation.resolve(Paths.get(filePath).getFileName());
                Files.deleteIfExists(imagePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + filePath, e);
        }
    }

    public String storeAdditionalImage(MultipartFile file) {
        try {
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID() + extension;

            // Сохраняем напрямую в папку для дополнительных изображений (images)
            Files.copy(file.getInputStream(), additionalImagesLocation.resolve(newFilename),
                    StandardCopyOption.REPLACE_EXISTING);

            return newFilename;
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
