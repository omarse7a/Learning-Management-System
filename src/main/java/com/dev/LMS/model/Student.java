package com.dev.LMS.model;


import jakarta.persistence.*;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="students")
public class Student extends User{
    //lesson
    @ManyToMany(mappedBy = "attendees")
    private Set<Lesson> lessonAttended = new HashSet<>();

    //courses
    @ManyToMany(mappedBy = "enrolled_students" )
    private Set<Course> enrolled_courses = new HashSet<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<AssignmentSubmisson> AssignmentSubmissions = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<QuizSubmission> quizSubmissions = new ArrayList<>();

    public Student() {}

    public Student(String name, String email) {
        super(name, email, Role.STUDENT);
    }

    public Set<Lesson> getLessonAttended() {
        return lessonAttended;
    }

    public void setLessonAttended(Set<Lesson> lessonAttended) {
        this.lessonAttended = lessonAttended;
    }

    public Set<Course> getEnrolled_courses() {
        return enrolled_courses;
    }

    public void setEnrolled_courses(Set<Course> enrolled_courses) {
        this.enrolled_courses = enrolled_courses;
    }

    public void attendLesson(Lesson lesson) {
        this.lessonAttended.add(lesson);
    }

    public void enrollCourse(Course course) {
        this.enrolled_courses.add(course);
    }

    public void unenrollCourse(Course course) {
        this.enrolled_courses.remove(course);
    }

    public List<AssignmentSubmisson> getAssignmentSubmissions() {
        return AssignmentSubmissions;
    }

    public void setAssignmentSubmissions(List<AssignmentSubmisson> submissions) {
        this.AssignmentSubmissions = submissions;
    }

    public void addSubmission(AssignmentSubmisson submission) {
        this.AssignmentSubmissions.add(submission);
        submission.setStudent(this);
    }

    public List<QuizSubmission> getQuizSubmissions() {
        return quizSubmissions;
    }

    public void setQuizSubmissions(List<QuizSubmission> quizSubmissions) {
        this.quizSubmissions = quizSubmissions;
    }
}
