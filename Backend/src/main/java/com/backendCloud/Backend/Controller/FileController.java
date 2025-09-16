package com.backendCloud.Backend.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backendCloud.Backend.Model.FileDocument;
import com.backendCloud.Backend.Service.FileService;

@RestController
@CrossOrigin(origins = "*")
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

    // get the list of files for a specific user
    @GetMapping("/my-files/{ownerId}")
    public ResponseEntity<List<FileDocument>> getFilesByOwner(@PathVariable String ownerId) {
        List<FileDocument> files = fileService.getFilesByOwner(ownerId);
        return ResponseEntity.ok(files);
    }

    // get all the files from the database
    @GetMapping("/all-files")
    public ResponseEntity<List<FileDocument>> getAllFiles() {
        return fileService.getAllFiles();
    }

    // Download file by their file id
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId){
        try {
            System.out.println("ran the downloadFile in controller");
            return fileService.downloadFile(fileId);
        } catch (Exception e) {
            System.err.println("Error in downloadFile as a catch in controller: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Delete file by file id
    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileId) {
        try {
            fileService.deleteFile(fileId);
            return ResponseEntity.ok().body("File deleted successfully");
        } catch (Exception e) {
            System.err.println("Error in deleteFile: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting file: " + e.getMessage());
        }
    }
}