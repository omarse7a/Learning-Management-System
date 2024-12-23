package com.dev.LMS.model;

import jakarta.persistence.*;

@Entity(name ="StudentAnswer")
public class SubmittedQuestion {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long SubmittedQuestionId;

    @Column(name = "studentAnswer",nullable = false)
    private String studentAnswer;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private Question question;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private QuizSubmission submission;

    public long getSubmittedQuestionId() {
        return SubmittedQuestionId;
    }

    public void setSubmittedQuestionId(long submittedQuestionId) {
        SubmittedQuestionId = submittedQuestionId;
    }

    public String getStudentAnswer() {
        return this.studentAnswer;
    }

    public void setStudentAnswer(String studentAnswer) {
        this.studentAnswer = studentAnswer;
    }

    public Question getQuestion() {
        return this.question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public QuizSubmission getSubmission() {
        return this.submission;
    }

    public void setSubmission(QuizSubmission submission) {
        this.submission = submission;
    }
}
