package com.dev.LMS.repository;

import com.dev.LMS.model.Course;
import com.dev.LMS.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepositry extends JpaRepository<User, Integer> {
    Course findById(int id);
}
