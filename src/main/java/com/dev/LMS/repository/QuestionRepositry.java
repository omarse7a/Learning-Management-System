package com.dev.LMS.repository;

import com.dev.LMS.model.Question;
import com.dev.LMS.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepositry extends JpaRepository<User, Integer> {
    Question findById(int QuesionId);
}
