package com.dev.LMS.util;


import com.dev.LMS.model.User;
import org.springframework.stereotype.Component;
import com.dev.LMS.model.Role;
import com.dev.LMS.model.Admin;
import com.dev.LMS.model.Instructor;
import com.dev.LMS.model.Student;
@Component
public class UserFactory {
    public User createUser(String role, String name, String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        Role userRole;
        try{
            userRole = Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role. Must be one of: ADMIN, INSTRUCTOR, STUDENT");
        }
        return switch (userRole) {
            case STUDENT -> new Student(name, email);
            case INSTRUCTOR -> new Instructor(name, email);
            case ADMIN -> new Admin(name, email);
            default -> throw new RuntimeException("Unsupported role.");
        };
    }

    public User tempLoginUser(String role, String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        Role userRole;
        try{
            userRole = Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role. Must be one of: ADMIN, INSTRUCTOR, STUDENT");
        }
        return switch (userRole) {
            case STUDENT -> new Student("temp", email);
            case INSTRUCTOR -> new Instructor("temp", email);
            case ADMIN -> new Admin("temp", email);
            default -> throw new RuntimeException("Unsupported role.");
        };
    }
}
