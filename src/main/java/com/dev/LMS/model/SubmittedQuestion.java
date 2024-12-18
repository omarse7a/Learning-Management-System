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
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

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

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
