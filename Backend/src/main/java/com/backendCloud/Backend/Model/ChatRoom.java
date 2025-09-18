package com.backendCloud.Backend.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.cglib.core.Local;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ChatRoom")
public class ChatRoom {

    public String id;
    public String participant1;
    public String participant2;
    public String createdAt;
    public String updatedAt;
    public List<ChatMessage> messages = new ArrayList<>();
    

}