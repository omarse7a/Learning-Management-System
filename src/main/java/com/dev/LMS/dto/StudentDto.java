package com.dev.LMS.dto;

import com.dev.LMS.model.Student;

public class StudentDto {
    private String studentName;
    private String studentEmail;
    private int numberOfAttendedLessons;

    public StudentDto(Student student){
        this.studentName = student.getName();
        this.studentEmail = student.getEmail();
        this.numberOfAttendedLessons = student.getLessonAttended().size();
    }
}
