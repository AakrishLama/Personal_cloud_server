package com.backendCloud.Backend.Service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backendCloud.Backend.Model.User;
import com.backendCloud.Backend.Repository.UserRepository;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    // add user with email and password.
    public User addUser(User newUser) {
        // TODO Auto-generated method stub
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getUsername().equals(newUser.getUsername())) {
                throw new RuntimeException("User already exists");
            }
        }
        return userRepository.save(newUser);
    }
    
}
