package com.backendCloud.Backend.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backendCloud.Backend.Model.User;
import com.backendCloud.Backend.Service.UserService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userService;

    // add user with email and password
    @PostMapping("/registerUser")
    public User Register(@RequestBody User newUser) {
        System.out.println("UserController: addUser called with user: " + newUser.getUsername());
        return userService.addUser(newUser);
    }

    // login user with email and password
    @PostMapping("/loginUser")
    public String loginUser(@RequestBody Map<String, String> body) {
        System.out.println("UserController: loginUser called with user: " + body.get("username"));
        String email = body.get("email");
        String password = body.get("password");

        boolean valid = userService.validateUser(email, password);
        if (valid) {
            return "Login successful";
        } else {
            return "Invalid username or password";
        }

    }

    // delete users
    @GetMapping("/deleteAllUsers")
    public String deleteUsers() {
        return userService.deleteAllUsers();
    }

    // get all users in the database
    @GetMapping("/getAllUsers")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    
}
