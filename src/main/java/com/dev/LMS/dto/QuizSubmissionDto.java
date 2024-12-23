package com.dev.LMS.dto;

import com.dev.LMS.model.Question;
import com.dev.LMS.model.Quiz;
import com.dev.LMS.model.QuizSubmission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class QuizSubmissionDto {
    private QuizDto quiz;
    private List<QuestionDto> questions;
    public static QuizSubmissionDto toDto(QuizSubmission quizSubmission){
        return builder().quiz(QuizDto.toDto(quizSubmission.getQuiz())).questions(QuestionDto.listToDto(quizSubmission.getQuestions())).build();
    }

}
