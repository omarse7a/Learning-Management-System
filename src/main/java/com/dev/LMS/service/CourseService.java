package com.dev.LMS.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.dev.LMS.model.*;
import com.dev.LMS.repository.CourseRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    @Value("${file.upload.base-path.lesson-resources}") //check application.yml
    private Path resourcesPath ;

    public CourseService(CourseRepository courseRepository)  {
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

    public Course getCourseById(int courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
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

    public Lesson addLesson(Course course, Lesson lesson) {
        course.addLesson(lesson);
        courseRepository.save(course);
        return course.getLessons().getLast();
    }

    public Lesson getLessonbyId(Course course, int lessonId) {
        List<Lesson> lessonList = course.getLessons();
        for (Lesson l : lessonList)
        {
            if (l.getLesson_id() == lessonId)
                return l;
        }
        return null;

    }


    public String addLessonResource(Course course, int lessonId, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileType = file.getContentType();
        try{
            if(fileName.contains("..")){
                throw new RuntimeException("Invalid file name");
            }
            Files.copy(file.getInputStream(), this.resourcesPath.resolve(file.getOriginalFilename()));

        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

        Lesson lesson = getLessonbyId(course, lessonId);
        if (lesson != null) {
            LessonResource lessonResource = new LessonResource(fileName, fileType);
            lesson.addLessonResource(lessonResource);
            courseRepository.save(course);
            return "file added successfully: " + fileName;
        }
        else throw new IllegalStateException("Lesson not found");
    }
}
