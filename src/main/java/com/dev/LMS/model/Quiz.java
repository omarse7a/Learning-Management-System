package com.dev.LMS.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JoinColumn(name= "course_id")
    private Course course;

    @Column(nullable = false, unique = true)
    private String quizTitle;

    @Column(nullable = false)
    private Time quizDuration;

    @OneToMany(mappedBy ="quiz", cascade = CascadeType.ALL)
    private List<QuizSubmission> submissions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<Question> questions = new ArrayList<>();

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public Long getQuizID() {
        return quizID;
    }

    public void setQuizID(Long quizID) {
        this.quizID = quizID;
    }

    public Course getCourseId() {
        return course;
    }

    public void setCourseId(Course courseId) {
        this.course = courseId;
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

    public List<QuizSubmission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<QuizSubmission> submissions) {
        this.submissions = submissions;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
