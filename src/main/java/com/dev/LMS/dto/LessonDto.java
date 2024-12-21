package com.dev.LMS.dto;

import com.dev.LMS.model.Lesson;
import lombok.Data;

@Data
public class LessonDto {
    private int id;
    private String title;
    private String description;
    private String courseName;
    private int numberOfAttendees;


    public LessonDto() {
    }
    public LessonDto(Lesson lesson) {
        this.id = lesson.getLesson_id();
        this.title = lesson.getTitle();
        this.description = lesson.getDescription();
        this.courseName = lesson.getCourse().getName();
        this.numberOfAttendees = lesson.getAttendees().size();
    }
}
