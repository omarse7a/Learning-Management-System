package com.dev.LMS.controller;

import java.util.*;
import com.dev.LMS.dto.*;
import com.dev.LMS.model.Course;
import com.dev.LMS.model.Instructor;
import com.dev.LMS.model.Student;
import com.dev.LMS.model.User;
import com.dev.LMS.service.CourseService;
import com.dev.LMS.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    @GetMapping("/get-course")
    public ResponseEntity<?> getCourse(@RequestBody CourseRequestDto requestDto ){
        String courseName = requestDto.getCourseName();
        Course course = courseService.getCourse(courseName);
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


}
