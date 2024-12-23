package com.dev.LMS.controller;

import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.*;
import com.dev.LMS.dto.*;
import com.dev.LMS.model.*;
import com.dev.LMS.service.CourseService;
import com.dev.LMS.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Controller
public class CourseController
{

    private final CourseService courseService;
    private final UserService userService;



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

    @GetMapping("/search-course/{courseName}")
    public ResponseEntity<?> getCourse(@Valid  @PathVariable("courseName") String courseName ){
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

    @PostMapping("/course/{courseName}/add-lesson")
    public ResponseEntity<?> addLesson(@PathVariable("courseName") String courseName, @RequestBody Lesson lesson){
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

    @GetMapping("/course/{courseName}/lessons")
    public ResponseEntity<?> getAllLessons(@PathVariable("courseName") String courseName){
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

    @GetMapping("/course/{courseName}/lessons/{lessonId}")
    public ResponseEntity<?> getLesson(@PathVariable("courseName") String courseName,@PathVariable("lessonId") int lessonId){
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

    @PostMapping("/course/{courseName}/lessons/{lessonId}/add-resource")
    public ResponseEntity<?> addResource(@PathVariable("courseName") String courseName,@PathVariable("lessonId") int lessonId,@RequestParam MultipartFile file){
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
            if (!(user instanceof Instructor)) return ResponseEntity.status(403).body("You are not authorized to add a resource to this lesson");
            Instructor instructor = (Instructor) user;
            if (instructor.getId() != course.getInstructor().getId()) return ResponseEntity.badRequest().body("You are not authorized to add a resource to this lesson");
            String message = courseService.addLessonResource(course, lessonId, file);
            return ResponseEntity.ok(message);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("An error occurred" + e.getMessage());
        }

    }

    @GetMapping("/course/{courseName}/lessons/{lessonId}/resources")
    public ResponseEntity<?> getAllResources(@PathVariable("courseName") String courseName,@PathVariable("lessonId") int lessonId){
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
            List<LessonResourceDto> resources = courseService.getLessonResources(course, user, lessonId);

            return  ResponseEntity.ok(resources);

        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body("An error occurred" + e.getMessage());
        }
    }

    @GetMapping("/course/{courseName}/lessons/{lessonId}/resources/{resourceId}")
    public ResponseEntity<?> getResource(@PathVariable("courseName") String courseName,
                                         @PathVariable("lessonId") int lessonId,
                                         @PathVariable("resourceId") int resourceId
    ){
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
            byte[] resourceFile = courseService.getFileResources(course, user, lessonId, resourceId);
            List<LessonResourceDto> lessonResources = courseService.getLessonResources(course,user,lessonId);
            LessonResourceDto resource = null;
            for (LessonResourceDto lessonResourceDto : lessonResources) {
                if (lessonResourceDto.getResource_id() == resourceId){
                    resource = lessonResourceDto;
                    break;
                }
            }
            String mimeType = URLConnection.guessContentTypeFromName(resource.getFile_name());
            return ResponseEntity.status(200).contentType(MediaType.parseMediaType(mimeType)).body(resourceFile);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body("An error occurred" + e.getMessage());
        }

    }

    @PostMapping("/course/{courseName}/enroll")
    public ResponseEntity<?> enrollCourse(@PathVariable("courseName") String courseName){
       try {
           String email = SecurityContextHolder.getContext().getAuthentication().getName();
           User user = userService.getUserByEmail(email);
           if (!(user instanceof Student))
               return ResponseEntity.status(403).body("You are not authorized to enroll in this course");
           Set<CourseDto> enrolledCourses = courseService.enrollCourse(courseName, user);
           return ResponseEntity.ok(enrolledCourses);
       }
       catch (Exception e){
           return ResponseEntity.badRequest().body("An error occurred" + e.getMessage());
       }

    }

    @GetMapping("/course/{courseName}/enrolled")
    public ResponseEntity<?> getEnrolledStudents(@PathVariable("courseName") String courseName){
        try{
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email);
            if (user == null) {
                throw new Exception("User not found, Please register or login first");
            }
            Set<StudentDto> studentDtos = courseService.getEnrolledStd(courseName);
            return ResponseEntity.ok(studentDtos);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body("An error occurred" + e.getMessage());
        }
    }

    @DeleteMapping("/course/{courseName}/remove-student/{studentId}")
    public ResponseEntity<?> removeEnrolledStd(@PathVariable("courseName") String courseName, @PathVariable("studentId") int studentId)
    {
        try{

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found, Please register or login first");
        }
        if (!(user instanceof Instructor)) {
            return ResponseEntity.status(403).body("You are not authorized to remove a student from this course");
        }
        Instructor instructor = (Instructor) user;
        Course course = courseService.getCourse(courseName);
        if (course == null) return ResponseEntity.badRequest().body("Course not found");
        if (instructor.getId() != course.getInstructor().getId()) {
            return ResponseEntity.status(403).body("You are not authorized to remove a student from this course");
        }

        courseService.removeEnrolledstd(course, instructor, studentId);
        return ResponseEntity.ok("Student removed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("An error occurred" + e.getMessage());
        }
    }

    @PostMapping("/course/{courseName}/lessons/{lessonId}/generate-OTP")
    public ResponseEntity<?> generateOTP(@PathVariable("courseName") String courseName,@PathVariable int lessonId,@RequestParam("duration") int duration){
        try{
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email);

            if (user == null) {return ResponseEntity.badRequest().body("User not found, Please register or login first");}
            if (!(user instanceof Instructor)) {return ResponseEntity.badRequest().body("You are not authorized");}

            Instructor instructor = (Instructor) user;
            Course course = courseService.getCourse(courseName);

            if (course == null) return ResponseEntity.badRequest().body("Course not found");
            Lesson lesson = courseService.getLessonbyId(course, lessonId);

            if (lesson == null || lesson.getLessonOTP() != null && lesson.getLessonOTP().getExpireAt().isBefore(LocalDateTime.now())) return ResponseEntity.badRequest().body("Lesson id not found or OTP generated before and not expired.");

            if (course.getInstructor().getId() != instructor.getId()) {return ResponseEntity.badRequest().body("You are not authorized to generate OTP for this course.");}

            //get enrolled students
            Set<Student> students = courseService.getEnrolledStd(course);
            int otp = courseService.generateOTP(
                    course, students, instructor, lesson, duration
            );
            return ResponseEntity.ok(otp);

        }catch (Exception e){
            return ResponseEntity.badRequest().body("An error occurred" + e.getMessage());
        }


    }


    @PostMapping("/course/{courseName}/lessons/{lessonId}/attendLesson")
    public ResponseEntity<?> attendLesson(@PathVariable("courseName") String courseName,@PathVariable int lessonId,@RequestParam("otp") int otp)
    {
        try{
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email);
            if (user == null) {return ResponseEntity.badRequest().body("User not found, Please register or login first");}
            if (! (user instanceof Student)) {return ResponseEntity.badRequest().body("You are not allowed to attend lessons");}

            Student student = (Student) user;
            Course course = courseService.getCourse(courseName);
            if (course == null) return ResponseEntity.badRequest().body("Course not found");
            if (!(student.getEnrolled_courses().contains(course))) {
                return ResponseEntity.status(403).body("Student is not enrolled course");
            }

            Lesson lesson = courseService.getLessonbyId(course, lessonId);
            if (lesson == null) return ResponseEntity.badRequest().body("Lesson id not found");
            if (lesson.getAttendees().contains(student)) {
                return ResponseEntity.status(403).body("Lesson already attended");
            }

            LessonDto lessonDto = courseService.attendLesson(course, student, lesson, otp);
            return ResponseEntity.ok(lessonDto);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body("An error occurred" + e.getMessage());
        }



    }

    @GetMapping("/course/{courseName}/attended-lessons")
    public ResponseEntity<?> getAttendedLessons(@PathVariable("courseName") String courseName ){
        try{
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email);

            if (user == null) return ResponseEntity.badRequest().body("User not found, Please register or login first");
            if (! (user instanceof Student)) return ResponseEntity.badRequest().build();

            Student student = (Student) user;
            Course course = courseService.getCourse(courseName);
            if (course == null) return ResponseEntity.badRequest().body("Course not found");
            Set <LessonDto>lessondto = courseService.getLessonAttended(course, student);
            return ResponseEntity.ok(lessondto);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body("An error occurred" + e.getMessage());
        }
    }


    @GetMapping("/course/{courseName}/lessons/{lessonId}/attendanceList")
    public ResponseEntity<?> getAttendance(@PathVariable("courseName") String courseName,@PathVariable int lessonId){
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email);

            if (user == null) {
                return ResponseEntity.badRequest().body("User not found, Please register or login first");
            }
            if (!(user instanceof Instructor)) {
                return ResponseEntity.badRequest().body("You are not authorized");
            }

            Instructor instructor = (Instructor) user;
            Course course = courseService.getCourse(courseName);

            if (course == null) return ResponseEntity.badRequest().body("Course not found");
            Lesson lesson = courseService.getLessonbyId(course, lessonId);

            if (lesson == null) return ResponseEntity.badRequest().body("Lesson id not found");

            List<StudentDto> attendanceList = courseService.getAttendance(lesson);
            return ResponseEntity.ok(attendanceList);

        }
        catch (Exception e){
            return ResponseEntity.badRequest().body("An error occurred" + e.getMessage());
        }
    }



}
