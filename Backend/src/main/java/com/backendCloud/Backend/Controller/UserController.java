package com.backendCloud.Backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backendCloud.Backend.Model.User;
import com.backendCloud.Backend.Service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userService;

    // add user with email and password
    @PostMapping("/addUser")
    public User addUser(@RequestBody User newUser) {
        System.out.println("UserController: addUser called with user: " + newUser.getUsername());
        return userService.addUser(newUser);
    }
    
    
}
