package com.dev.LMS.dto;

import com.dev.LMS.model.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionDto {

    private Long id;

    private QuestionType type;

    private String content;

    private List<Choice> choices;

    private String correctAnswer;

    private Quiz quiz;

    private Course course;

    private List<QuizSubmission> submissions = new ArrayList<>();

    private List<SubmittedQuestion> submittedQuestions = new ArrayList<>();

    public static QuestionDto toDto(Question question){
        return QuestionDto.builder().
                choices(question.getChoices()).
                id(question.getId()).
                submittedQuestions(question.getSubmittedQuestions()).
                content(question.getContent()).
                type(question.getType()).
                correctAnswer(question.getCorrectAnswer()).
                quiz(question.getQuiz()).
                course(question.getCourse()).
                submissions(question.getSubmissions()).
                submittedQuestions(question.getSubmittedQuestions()).
                build();
    }

}
