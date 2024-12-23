package com.dev.LMS.model;

import jakarta.persistence.*;

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


    @ManyToMany(mappedBy = "enrolled_students",cascade = CascadeType.PERSIST)

    private Set<Course> enrolled_courses = new HashSet<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<AssignmentSubmission> AssignmentSubmissions = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.PERSIST)
    private List<QuizSubmission> quizSubmissions = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();

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

    public List<AssignmentSubmission> getAssignmentSubmissions() {
        return AssignmentSubmissions;
    }

    public void setAssignmentSubmissions(List<AssignmentSubmission> submissions) {
        this.AssignmentSubmissions = submissions;
    }

    public void addAssignmentSubmission(AssignmentSubmission submission) {

        this.AssignmentSubmissions.add(submission);
        submission.setStudent(this);
    }
    public void addQuizSubmission(QuizSubmission submission) {
        this.quizSubmissions.add(submission);
    }


    public List<QuizSubmission> getQuizSubmissions() {
        return quizSubmissions;
    }

    public void setQuizSubmissions(List<QuizSubmission> quizSubmissions) {
        this.quizSubmissions = quizSubmissions;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public void addNotification(Notification notification) {
        this.notifications.add(notification);
        notification.setStudent(this);
    }

    public void removeNotification(Notification notification) {
        this.notifications.remove(notification);
        notification.setStudent(null);
    }
}
