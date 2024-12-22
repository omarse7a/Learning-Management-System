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


    public static QuestionDto toDto(Question question){
        return QuestionDto.builder().
                choices(question.getChoices()).
                id(question.getId()).
                content(question.getContent()).
                type(question.getType()).
                correctAnswer(question.getCorrectAnswer()).
                build();
    }

}
