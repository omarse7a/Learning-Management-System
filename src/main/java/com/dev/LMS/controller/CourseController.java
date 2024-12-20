package com.dev.LMS.controller;

import java.util.*;
import com.dev.LMS.dto.*;
import com.dev.LMS.model.*;
import com.dev.LMS.service.CourseService;
import com.dev.LMS.service.UserService;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class CourseController
{
    private final CourseService courseService;
    private final UserService userService;

    public CourseController(CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
    }

    @PostMapping("/create-course")
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
        try{
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found, Please register or login first");
            }
            if (!(user  instanceof Instructor)) {
                return ResponseEntity.status(403).body("You are not authorized to create a course");
            }
            Instructor instructor = (Instructor) user;
            Course createdcourse = courseService.createCourse(course, instructor);
            CourseDto courseDto = new CourseDto(createdcourse);
            return ResponseEntity.ok(courseDto);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body("An error occurred" + e.getMessage());
        }
    }

    @GetMapping("/search-course/{course-name}")
    public ResponseEntity<?> getCourse(@Valid  @PathVariable("course-name") String courseName ){
        Course course = courseService.getCourse(courseName);
        if(course == null){
            return ResponseEntity.badRequest().body("Course not found");
        }
        CourseDto courseDto = new CourseDto(course);
        return ResponseEntity.ok(courseDto);
    }
    @GetMapping("/course/{id}")
    public ResponseEntity<?> getCourse(@PathVariable("id") int id){
        Course course = courseService.getCourseById(id);
        if(course == null){
            return ResponseEntity.badRequest().body("Course not found");
        }
        CourseDto courseDto = new CourseDto(course);
        return ResponseEntity.ok(courseDto);
    }

    @GetMapping("/get-all-courses")
    public ResponseEntity<?> getAllCourses(){
        List<Course> courseList = courseService.getAllCourses();
        if(courseList == null){
            return ResponseEntity.ok().body("No courses found");
        }
        List<CourseDto> courseDtoList = new ArrayList<>();
        for(Course course: courseList){courseDtoList.add(new CourseDto(course));}
        return ResponseEntity.ok(courseDtoList);
    }

    @GetMapping("/get-my-courses")
    public ResponseEntity<?> getMyCourses(){
      try{
          String email = SecurityContextHolder.getContext().getAuthentication().getName();
          User user = userService.getUserByEmail(email);
          if (user == null) {
              return ResponseEntity.badRequest().body("User not found, Please register or login first");
          }
          if ((user  instanceof Instructor)) {
              Instructor instructor = (Instructor) user;
              Set<Course> createdCourses = courseService.getCreatedCourses(instructor);
              if(createdCourses == null){
                  return ResponseEntity.ok().body("No courses found");
              }
              Set<CourseDto> courseDtoList = new HashSet<>();
              for(Course course: createdCourses){courseDtoList.add(new CourseDto(course));}
              return ResponseEntity.ok(courseDtoList);

          }
          if (user instanceof Student){
              Student student = (Student) user;
              Set<Course> enrolledCourses = courseService.getEnrolledCourses(student);
              if(enrolledCourses == null){
                  return ResponseEntity.ok().body("No courses found");
              }
              Set<CourseDto> courseDtoList = new HashSet<>();
              for(Course course: enrolledCourses){courseDtoList.add(new CourseDto(course));}
              return ResponseEntity.ok(courseDtoList);

          }
          else
              return getAllCourses();

      }
      catch (Exception e){
          return ResponseEntity.badRequest().body("An error occurred" + e.getMessage());
      }
    }

    @PostMapping("/course/{course-name}/add-lesson")
    public ResponseEntity<?> addLesson(@PathVariable("course-name") String courseName, @RequestBody Lesson lesson){
        try{
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found, Please register or login first");
            }
            if (!(user  instanceof Instructor)) {
                return ResponseEntity.status(403).body("You are not authorized to add a lesson to this course");
            }
            else{
                Course course = courseService.getCourse(courseName);
                if(course == null){
                    return ResponseEntity.badRequest().body("Course not found");
                }
                Instructor instructor = (Instructor) user;
                if(course.getInstructor().getId() != instructor.getId()){
                    return ResponseEntity.status(403).body("You are not authorized to add a lesson to this course");
                }
                else{
                    Lesson addedLesson = courseService.addLesson(course, lesson);
                    LessonDto  lessonDto = new LessonDto(addedLesson);
                    return ResponseEntity.ok(lessonDto);
                }
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("An error occurred" + e.getMessage());
        }
    }

    @GetMapping("/course/{course-name}/lessons")
    public ResponseEntity<?> getAllLessons(@PathVariable("course-name") String courseName){
        try{
            Course course = courseService.getCourse(courseName);
            if(course == null){
                return ResponseEntity.badRequest().body("Course not found");
            }
            List<Lesson> lessons = course.getLessons();
            List<LessonDto> lessonDtoList = new ArrayList<>();
            for(Lesson lesson: lessons){lessonDtoList.add(new LessonDto(lesson));}
            return ResponseEntity.ok(lessonDtoList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("An error occurred" + e.getMessage());
        }
    }

    @GetMapping("/course/{course-name}/lessons/{lesson-id}")
    public ResponseEntity<?> getLesson(@PathVariable("course-name") String courseName,@PathVariable("lesson-id") int lessonId){
        try{
            Course course = courseService.getCourse(courseName);
            if(course == null){
                return ResponseEntity.badRequest().body("Course not found");
            }
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found, Please register or login first");
            }
            Lesson lesson = courseService.getLessonbyId(course, lessonId);
            if(lesson == null){
                return ResponseEntity.badRequest().body("Lesson not found");
            }
            if (user  instanceof Instructor ) {
                Instructor instructor = (Instructor) user;
                if (instructor.getId() != course.getInstructor().getId()) { //instructor of that course
                    LessonDto  lessonDto = new LessonDto(lesson);
                    return ResponseEntity.ok(lessonDto); //just simple data
                }
                else { //Instructor of the course
                    DetailedLessonDto detailedLessonDto = new DetailedLessonDto(lesson);
                    return ResponseEntity.ok(detailedLessonDto);
                }
            }
            else { //Student or may be admin
                LessonDto  lessonDto = new LessonDto(lesson);
                return ResponseEntity.ok(lessonDto);
            }

        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body("An error occurred" + e.getMessage());
        }

    }



}
