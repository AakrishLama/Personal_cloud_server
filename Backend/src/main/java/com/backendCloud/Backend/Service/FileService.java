package com.backendCloud.Backend.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backendCloud.Backend.Model.FileDocument;
import com.backendCloud.Backend.Repository.FileDocumentRepository;

@Service
public class FileService {

    private final FileDocumentRepository fileRepo;

    public FileService(FileDocumentRepository fileRepo) {
        this.fileRepo = fileRepo;
    }

    @Value("${file.upload-dir}")
    private String uploadDir;

    public FileDocument storeFile(MultipartFile file, String ownerId) {
        try {
            // Validate inputs
            if (file.isEmpty()) {
                throw new RuntimeException("Cannot store empty file");
            }
            
            if (ownerId == null || ownerId.trim().isEmpty()) {
                throw new RuntimeException("Owner ID cannot be null or empty");
            }

            // --- KEY CHANGE: Navigate to project's src/main/resources ---
            // Get the current working directory (where the app is running from)
            String currentDir = System.getProperty("user.dir");
            System.out.println("Current working directory: " + currentDir);
            
            // If running from Backend/target/classes, navigate back to Backend/
            File runningDir = new File(currentDir);
            File backendDir;
            
            if (currentDir.endsWith("target/classes")) {
                // We're running from compiled location
                backendDir = runningDir.getParentFile().getParentFile(); // Goes to Backend/
            } else {
                // We're running from source or another location
                backendDir = runningDir;
            }
            
            // Build path to src/main/resources/uploads
            Path resourcesUploadPath = Paths.get(
                backendDir.getAbsolutePath(),
                "src", "main", "resources", uploadDir, ownerId
            );
            
            System.out.println("Final upload path: " + resourcesUploadPath);
            // --- END KEY CHANGE ---

            // Create directories if they don't exist
            Files.createDirectories(resourcesUploadPath);
            
            // Create the complete file path
            Path fullFilePath = resourcesUploadPath.resolve(file.getOriginalFilename());
            
            // Transfer file to destination
            file.transferTo(fullFilePath.toFile());

            // Create FileDocument and save to MongoDB
            FileDocument fileDoc = new FileDocument();
            fileDoc.setOwnerId(ownerId);
            fileDoc.setFilename(file.getOriginalFilename());
            fileDoc.setContentType(file.getContentType());
            fileDoc.setSize(file.getSize());
            fileDoc.setStoragePath(uploadDir + "/" + ownerId + "/" + file.getOriginalFilename());
            
            return fileRepo.save(fileDoc);
            
        } catch (Exception e) {
            System.err.println("Error storing file: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Could not store file. Error: " + e.getMessage(), e);
        }
    }

    public List<FileDocument> getFilesByOwner(String ownerId) {
        return fileRepo.findByOwnerId(ownerId);
    }
}