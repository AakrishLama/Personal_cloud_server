package com.backendCloud.Backend.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backendCloud.Backend.Model.FileDocument;
import com.backendCloud.Backend.Repository.FileDocumentRepository;

@Service
public class FileService {

    private final FileDocumentRepository fileRepo;
    private final ResourceLoader resourceLoader; // Inject ResourceLoader

    // Use constructor injection for both dependencies
    public FileService(FileDocumentRepository fileRepo, ResourceLoader resourceLoader) {
        this.fileRepo = fileRepo;
        this.resourceLoader = resourceLoader;
    }

    @Value("${file.upload-dir:uploads}") // default to "uploads" if not set
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

            // --- KEY CHANGE IS HERE ---
            // Get the resource directory and then build the full path
            Resource resource = resourceLoader.getResource("classpath:");
            Path rootPath = Paths.get(resource.getFile().getAbsolutePath());
            Path userUploadDir = rootPath.resolve(uploadDir).resolve(ownerId);
            Path fullFilePath = userUploadDir.resolve(file.getOriginalFilename());
            System.out.println("**resource**"+resource+ "** rootPath**"+rootPath
            + "** userUploadDir**"+userUploadDir+" ** fullFilePath**"+fullFilePath);
            // --- END KEY CHANGE ---

            // Create directories if they don't exist
            Files.createDirectories(userUploadDir);
            
            // Transfer file to destination
            file.transferTo(fullFilePath.toFile());

            // Create FileDocument and save to MongoDB
            FileDocument fileDoc = new FileDocument();
            fileDoc.setOwnerId(ownerId);
            fileDoc.setFilename(file.getOriginalFilename());
            fileDoc.setContentType(file.getContentType());
            fileDoc.setSize(file.getSize());
            fileDoc.setStoragePath(fullFilePath.toString()); // Store the absolute path
            
            return fileRepo.save(fileDoc);
            
        } catch (Exception e) {
            // Log the actual error for debugging
            System.err.println("Error storing file: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Could not store file. Error: " + e.getMessage(), e);
        }
    }
}