package com.backendCloud.Backend.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.backendCloud.Backend.Model.ChatRoom;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    // find chatroom by both participant (order doesnt matter)
    Optional<ChatRoom> findByParticipant1AndParticipant2(String participant1, String participant2);
    Optional<ChatRoom> findByParticipant2AndParticipant1(String participant1, String participant2);

    List<ChatRoom> findByParticipant1OrParticipant2(String participant1, String participant2);

    // check if the chatroom exists by any order
    boolean existsByParticipant1AndParticipant2(String participant1, String participant2);
    boolean existsByParticipant2AndParticipant1(String participant1, String participant2);
}