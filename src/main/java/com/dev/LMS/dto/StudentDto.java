package com.dev.LMS.dto;

import com.dev.LMS.model.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
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
