package com.dev.LMS.service;

import com.dev.LMS.dto.UpdateProfileDto;
import com.dev.LMS.model.User;
import com.dev.LMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.dev.LMS.util.JwtUtil;
import java.util.List;

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

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepo.save(user);
    }

    public String login(User user) {
        User existingUser = userRepo.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password."));

        if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            throw new RuntimeException("Invalid email or password.");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return token;
    }

    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public User updateUser(String email, UpdateProfileDto updateProfileDto) {
        User existingUser = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        existingUser.setName(updateProfileDto.getName());
        existingUser.setEmail(updateProfileDto.getEmail());
        existingUser.setPassword(passwordEncoder.encode(updateProfileDto.getPassword()));

        return userRepo.save(existingUser);
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User getUserById(int id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User createUser(User user) {
        return userRepo.save(user);
    }

    public void deleteUser(int id) {
        userRepo.deleteById(id);
    }
}