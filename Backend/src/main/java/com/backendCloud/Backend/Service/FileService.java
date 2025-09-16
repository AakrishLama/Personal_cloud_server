package com.backendCloud.Backend.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backendCloud.Backend.Model.FileDocument;
import com.backendCloud.Backend.Repository.FileDocumentRepository;

@Service
public class FileService {

    private final FileDocumentRepository fileRepo;

    // Base upload directory - matches your Docker volume mount
    private final String baseUploadDir = "/app/uploads";

    public FileService(FileDocumentRepository fileRepo) {
        this.fileRepo = fileRepo;
    }

    public FileDocument storeFile(MultipartFile file, String ownerId) {
        try {
            // Validate inputs
            if (file.isEmpty()) {
                throw new RuntimeException("Cannot store empty file");
            }

            if (ownerId == null || ownerId.trim().isEmpty()) {
                throw new RuntimeException("Owner ID cannot be null or empty");
            }

            // Use the fixed upload directory (mapped to EBS volume via Docker)
            Path userUploadPath = Paths.get(baseUploadDir, ownerId);
            System.out.println("Upload path: " + userUploadPath);

            // Create directories if they don't exist
            Files.createDirectories(userUploadPath);

            // Create the complete file path
            Path fullFilePath = userUploadPath.resolve(file.getOriginalFilename());

            // Transfer file to destination
            file.transferTo(fullFilePath.toFile());

            // Create FileDocument and save to MongoDB
            FileDocument fileDoc = new FileDocument();
            fileDoc.setOwnerId(ownerId);
            fileDoc.setFilename(file.getOriginalFilename());
            fileDoc.setContentType(file.getContentType());
            fileDoc.setSize(file.getSize());
            fileDoc.setStoragePath(ownerId + "/" + file.getOriginalFilename()); // Relative path

            return fileRepo.save(fileDoc);

        } catch (Exception e) {
            System.err.println("Error storing file: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Could not store file. Error: " + e.getMessage(), e);
        }
    }

    // list of files for a specific user by their userid.
    public List<FileDocument> getFilesByOwner(String ownerId) {
        return fileRepo.findByOwnerId(ownerId);
    }

    public ResponseEntity<Resource> downloadFile(String fileId) {
        try {
            // Get the file document from database
            FileDocument fileDoc = fileRepo.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + fileId));
            
            String storagePath = fileDoc.getStoragePath();
            System.out.println("StoragePath from DB: " + storagePath);

            // Build the full path to the file
            Path filePath = Paths.get(baseUploadDir, storagePath);
            System.out.println("Full file path: " + filePath);

            // Check if file exists
            if (!Files.exists(filePath)) {
                throw new RuntimeException("File not found on disk: " + filePath);
            }

            // Create resource
            Resource resource = new FileSystemResource(filePath.toFile());
            
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + fileDoc.getFilename() + "\"")
                .body(resource);

        } catch (Exception e) {
            System.err.println("Error in downloadFile: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error downloading file: " + e.getMessage(), e);
        }
    }

    // get all the files from the database
    public ResponseEntity<List<FileDocument>> getAllFiles() {
        List<FileDocument> files = fileRepo.findAll();
        return ResponseEntity.ok(files);
    }

    // Delete file by file id
    public void deleteFile(String fileId) {
        try {
            // Get the file document from database
            Optional<FileDocument> fileDocOptional = fileRepo.findById(fileId);
            if (!fileDocOptional.isPresent()) {
                throw new RuntimeException("File not found with id: " + fileId);
            }

            FileDocument fileDoc = fileDocOptional.get();
            String storagePath = fileDoc.getStoragePath();
            
            // Build the full path to the file
            Path filePath = Paths.get(baseUploadDir, storagePath);
            System.out.println("Deleting file at path: " + filePath);

            // Delete the physical file
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("Physical file deleted: " + filePath);
            } else {
                System.out.println("Physical file not found, but proceeding with DB deletion: " + filePath);
            }

            // Delete the database record
            fileRepo.deleteById(fileId);
            System.out.println("Database record deleted for fileId: " + fileId);

        } catch (Exception e) {
            System.err.println("Error deleting file: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Could not delete file. Error: " + e.getMessage(), e);
        }
    }
}