package com.dev.LMS.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class LessonResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int resource_id;

    @Column(nullable = false)
    private String file_name;

    @Column(nullable = false)
    private String file_type;

    @Column(nullable = false)
    private String file_path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    @JsonIgnore
    private Lesson lesson;

    public LessonResource() {}

    public LessonResource(int resource_id, String file_name, String file_type, String file_path) {
        this.resource_id = resource_id;
        this.file_name = file_name;
        this.file_type = file_type;
        this.file_path = file_path;
    }

    public int getResource_id() {
        return resource_id;
    }

    public void setResource_id(int resource_id) {
        this.resource_id = resource_id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }
}
