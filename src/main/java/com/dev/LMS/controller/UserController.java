package com.dev.LMS.controller;

import com.dev.LMS.dto.NotificationDto;
import com.dev.LMS.dto.RegisterDto;
import com.dev.LMS.dto.UpdateProfileDto;
import com.dev.LMS.dto.UserLoginDto;
import com.dev.LMS.model.Instructor;
import com.dev.LMS.model.Student;
import com.dev.LMS.model.User;
import com.dev.LMS.service.NotificationService;
import com.dev.LMS.service.UserService;
import com.dev.LMS.util.UserFactory;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateUserProfile(@RequestBody UpdateProfileDto updateProfileDto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User updatedUser = userService.updateUser(email, updateProfileDto);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users/create")
    public ResponseEntity<Map<String, String>> createUser(@Valid @RequestBody RegisterDto registerDto) {
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

    @DeleteMapping("/users/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/notifications")
    public ResponseEntity<?> getAllNotifications() {
        try{
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }
            if (user instanceof Instructor){
                Instructor instructor = (Instructor) user;
                List<NotificationDto> notifications = notificationService.getInstructorNotification(instructor);
                return ResponseEntity.ok(notifications);
            }
            if (user instanceof Student){
                Student student = (Student) user;
                List<NotificationDto> notifications = notificationService.getStudentNotification(student);
                return ResponseEntity.ok(notifications);
            }
            else
                return ResponseEntity.badRequest().body("User not found");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
