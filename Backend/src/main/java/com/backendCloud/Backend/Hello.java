package com.backendCloud.Backend;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class Hello {
    @RequestMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
    
}
