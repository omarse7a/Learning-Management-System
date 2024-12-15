package com.dev.LMS.service;

import com.dev.LMS.model.User;
import com.dev.LMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.dev.LMS.util.JwtUtil;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public void register(User user) {
        if (!userRepo.findByEmail(user.getEmail()).isEmpty()) {
            throw new RuntimeException("User with this email already exists.");
        }

        if (!userRepo.findById(user.getId()).isEmpty()) {
            throw new RuntimeException("User with this id already exists.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepo.save(user);
    }

    public String login(User user) {
        User existingUser = userRepo.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password."));

        if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            throw new RuntimeException("Invalid email or password.");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return token;
    }

    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public User updateUser(String email, User user) {
        User existingUser = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepo.save(existingUser);
    }
}