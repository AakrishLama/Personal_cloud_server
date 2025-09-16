package com.backendCloud.Backend.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.time.LocalDateTime;

@Document(collection = "chat_messages")
@Data
public class ChatMessage {
    @Id
    private String id;
    private String senderEmail;
    private String senderName;
    private String receiverEmail;
    private String receiverName;
    private String content;
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private String messageType; // "TEXT" or "FILE"
    private LocalDateTime timestamp; // Change to LocalDateTime
    private boolean delivered;
    private boolean read;

    public ChatMessage() {
        this.timestamp = LocalDateTime.now();
        this.delivered = false;
        this.read = false;
    }

    // Add constructor for text messages
    public ChatMessage(String senderEmail, String senderName, String receiverEmail, 
                     String receiverName, String content, String messageType) {
        this();
        this.senderEmail = senderEmail;
        this.senderName = senderName;
        this.receiverEmail = receiverEmail;
        this.receiverName = receiverName;
        this.content = content;
        this.messageType = messageType;
    }

    // Add constructor for file messages
    public ChatMessage(String senderEmail, String senderName, String receiverEmail,
                     String receiverName, String fileName, String fileUrl, 
                     Long fileSize, String content) {
        this();
        this.senderEmail = senderEmail;
        this.senderName = senderName;
        this.receiverEmail = receiverEmail;
        this.receiverName = receiverName;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.content = content;
        this.messageType = "FILE";
    }
}