package com.dev.LMS.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    //lesson
    @ManyToMany(mappedBy = "attendees")
    private Set<Lesson> lessonAttended = new HashSet<>();

    //courses
    @ManyToMany(mappedBy = "enrolled_students")
    private Set<Course> enrolled_courses = new HashSet<>();

    public User() {
    }

    public User(String name, String email, Role role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }



    //for attending lesson (student)
    public void setLessonAttended(Set<Lesson> lessonAttended) {
        this.lessonAttended = lessonAttended;
    }
    public Set<Lesson> getLessonAttended() {
        return lessonAttended;
    }

    public void attendLesson(Lesson lesson) {
        this.lessonAttended.add(lesson);
    }


    //for enrolled course (student)
    public void setEnrolled_courses(Set<Course> enrolled_courses) {
        this.enrolled_courses = enrolled_courses;
    }

    public Set<Course> getEnrolled_courses() {
        return enrolled_courses;
    }
    public void enrollCourse(Course course) {
        this.enrolled_courses.add(course);
    }


}
