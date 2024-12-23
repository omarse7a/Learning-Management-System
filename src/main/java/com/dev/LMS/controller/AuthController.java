package com.dev.LMS.controller;

import com.dev.LMS.dto.UserLoginDto;
import com.dev.LMS.model.User;
import com.dev.LMS.service.UserService;
import com.dev.LMS.util.UserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.dev.LMS.dto.RegisterDto;

import java.util.HashMap;
import java.util.Map;
import jakarta.validation.Valid;

@RestController
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterDto registerDto) {
        Map<String, String> response = new HashMap<>();
        try {
            User user = userFactory.createUser(registerDto.getRole(), registerDto.getName(), registerDto.getEmail());
            user.setPassword(registerDto.getPassword());


            userService.register(user);
            response.put("message", "User registered successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserLoginDto userdto) {
        Map<String, String> response = new HashMap<>();
        User user = userFactory.tempLoginUser(userdto.getRole(), userdto.getEmail());
        if (user != null) {
            user.setPassword(userdto.getPassword());
        } else {
            response.put("message", "Invalid login credentials.");
            return ResponseEntity.badRequest().body(response);
        }
        try {
            if (user.getEmail() == null || user.getPassword() == null) {
                response.put("message", "Email and password are required.");
                return ResponseEntity.badRequest().body(response);
            }
            String token = userService.login(user);
            response.put("message", "Login successful");
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
