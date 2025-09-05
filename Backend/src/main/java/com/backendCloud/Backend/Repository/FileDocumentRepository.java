package com.backendCloud.Backend.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.backendCloud.Backend.Model.FileDocument;

public interface FileDocumentRepository extends MongoRepository<FileDocument, String>{
    List<FileDocument> findByOwnerId(String ownerId);
    List<FileDocument> findBySharedWithContaining(String userId);
}
