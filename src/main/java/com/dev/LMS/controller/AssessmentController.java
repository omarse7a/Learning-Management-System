package com.dev.LMS.controller;

import com.dev.LMS.dto.QuestionDto;
import com.dev.LMS.dto.QuizDto;
import com.dev.LMS.exception.CourseNotFoundException;
import com.dev.LMS.model.*;
import com.dev.LMS.service.AssessmentService;
import com.dev.LMS.service.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@Controller
@RequestMapping("/course/{course-name}")
public class AssessmentController {

    AssessmentService assessmentService ;
    UserService userService;
    @PostMapping("/create-question")
    public ResponseEntity<?> addQuestion(@PathVariable("course-name") String courseName,
                                         @RequestBody Question question)
    {

        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found, Please register or login first");
            }
            if (!(user  instanceof Instructor)) {
                return ResponseEntity.status(403).body("You are not authorized to create an assignment");
            }
            Instructor instructor = (Instructor) user;
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
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found, Please register or login first");
        }
        if (!(user  instanceof Instructor)) {
            return ResponseEntity.status(403).body("You are not authorized to create an assignment");
        }
        try {
            List<QuestionDto> questions = assessmentService.getQuestions(courseName);
            return  ResponseEntity.ok(questions);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    } @GetMapping("/get-question-by-id")
    public ResponseEntity<?> getQuestions(@PathVariable("course-name") String courseName,@RequestBody int questionId){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found, Please register or login first");
        }
        if (!(user  instanceof Instructor)) {
            return ResponseEntity.status(403).body("You are not authorized to create an assignment");
        }
        try {
           QuestionDto question =  assessmentService.getQuestionById(courseName,questionId);
            return ResponseEntity.ok(question);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/take-quiz/{quizName}")
    public ResponseEntity<?> takeQuiz(
            @PathVariable("course-name") String courseName,
            @PathVariable("quizName") String quizName) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found. Please register or login first.");
        }
        if (!(user instanceof Student)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only students can take quizzes.");
        }

        try {
            QuizDto quiz = assessmentService.generateQuiz(courseName, quizName);
            return ResponseEntity.status(HttpStatus.CREATED).body(quiz);
        } catch (CourseNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @PostMapping("/submit-quiz/{quizName}")
    public ResponseEntity<?> submitQuiz(
            @PathVariable("course-name") String courseName,
            @PathVariable("quizName") String quizName,@RequestBody QuizSubmission quizSubmission) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found. Please register or login first.");
        }
        if (!(user instanceof Student)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only students can take quizzes.");
        }
        try {
            assessmentService.submitQuiz(courseName, quizName,quizSubmission,(Student) user);
            return ResponseEntity.status(HttpStatus.CREATED).body("submitted successfully");
        } catch (CourseNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


}
