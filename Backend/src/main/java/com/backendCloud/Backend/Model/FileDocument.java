package com.backendCloud.Backend.Model;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "files")
@Data
public class FileDocument {

    @Id
    private String id;
    private String ownerId;
    private String filename;
    private String contentType;
    private long size;
    private String storagePath;
    private String uploadDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    private boolean shared = false;
    private List<String> sharedWith; // list of userIds

}
