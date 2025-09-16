package com.backendCloud.Backend.Controller;

// All imports should be at the top of the file
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;

import com.backendCloud.Backend.Model.ChatMessage;
import com.backendCloud.Backend.Repository.ChatMessageRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Controller
@CrossOrigin(origins = "*")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final String FILE_UPLOAD_DIR = "/app/chat-files/";

    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate, 
                         ChatMessageRepository chatMessageRepository) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageRepository = chatMessageRepository;
    }

    @MessageMapping("/chat.privateMessage")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage) {
        // Set timestamp properly
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setDelivered(false);
        chatMessage.setRead(false);
        
        // Save to MongoDB
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
        
        // Send to receiver via WebSocket
        String destination = "/queue/" + chatMessage.getReceiverEmail();
        messagingTemplate.convertAndSend(destination, savedMessage);
    }

    @PostMapping("/api/chat/uploadFile")
    public ResponseEntity<ChatMessage> uploadChatFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("senderEmail") String senderEmail,
            @RequestParam("senderName") String senderName,
            @RequestParam("receiverEmail") String receiverEmail,
            @RequestParam("receiverName") String receiverName) {
        
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(FILE_UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null && originalFilename.lastIndexOf(".") != -1 ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFilename);

            // Save file to EBS volume
            file.transferTo(filePath.toFile());

            // Create and save chat message to MongoDB
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSenderEmail(senderEmail);
            chatMessage.setSenderName(senderName);
            chatMessage.setReceiverEmail(receiverEmail);
            chatMessage.setReceiverName(receiverName);
            chatMessage.setFileName(originalFilename);
            chatMessage.setFileUrl("/api/chat/files/" + uniqueFilename);
            chatMessage.setFileSize(file.getSize());
            chatMessage.setMessageType("FILE");
            chatMessage.setContent("Shared a file: " + originalFilename);
            chatMessage.setTimestamp(LocalDateTime.now());
            chatMessage.setDelivered(false);
            chatMessage.setRead(false);

            ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

            // Send to receiver
            messagingTemplate.convertAndSend("/queue/" + receiverEmail, savedMessage);

            return ResponseEntity.ok(savedMessage);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get chat history between two users
    @GetMapping("/api/chat/history")
    public ResponseEntity<List<ChatMessage>> getChatHistory(
            @RequestParam String user1Email,
            @RequestParam String user2Email) {
        
        List<ChatMessage> messages1 = chatMessageRepository.findBySenderEmailAndReceiverEmail(user1Email, user2Email);
        List<ChatMessage> messages2 = chatMessageRepository.findBySenderEmailAndReceiverEmail(user2Email, user1Email);
        
        messages1.addAll(messages2);
        
        // Sort by timestamp
        messages1.sort(Comparator.comparing(ChatMessage::getTimestamp));
        
        return ResponseEntity.ok(messages1);
    }

    @GetMapping("/api/chat/files/{filename}")
    public ResponseEntity<Resource> getChatFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(FILE_UPLOAD_DIR).resolve(filename);
            Resource resource = new FileSystemResource(filePath.toFile());
            
            if (resource.exists()) {
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Implement markDelivered and markRead methods properly
    @PostMapping("/api/chat/markDelivered")
    public ResponseEntity<Void> markMessagesAsDelivered(@RequestParam String messageId) {
        chatMessageRepository.findById(messageId).ifPresent(message -> {
            message.setDelivered(true);
            chatMessageRepository.save(message);
        });
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/chat/markRead")
    public ResponseEntity<Void> markMessagesAsRead(@RequestParam String messageId) {
        chatMessageRepository.findById(messageId).ifPresent(message -> {
            message.setRead(true);
            chatMessageRepository.save(message);
        });
        return ResponseEntity.ok().build();
    }
}