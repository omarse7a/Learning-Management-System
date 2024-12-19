package com.dev.LMS.model;

import jakarta.persistence.*;

@Entity(name ="StudentAnswer")
public class SubmittedQuestion {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long SubmittedQuestionId;

    @Column(name = "student_answer",nullable = false)
    private String studentAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    private QuizSubmission submission;

    public long getSubmittedQuestionId() {
        return SubmittedQuestionId;
    }

    public void setSubmittedQuestionId(long submittedQuestionId) {
        SubmittedQuestionId = submittedQuestionId;
    }

    public String getStudentAnswer() {
        return studentAnswer;
    }

    public void setStudentAnswer(String studentAnswer) {
        this.studentAnswer = studentAnswer;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public QuizSubmission getSubmission() {
        return submission;
    }

    public void setSubmission(QuizSubmission submission) {
        this.submission = submission;
    }
}
