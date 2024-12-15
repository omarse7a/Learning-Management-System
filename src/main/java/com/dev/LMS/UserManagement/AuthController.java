package com.dev.LMS.UserManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        try {
            if (user.getEmail() == null || user.getPassword() == null || user.getId() == null) {
                response.put("message", "Email and password are required.");
                return ResponseEntity.badRequest().body(response);
            }
            if (user.getPassword().length() < 8) {
                response.put("message", "Password must be at least 8 characters long.");
                return ResponseEntity.badRequest().body(response);
            }
            userService.register(user);
            response.put("message", "User registered successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
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
