package com.backendCloud.Backend.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backendCloud.Backend.Model.User;
import com.backendCloud.Backend.Repository.UserRepository;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Add user with email and password.
    public User addUser(User newUser) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getUsername().equals(newUser.getUsername())) {
                throw new RuntimeException("User already exists");
            }
        }
        // encode bcypt password
        User user = new User();
        user.setUsername(newUser.getUsername());
        user.setEmail(newUser.getEmail());
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return userRepository.save(user);
    }

    // login user with email and password
    public boolean validateUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return false;
        }
        return passwordEncoder.matches(password, user.getPassword());
    }

    public String deleteAllUsers() {
        // TODO Auto-generated method stub
        if (userRepository.count() == 0) {
            return "No users to delete";
        }
        userRepository.deleteAll();
        return "All users deleted";
    }

    public List<User> getAllUsers() {
        // TODO Auto-generated method stub
        List<User> users = userRepository.findAll();
        return users;
    }
}
