package com.backendCloud.Backend.Model;

import java.io.File;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

// class to store the message type like file and test
@Data
@NoArgsConstructor
public class ChatMessage {

    private String content;
    private String sender;
    private String receiver;
    private String timestamp;
    private boolean read = false;

    // file document field
    private String fileId; // ← Use file ID instead of owner ID
    private String messageType; // e.g., "TEXT", "FILE"
    private String fileName;
    private String fileOwnerId; // The unique ID from the FileDocument
    private String pathOfFile;


}
