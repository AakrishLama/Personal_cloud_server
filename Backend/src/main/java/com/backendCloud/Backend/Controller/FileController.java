package com.backendCloud.Backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backendCloud.Backend.Model.FileDocument;
import com.backendCloud.Backend.Service.FileService;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
            @RequestParam("ownerId") String ownerId) {
        try {
            // Basic validation
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }
            
            if (ownerId == null || ownerId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Owner ID is required");
            }

            System.out.println("Uploading file: " + file.getOriginalFilename() + " for owner: " + ownerId);
            
            FileDocument savedFile = fileService.storeFile(file, ownerId);
            return ResponseEntity.ok(savedFile);
            
        } catch (Exception e) {
            System.err.println("Error in uploadFile: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading file: " + e.getMessage());
        }
    }
}