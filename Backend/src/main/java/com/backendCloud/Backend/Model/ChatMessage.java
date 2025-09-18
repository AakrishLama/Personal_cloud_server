package com.backendCloud.Backend.Model;

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

}
