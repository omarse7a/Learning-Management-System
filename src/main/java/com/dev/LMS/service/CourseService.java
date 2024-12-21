package com.dev.LMS.service;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import com.dev.LMS.model.*;
import com.dev.LMS.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
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

    public List<UrlResource> getLessonResources(Course course, User user, int lessonId) {
        Lesson lesson = getLessonbyId(course, lessonId);
        if (lesson == null) throw new IllegalStateException("Lesson not found");
        if (user instanceof Instructor) {
           Instructor instructor = (Instructor) user;
           if (instructor.getId() != course.getInstructor().getId())
               throw new IllegalStateException("You are not authorized to access this resource");
        }
        if (user instanceof Student) {
            Student student = (Student) user;
            if (!student.getEnrolled_courses().contains(course) || !lesson.getAttendees().contains(student) )
                throw new IllegalStateException("You are not authorized to access this resource");
        }

        List<LessonResource> lessonResources = lesson.getLessonResources();
        List<UrlResource> urlResources = new ArrayList<>();
        for (LessonResource lessonResource : lessonResources) {
            urlResources.add(getResource(lessonResource));
        }
        return urlResources;
    }

    private UrlResource getResource(LessonResource lessonResource) {
        UrlResource resource;
        try {
            Path file = resourcesPath.resolve(lessonResource.getFile_name());
            resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else throw new RuntimeException("File Cannot be read");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
