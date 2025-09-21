package com.backendCloud.Backend.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.backendCloud.Backend.Model.FileDocument;

public interface FileDocumentRepository extends MongoRepository<FileDocument, String>{
    List<FileDocument> findByOwnerId(String ownerId);
    List<FileDocument> findBySharedWithContaining(String userId);
    // find by file id 

    @Query("{'_id': ?0}")
    Optional<FileDocument> fileDocumentById(String id);
}
