package com.backendCloud.Backend.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators.Convert;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.backendCloud.Backend.Model.ChatMessage;
import com.backendCloud.Backend.Model.ChatRoom;
import com.backendCloud.Backend.Repository.ChatRoomRepository;

@RestController
public class ChatRoomController {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd
    // HH:mm:ss"));
    public String getNowDateAndTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // get or create a chat room between two users
    @GetMapping("/room/{user1}/{user2}")
    public ResponseEntity<ChatRoom> getOrCreateChatRoom(@PathVariable String user1, @PathVariable String user2) {
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByParticipant1AndParticipant2(user1, user2);
        if (!existingRoom.isPresent()) {
            existingRoom = chatRoomRepository.findByParticipant2AndParticipant1(user1, user2);
        }
        if (existingRoom.isPresent()) {
            return ResponseEntity.ok(existingRoom.get());
        }
        // create a new chat room for users
        ChatRoom newRoom = new ChatRoom();
        newRoom.setParticipant1(user1);
        newRoom.setParticipant2(user2);
        newRoom.setCreatedAt(getNowDateAndTime());
        newRoom.setUpdatedAt(getNowDateAndTime());
        ChatRoom savedRoom = chatRoomRepository.save(newRoom);
        return ResponseEntity.ok(savedRoom);
    }

    // websocket message handling
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        // determine the sender and reciever from the payload
        String sender = chatMessage.getSender();
        String receiver = chatMessage.getReceiver();
        // get the chat room between the sender and receiver
        Optional<ChatRoom> chatRoom = chatRoomRepository.findByParticipant1AndParticipant2(sender, receiver);
        if (!chatRoom.isPresent()) {
            chatRoom = chatRoomRepository.findByParticipant2AndParticipant1(sender, receiver);
        }
        ChatRoom room;
        if (chatRoom.isPresent()) {
            room = chatRoom.get();
        } else {
            room = new ChatRoom();
            room.setParticipant1(sender);
            room.setParticipant2(receiver);
            room.setCreatedAt(getNowDateAndTime());
            room.setUpdatedAt(getNowDateAndTime());
        }

        // add the messages to the chatroom
        chatMessage.setTimestamp(getNowDateAndTime());
        room.getMessages().add(chatMessage);
        chatRoomRepository.save(room);

        // send the message to the both participants
        messagingTemplate.convertAndSendToUser(receiver,
         "/queue/messages",
          chatMessage);
        messagingTemplate.convertAndSendToUser(sender,
         "/queue/messages",
          chatMessage);
    }

    // get chat history 
    @GetMapping("/history/{user1}/{user2}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable String user1, @PathVariable String user2) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findByParticipant1AndParticipant2(user1, user2);
        if(!chatRoom.isPresent()){
            chatRoom = chatRoomRepository.findByParticipant2AndParticipant1(user1, user2);
        }
        if(chatRoom.isPresent()){
            return ResponseEntity.ok(chatRoom.get().getMessages());
        }
        return ResponseEntity.ok(null);
    }

}