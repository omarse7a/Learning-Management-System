package com.dev.LMS.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.Set;

@Entity
@Table(name="instructors")
public class Instructor extends User {
    @OneToMany(mappedBy = "instructor")
    private Set<Course> createdCourses;

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
}
