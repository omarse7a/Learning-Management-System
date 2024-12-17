package com.dev.LMS.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique=true, nullable=false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Float duration;    // completion hours

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User instructor;



    //Lessons
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "course_id")
    @JsonManagedReference
    private List<Lesson> lesson = new ArrayList<>();

    //Attendance List
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "enrolled_students",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")

    )
    private Set<User> enrolled_students = new HashSet<>();

    public Course() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
       this.id = id ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getDuration() {
        return duration;
    }

    public void setDuration(Float duration) {
        this.duration = duration;
    }

    public User getInstructor() {
        return instructor;
    }

    public void setInstructor(User instructor) {
        this.instructor = instructor;
    }

    public Set<User> getEnrolled_students() {
        return enrolled_students;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id == course.id && Objects.equals(name, course.name) && Objects.equals(description, course.description) && Objects.equals(duration, course.duration) && Objects.equals(instructor, course.instructor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, duration, instructor);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", instructor_id=" + instructor +
                '}' +
                ", Number of Lessons= " + lesson.size()
                ;
    }



    //for lessons
    public List<Lesson> getLesson() {
        return lesson;
    }

    public void setLesson(List<Lesson> lesson) {
        this.lesson = lesson;
    }

    public void addLesson(Lesson lesson) {
        if (this.lesson == null) {
            this.lesson = new ArrayList<>();
        }
        this.lesson.add(lesson);
        lesson.setCourse(this);
    }

    public void removeLesson(Lesson lesson) {
        this.lesson.remove(lesson);
        lesson.setCourse(null);
    }

    //for enrolled students
    public void setEnrolled_students(Set<User> enrolled_students) {
        this.enrolled_students = enrolled_students;
    }

    public void addStudent(@NotNull User user) {
        if (user.getRole() == Role.STUDENT) {
            this.enrolled_students.add(user);
        }
        else {
            throw new IllegalArgumentException("Only students can enroll in courses");
        }
    }
}
