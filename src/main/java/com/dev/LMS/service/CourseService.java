package com.dev.LMS.service;

import java.util.*;
import com.dev.LMS.model.Course;
import com.dev.LMS.model.Instructor;
import com.dev.LMS.model.Student;
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

    public Course getCourse(String courseName) {
        Optional<Course> course = courseRepository.findByName(courseName);
        return course.orElse(null);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }


    public Set<Course> getCreatedCourses(Instructor instructor) {
        return instructor.getCreatedCourses();

    }

    public Set<Course> getEnrolledCourses(Student student) {
        return student.getEnrolled_courses();
    }
}
