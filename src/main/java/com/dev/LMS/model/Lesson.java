package com.dev.LMS.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity

public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int lesson_id;
    @Column(nullable = false)
    @NotEmpty
    private String title;
    private String video_url;
    private String description;
    private int OTP;

    //extra lesson resource
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<LessonResource> lessonResources = new ArrayList<>();

    //Joining with course table
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @JsonBackReference
    private Course course;


    //Attendance List, Joining with user
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "lesson_attendance",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> attendees = new HashSet<>();

    public Lesson() {}

    public Lesson(int OTP, String video_url, String description, String title, int lesson_id) {
        this.OTP = OTP;
        this.description = description;
        this.title = title;
        this.lesson_id = lesson_id;
        this.video_url = video_url;
    }

    public int getLesson_id() {
        return lesson_id;
    }

    public void setLesson_id(int lesson_id) {
        this.lesson_id = lesson_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOTP() {
        return OTP;
    }

    public void setOTP(int OTP) {
        this.OTP = OTP;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }



    //for students
    public Set<User> getAttendees() {
        return attendees;
    }

    public void setAttendees(Set<User> attendees) {
        this.attendees = attendees;
    }

    public void addAttendee(@org.jetbrains.annotations.NotNull User user) {
            this.attendees.add(user);

    }


    //for courses
    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    //for resources
    public void setLessonResources(List<LessonResource> lessonResources) {
        this.lessonResources = lessonResources;
    }
    public List<LessonResource> getLessonResources() {
        return lessonResources;
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

}
