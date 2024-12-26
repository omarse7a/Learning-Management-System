package com.dev.LMS.repository;

import com.dev.LMS.model.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizSubmissionRepositry extends JpaRepository<QuizSubmission,Integer> {
    Optional<QuizSubmission> findById(Integer integer);
}
