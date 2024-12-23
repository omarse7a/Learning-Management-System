package com.dev.LMS.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int lesson_id;
    @Column(nullable = false)
    @NotEmpty
    private String title;
    private String description;

    @OneToOne(mappedBy = "lesson", cascade = CascadeType.ALL)
    private LessonOTP lessonOTP ;

    //extra lesson resource
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    private List<LessonResource> lessonResources = new ArrayList<>();

    //Joining with course table
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;


    //Attendance List, Joining with user
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "lesson_attendance",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<Student> attendees = new HashSet<>();




    public void addAttendee(@NotNull Student user) {
            this.attendees.add(user);
            user.attendLesson(this);

    }


    public void addLessonResource(LessonResource lessonResource) {
        if (this.lessonResources == null) {
            this.lessonResources = new ArrayList<>();
        }
        this.lessonResources.add(lessonResource);
        lessonResource.setLesson(this);
    }

    public void removeLessonResource(LessonResource lessonResource) {
        this.lessonResources.remove(lessonResource);
        lessonResource.setLesson(null);
    }

    public void addLessonOTP(LessonOTP lessonOTP) {
        this.lessonOTP = lessonOTP;
        lessonOTP.setLesson(this);
    }

}
