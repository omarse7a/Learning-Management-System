package com.dev.LMS.dto;


import com.dev.LMS.model.Assignment;
import com.dev.LMS.model.Course;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentDto {
    private int id;
    private String title;
    private String description;
    private LocalDateTime dueDate;   // "2024-12-20T10:15:30"
    private String courseName;
    private int numberOfSubmissions;


    public AssignmentDto(Assignment assignment) {
        this.id = assignment.getAssignmentId();
        this.title = assignment.getTitle();
        this.description = assignment.getDescription();
        this.dueDate = assignment.getDueDate();
        this.courseName = assignment.getCourse().getName();
        this.numberOfSubmissions = assignment.getSubmissions().size();
    }

    public AssignmentDto(String title, String description, String courseName, LocalDateTime dueDate) {
        this.title = title;
        this.description = description;
        this.courseName = courseName;
        this.dueDate = dueDate;
    }
}
