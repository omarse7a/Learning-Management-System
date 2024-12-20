package com.dev.LMS.dto;

import com.dev.LMS.model.Course;
import lombok.Data;

@Data
public class CourseDto {
    private int id;
    private String courseName;
    private String courseDescription;
    private float courseDuration;
    private String instructorName;
    private int numberOfStudents;
    private int numberofLessons;



    public CourseDto(Course course) {
        this.id = (int) course.getCourseId();
        this.courseName = course.getName();
        this.courseDescription = course.getDescription();
        this.courseDuration =  course.getDuration();
        this.instructorName = course.getInstructor().getName();
        this.numberOfStudents = course.getEnrolled_students().size();
        this.numberofLessons = course.getLessons().size();


    }


}
