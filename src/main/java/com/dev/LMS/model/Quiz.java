package com.dev.LMS.model;

import jakarta.persistence.*;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
 @Table(name="quiz")
public class Quiz {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long quizID;
    @ManyToOne
    @JoinColumn(name= "course")
    private Course courseId;
    @ManyToOne
    @JoinColumn(name= "user")
    private User userId;
    @Column(nullable = false, unique = true)
    private String quizTitle;
    @Column(nullable = false)
    private Time quizDuration;
    @OneToMany(mappedBy ="quiz" )
    private List<Question> questions = new ArrayList<>();
    @OneToMany(mappedBy ="questions_answers" )
    private List<SubmittedQuestion> submittedQuestions = new ArrayList<>();

    public List<SubmittedQuestion> getSubmittedQuestions() {
        return submittedQuestions;
    }

    public void setSubmittedQuestions(List<SubmittedQuestion> submittedQuestions) {
        this.submittedQuestions = submittedQuestions;
    }

    public Long getQuizID() {
        return quizID;
    }

    public void setQuizID(Long quizID) {
        this.quizID = quizID;
    }

    public Course getCourseId() {
        return courseId;
    }

    public void setCourseId(Course courseId) {
        this.courseId = courseId;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public Time getQuizDuration() {
        return quizDuration;
    }

    public void setQuizDuration(Time quizDuration) {
        this.quizDuration = quizDuration;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
