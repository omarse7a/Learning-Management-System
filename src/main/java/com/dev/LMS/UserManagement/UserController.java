package com.dev.LMS.UserManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.dev.LMS.util.JwtUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String token) {
        if (!jwtUtil.checkToken(token)) {
            return ResponseEntity.status(401).build();
        }
        String tokenString = jwtUtil.extractToken(token);
        String email = jwtUtil.extractEmail(tokenString);
        User user = userService.getUserByEmail(email);

        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateUserProfile(@RequestHeader("Authorization") String token, @RequestBody User user) {
        if (!jwtUtil.checkToken(token)) {
            return ResponseEntity.status(401).build();
        }
        String tokenString = jwtUtil.extractToken(token);
        String email = jwtUtil.extractEmail(tokenString);
        User updatedUser = userService.updateUser(email, user);
        return ResponseEntity.ok(updatedUser);
    }
}
