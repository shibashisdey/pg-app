package com.pgfinder.service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ImageUploadService {

    @Value("${app.upload.dir:uploads/}")
    private String uploadDir;
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${server.servlet.context-path:/api}")
    private String contextPath;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public String uploadImage(MultipartFile file, String subfolder) throws IOException {
        // Validate file
        validateImageFile(file);
        
        // Create directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir, subfolder);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + "." + extension;
        
        // Save file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Return public URL
        return generateImageUrl(subfolder, uniqueFilename);
    }

    public void deleteImage(String imageUrl) {
        try {
            // Extract relative path from URL
            String relativePath = extractRelativePathFromUrl(imageUrl);
            Path filePath = Paths.get(uploadDir, relativePath);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            // Log error but don't throw exception
            System.err.println("Failed to delete image: " + e.getMessage());
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 5MB");
        }
        
        String extension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Invalid file format. Allowed formats: " + String.join(", ", ALLOWED_EXTENSIONS));
        }
        
        // Check MIME type as additional security
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }
    }
    
    private String generateImageUrl(String subfolder, String filename) {
        return String.format("http://localhost:%s%s/images/%s/%s", 
                           serverPort, contextPath, subfolder, filename);
    }
    
    private String extractRelativePathFromUrl(String imageUrl) {
        // Extract the path after /images/
        int imagesIndex = imageUrl.indexOf("/images/");
        if (imagesIndex != -1) {
            return imageUrl.substring(imagesIndex + 8); // 8 is length of "/images/"
        }
        return "";
    }
    
    public boolean isValidImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return false;
        }
        
        try {
            String relativePath = extractRelativePathFromUrl(imageUrl);
            Path filePath = Paths.get(uploadDir, relativePath);
            return Files.exists(filePath);
        } catch (Exception e) {
            return false;
        }
    }
}
