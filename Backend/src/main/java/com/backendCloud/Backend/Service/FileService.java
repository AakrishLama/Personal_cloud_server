package com.backendCloud.Backend.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

            // Create file path
            String filePath = uploadDir + "/" + ownerId + "/" + file.getOriginalFilename();
            Path path = Paths.get(filePath);
            
            // Create directories if they don't exist
            Files.createDirectories(path.getParent());
            
            // Transfer file to destination
            file.transferTo(path.toFile());

            // Create FileDocument and save to MongoDB
            FileDocument fileDoc = new FileDocument();
            fileDoc.setOwnerId(ownerId);
            fileDoc.setFilename(file.getOriginalFilename());
            fileDoc.setContentType(file.getContentType());
            fileDoc.setSize(file.getSize());
            fileDoc.setStoragePath(filePath);
            
            return fileRepo.save(fileDoc);
            
        } catch (Exception e) {
            // Log the actual error for debugging
            System.err.println("Error storing file: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Could not store file. Error: " + e.getMessage(), e);
        }
    }
}