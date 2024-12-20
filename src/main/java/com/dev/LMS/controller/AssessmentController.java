package com.dev.LMS.controller;

import com.dev.LMS.dto.AssignmentDto;
import com.dev.LMS.model.*;
import com.dev.LMS.service.AssessmentService;
import com.dev.LMS.service.CourseService;
import com.dev.LMS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@Controller
@RequestMapping("/course/{course-name}")
public class AssessmentController {

    @Autowired
    AssessmentService assessmentService;
    @Autowired
    UserService userService;
    @Autowired
    CourseService courseService;
    @PostMapping("/create-question")
    public ResponseEntity<?> addQuestion(@PathVariable("course-name") String courseName,
                                         @RequestBody Question question)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found, Please register or login first");
        }
        if (!(user  instanceof Instructor)) {
            return ResponseEntity.status(403).body("You are not authorized to create an assignment");
        }
        Instructor instructor = (Instructor) user;
        try {
            System.out.println(question);
            assessmentService.createQuestion(courseName,question);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/create-quiz")
    public ResponseEntity<?> createQuiz(@PathVariable("course-name") String courseName,
                                        @RequestBody Quiz quiz)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found, Please register or login first");
        }
        if (!(user  instanceof Instructor)) {
            return ResponseEntity.status(403).body("You are not authorized to create an assignment");
        }
        Instructor instructor = (Instructor) user;
        try {
            System.out.println(quiz);
            assessmentService.createQuiz(courseName,quiz);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get-questions")
    public ResponseEntity<?> getQuestions(@PathVariable("course-name") String courseName){

        try {
            assessmentService.getQuestions(courseName);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    } @GetMapping("/get-question-by-id")
    public ResponseEntity<?> getQuestions(@PathVariable("course-name") String courseName,@RequestBody int questionId){
        try {
            assessmentService.getQuestionById(courseName,questionId);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @GetMapping("/take-quiz")
//    public ResponseEntity<?> takeQuiz(@PathVariable("course-name") String courseName){
//
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        User user = userService.getUserByEmail(email);
//        if (user == null) {
//            return ResponseEntity.badRequest().body("User not found, Please register or login first");
//        }
//        if (!(user  instanceof Student)) {
//            return ResponseEntity.status(403).body("You are not StudentStudent");
//        }
//        Instructor instructor = (Instructor) user;
//
//    }

    @PostMapping("/create-assignment")
    public ResponseEntity<?> createAssignment(@PathVariable("course-name") String courseName, @RequestBody Assignment assignment){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found, Please register or login first");
        }
        if (!(user  instanceof Instructor)) {
            return ResponseEntity.status(403).body("You are not authorized to create an assignment");
        }
        Instructor instructor = (Instructor) user;
        // retrieving course
        Course course = courseService.getCourse(courseName);

        // returns true if the user is the instructor of this course
        boolean created = assessmentService.addAssignment(course,assignment,instructor);
        if(created){
            return ResponseEntity.status(HttpStatus.CREATED).body("Assignment created successfully");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to add assignments to this course");
    }

    @GetMapping("/view-assignments")
    public ResponseEntity<?> viewAssignments(@PathVariable("course-name") String courseName){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found, Please register or login first");
        }
        // only instructor and student are authorized
        if (user instanceof Instructor || user instanceof Student) {
            // retrieving course
            Course course = courseService.getCourse(courseName);
            // retrieve the assignments from the course
            List<Assignment> assignments = course.getAssignments();
            List<AssignmentDto> assignmentDtos = assessmentService.getAssignments(course, user);
            // return assignments in response
            return ResponseEntity.ok(Map.of("assignments", assignmentDtos));
        }
        return ResponseEntity.status(403).body("You are not authorized to view assignments.");
    }

    @GetMapping("/view-assignment/{id}")
    public ResponseEntity<?> viewAssignment(@PathVariable("course-name") String courseName, @PathVariable("id") int assignment_id){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found, Please register or login first");
        }
        // only instructor and student are authorized
        if (user instanceof Instructor || user instanceof Student) {
            // retrieving course
            Course course = courseService.getCourse(courseName);
            if (course == null) {
                return ResponseEntity.badRequest().body("Course not found.");
            }
            // retrieve the assignments from the course
            Assignment assignment = assessmentService.getAssignment(course, user, assignment_id);

            // return the assignment in response
            return ResponseEntity.ok(new AssignmentDto(assignment));
        }
        return ResponseEntity.status(403).body("You are not authorized to view assignments.");
    }

    @PostMapping("submit-assignment/{assignment_id}")
    public ResponseEntity<?> submitAssignment(@PathVariable("course-name") String courseName, @PathVariable("id") int assignment_id, @RequestBody AssignmentSubmisson assignmentSubmisson){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        if(user == null){
            return ResponseEntity.badRequest().body("User not found, Please register or login first");
        }
        if(!(user instanceof Student)) {
            return ResponseEntity.status(403).body("You are not authorized to submit assignments.");
        }
        Course course = courseService.getCourse(courseName);
        Assignment assignment = assessmentService.getAssignment(course, user, assignment_id);
        return ResponseEntity.ok("Assignment submitted successfully");  ////////////////////
    }
}
