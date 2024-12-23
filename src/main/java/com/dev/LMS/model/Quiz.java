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
    @ManyToOne( cascade = CascadeType.PERSIST)
    @JoinColumn(name= "course_id")
    private Course course;

    @Column(nullable = false, unique = true)
    private String quizTitle;

//    @Column(nullable = false)
    private Time quizDuration;

    @OneToMany(mappedBy ="quiz", cascade = CascadeType.PERSIST)
    private List<QuizSubmission> submissions;
    public void addQuizSubmission(QuizSubmission quizSubmission){
        this.submissions.add(quizSubmission);
        quizSubmission.setQuiz(this);
    }

    public Long getQuizID() {
        return this.quizID;
    }

    public void setQuizID(Long quizID) {
        this.quizID = quizID;
    }

    public Course getCourseId() {
        return this.course;
    }

    public void setCourseId(Course courseId) {
        this.course = courseId;
    }

    public String getQuizTitle() {
        return this.quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public Time getQuizDuration() {
        return this.quizDuration;
    }

    public void setQuizDuration(Time quizDuration) {
        this.quizDuration = quizDuration;
    }

    public List<QuizSubmission> getSubmissions() {
        return this.submissions;
    }

    public void setSubmissions(List<QuizSubmission> submissions) {
        this.submissions = submissions;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
    public QuizSubmission findbyStudent(Student student){
        for (int i = 0; i < this.getSubmissions().size(); i++){
            if((this.getSubmissions().get(i).getStudent()).equals(student))
                return this.getSubmissions().get(i);
        }
        return null;
    }
}
