package com.dev.LMS.service;

import com.dev.LMS.model.Course;
import com.dev.LMS.model.Instructor;
import com.dev.LMS.repository.CourseRepository;
import org.springframework.stereotype.Service;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    CourseService(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    public Course createCourse(Course course, Instructor instructor){
        course.setInstructor(instructor);
        courseRepository.save(course);
        return course;
    }
}
