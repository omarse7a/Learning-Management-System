package com.dev.LMS.model;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="instructors")
public class Instructor extends User {
    @OneToMany(mappedBy = "instructor")
    private Set<Course> createdCourses;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "instructor_id")
    private List<Notification> notifications = new ArrayList<>();

    public Instructor() {}

    public Instructor(String name, String email) {
        super(name, email, Role.INSTRUCTOR);
    }

    public Set<Course> getCreatedCourses() {
        return createdCourses;
    }

    public void setCreatedCourses(Set<Course> createdCourses) {
        this.createdCourses = createdCourses;
    }

    public void createCourse(Course course) {
        this.createdCourses.add(course);
        course.setInstructor(this);
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}
