package com.dev.LMS.model;

import jakarta.persistence.*;

@Entity(name ="StudentAnswer")
public class SubmittedQuestion {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long SubmittedQuestionId;

    @ManyToOne
    private Question question;
    @ManyToOne
    private Quiz quiz;
    @Column(name = "student_answer",nullable = false)
    private String studentAnswer;


}
