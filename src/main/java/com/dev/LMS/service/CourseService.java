package com.dev.LMS.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import com.dev.LMS.dto.CourseDto;
import com.dev.LMS.dto.LessonResourceDto;
import com.dev.LMS.dto.StudentDto;
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

    public List<LessonResourceDto> getLessonResources(Course course, User user, int lessonId) {
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
        List<LessonResourceDto> lessonResourceDtos = new ArrayList<>();
        for (LessonResource lessonResource : lessonResources) {
            lessonResourceDtos.add(new LessonResourceDto(lessonResource));
        }
        return lessonResourceDtos;
    }

    public byte[] getFileResources(Course course, User user, int lessonId, int resourceId) {
        Lesson lesson = getLessonbyId(course, lessonId);
        if (lesson == null) throw new IllegalStateException("Lesson not found");
        if (user instanceof Instructor) {
            Instructor instructor = (Instructor) user;
            if (instructor.getId() != course.getInstructor().getId())
                throw new IllegalStateException("You are not authorized to access this resource(I)");
        }
        if (user instanceof Student) {
            Student student = (Student) user;
            if (!student.getEnrolled_courses().contains(course) || !lesson.getAttendees().contains(student) )
                throw new IllegalStateException("You are not authorized to access this resource");
        }
        List<LessonResource> resources = lesson.getLessonResources();
        LessonResource resource = null;
        for (LessonResource lessonResource : resources) {
            if (lessonResource.getResource_id() == resourceId) {
                resource = lessonResource;
                break;
            }
        }
        if (resource == null)
            throw new IllegalStateException("Resource not found");

        // storing the file into a byte array
        String fileName = resource.getFile_name();
        try {
            byte[] resourceData = Files.readAllBytes(resourcesPath.resolve(fileName));
            return resourceData;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public Set<CourseDto> enrollCourse(String courseName, User user) {
        Student student = (Student) user;
        Course course = getCourse(courseName);
        if (course == null) throw new IllegalStateException("Course not found");
        if (course.getEnrolled_students().contains(student))
            throw new IllegalStateException("You are already enrolled in this course");
        course.addStudent(student);
        courseRepository.save(course);
        Set<Course> enrolledCourses = student.getEnrolled_courses();
        Set<CourseDto> enrolledCoursesDto = new HashSet<>();
        for (Course c : enrolledCourses) {
            enrolledCoursesDto.add(new CourseDto(c));
        }
        return enrolledCoursesDto;


    }

    public Set<StudentDto> getEnrolledStd(String courseName) {
        Course course = getCourse(courseName);
        if (course == null) throw new IllegalStateException("Course not found");
        Set<Student> students = course.getEnrolled_students();
        Set<StudentDto> studentDtos = new HashSet<>();
        for (Student s : students) {
            studentDtos.add(new StudentDto(s));
        }
        return studentDtos;
    }
}
