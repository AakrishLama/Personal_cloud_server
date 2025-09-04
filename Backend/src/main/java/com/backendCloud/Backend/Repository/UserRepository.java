package com.backendCloud.Backend.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.backendCloud.Backend.Model.User;

public interface UserRepository extends MongoRepository<User, String> {
    
    User findByUsername(String username);  //  custom method.
}
