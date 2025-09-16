package com.backendCloud.Backend.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.backendCloud.Backend.Model.ChatMessage;
import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    
    List<ChatMessage> findBySenderEmailAndReceiverEmail(String senderEmail, String receiverEmail);
    
    List<ChatMessage> findByReceiverEmail(String receiverEmail);
    
    List<ChatMessage> findBySenderEmailOrReceiverEmail(String email1, String email2);
    
    @Query("{ '$or': [ { 'senderEmail': ?0 }, { 'receiverEmail': ?0 } ] }")
    List<ChatMessage> findUserConversations(String userEmail);
    
    long countByReceiverEmailAndDeliveredFalse(String receiverEmail);
    
    long countByReceiverEmailAndReadFalse(String receiverEmail);
}