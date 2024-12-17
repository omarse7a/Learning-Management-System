package com.dev.LMS.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="students")
public class Student extends  User{
    //lesson
    @ManyToMany(mappedBy = "attendees")
    private Set<Lesson> lessonAttended = new HashSet<>();

    //courses
    @ManyToMany(mappedBy = "enrolled_students")
    private Set<Course> enrolled_courses = new HashSet<>();

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

}
