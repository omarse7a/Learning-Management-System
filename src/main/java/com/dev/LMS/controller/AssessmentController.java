package com.dev.LMS.controller;

import com.dev.LMS.model.*;
import com.dev.LMS.service.AssessmentService;
import com.dev.LMS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@Controller
@RequestMapping("/course/{course-name}")
public class AssessmentController {

    @Autowired
    AssessmentService assessmentService;
    UserService userService;
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
    public ResponseEntity<?> createAssignment(@RequestBody String courseName, @RequestBody Assignment assignment){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found, Please register or login first");
        }
        if (!(user  instanceof Instructor)) {
            return ResponseEntity.status(403).body("You are not authorized to create an assignment");
        }
        Instructor instructor = (Instructor) user;
        // returns true if the user is the instructor of this course
        if(assessmentService.addAssignment(courseName,assignment,instructor)){
            return ResponseEntity.status(HttpStatus.CREATED).body("Assignment created successfully");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to add assignments to this course");
    }

    @GetMapping("/view-assignments")
    public ResponseEntity<?> viewAssignments(@PathVariable String courseName){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found, Please register or login first");
        }
        if ((user  instanceof Instructor )|| (user instanceof Student)) {}

        return ResponseEntity.status(403).body("You are not authorized to create an assignment");
    }

    @GetMapping("/view-assignment/{id}")
    public ResponseEntity<?> viewAssignment(@PathVariable("id") int assignment_id){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found, Please register or login first");
        }
        if (user  instanceof Instructor) {

        }
        else if(user  instanceof Student) {

        }
        return ResponseEntity.status(403).body("You are not authorized to create an assignment");
    }

}
