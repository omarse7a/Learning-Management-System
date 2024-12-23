package com.dev.LMS.dto;

import com.dev.LMS.model.Course;
import com.dev.LMS.model.Question;
import com.dev.LMS.model.Quiz;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class QuizDto {
    private Long quizID;
    private String quizTitle;
    private Time quizDuration;
public static QuizDto toDto(Quiz quiz){
    return QuizDto.builder().
            quizID(quiz.getQuizID()).
            quizDuration(quiz.getQuizDuration()).
            quizTitle(quiz.getQuizTitle()).
            build();
}

}
