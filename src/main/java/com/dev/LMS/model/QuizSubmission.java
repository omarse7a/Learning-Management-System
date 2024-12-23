package com.dev.LMS.model;

import com.dev.LMS.dto.QuestionDto;
import jakarta.persistence.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Entity
public class QuizSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer submission_id;

    private int grade;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Student student;
    @OneToMany(mappedBy = "submission", cascade = CascadeType.PERSIST)
    private List<SubmittedQuestion> submittedQuestions = new ArrayList<>();
    @ManyToMany(mappedBy = "submissions", cascade = CascadeType.PERSIST)
    private List<Question> questions = new ArrayList<>();

    public QuizSubmission() {
    }
    public Integer getSubmission_id() {
        return this.submission_id;
    }

    public void setSubmission_id(Integer submission_id) {
        this.submission_id = submission_id;
    }

    public Student getStudent() {
        return this.student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Quiz getQuiz() {
        return this.quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public List<Question> getQuestions() {return this.questions;}

    public int getGrade() {
        return this.grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<SubmittedQuestion> getSubmittedQuestions() {
        return this.submittedQuestions;
    }

    public void setSubmittedQuestions(List<SubmittedQuestion> submittedQuestions) {
        this.submittedQuestions = submittedQuestions;
    }
}