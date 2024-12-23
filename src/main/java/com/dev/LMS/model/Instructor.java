package com.dev.LMS.model;

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

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL)
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

    public void addNotification(Notification notification) {
        this.notifications.add(notification);
        notification.setInstructor(this);
    }

    public void removeNotification(Notification notification) {
        this.notifications.remove(notification);
        notification.setInstructor(null);
    }
}
