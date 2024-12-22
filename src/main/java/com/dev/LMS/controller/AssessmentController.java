package com.dev.LMS.controller;

import com.dev.LMS.dto.AssignmentDto;
import com.dev.LMS.dto.AssignmentSubmissionDto;
import com.dev.LMS.dto.QuestionDto;
import com.dev.LMS.dto.QuizDto;
import com.dev.LMS.exception.CourseNotFoundException;
import com.dev.LMS.model.*;
import com.dev.LMS.service.AssessmentService;
import com.dev.LMS.service.CourseService;
import com.dev.LMS.service.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@Controller
@RequestMapping("/course/{courseName}")
public class AssessmentController {

    AssessmentService assessmentService ;
    UserService userService;
    CourseService courseService;
    @PostMapping("/create-question") //tested
    public ResponseEntity<?> addQuestion(@PathVariable("courseName") String courseName,
                                         @RequestBody Question question)
    {

        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found, Please register or login first");
            }
            if (!(user  instanceof Instructor)) {
                return ResponseEntity.status(405).body("You are not authorized to create an assignment");
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
    @PostMapping("/create-quiz") //tested
    public ResponseEntity<?> createQuiz(@PathVariable("courseName") String courseName,
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
    @GetMapping("/get-questions") //tested
    public ResponseEntity<?> getQuestions(@PathVariable("courseName") String courseName){
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
    } @GetMapping("/get-question-by-id") //tested
    public ResponseEntity<?> getQuestions(@PathVariable("courseName") String courseName, @RequestBody int questionId){
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
    @GetMapping("/{quizName}/take-quiz") //tested and fixed
    public ResponseEntity<?> takeQuiz(
            @PathVariable("courseName") String courseName,
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
            System.out.println(quiz);
            return ResponseEntity.status(HttpStatus.CREATED).body(quiz);
        } catch (CourseNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @PostMapping("/{quizName}/submit-quiz")
    public ResponseEntity<?> submitQuiz(
            @PathVariable("courseName") String courseName,
            @PathVariable("quizName") String quizName,@RequestBody QuizSubmission quizSubmission) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found. Please register or login first.");
        }
        if (!(user instanceof Student)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only students can submit quizzes.");
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
    @GetMapping("/{quizName}/grade")
   // getQuizGrade(String quizTitle,String courseName , Student user)
    public ResponseEntity<?> getGrade(@PathVariable("courseName") String courseName , @PathVariable("quizName") String quizTitle){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found, Please register or login first");
        }
        if (!(user  instanceof Student)) {
            return ResponseEntity.status(403).body("You are not authorized to get the grade");
        }
        try {
            int grade = assessmentService.getQuizGrade(quizTitle,courseName,(Student) user );
            return  ResponseEntity.ok(grade);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/create-assignment")
    public ResponseEntity<?> createAssignment(@PathVariable("courseName") String courseName,
                                              @RequestBody Assignment assignment)
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
        // retrieving course
        Course course = courseService.getCourse(courseName);

        // returns true if the user is the instructor of this course
        boolean created = assessmentService.addAssignment(course,assignment,instructor);
        if(created){
            return ResponseEntity.status(HttpStatus.CREATED).body("Assignment created successfully");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to add assignments to this course");
    }

    @GetMapping("/assignments") // "/view-assignments"
    public ResponseEntity<?> viewAssignments(@PathVariable("courseName") String courseName)
    {
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

    @GetMapping("/assignment/{assignment_id}/view")     // "/view-assignment/{id}"
    public ResponseEntity<?> viewAssignment(@PathVariable("courseName") String courseName,
                                            @PathVariable("assignment_id") int assignment_id)
    {
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
            Assignment assignment = assessmentService.getAssignment(course, user, assignment_id);
            if(assignment == null){
                return ResponseEntity.status(404).body("Assignment not found");
            }
            // return the assignment in response
            return ResponseEntity.ok(new AssignmentDto(assignment));
        }
        return ResponseEntity.status(403).body("You are not authorized to view assignments.");
    }

    @PostMapping("assignment/{assignment_id}/submit")   // "submit-assignment/{assignment_id}"
    public ResponseEntity<?> submitAssignment(@PathVariable("courseName") String courseName,
                                              @PathVariable("assignment_id") int assignmentId,
                                              @RequestParam("file") MultipartFile file)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        if(user == null){
            return ResponseEntity.badRequest().body("User not found, Please register or login first.");
        }
        if(!(user instanceof Student)) {
            return ResponseEntity.status(403).body("You are not authorized to submit assignments.");
        }
        if(!file.getOriginalFilename().endsWith(".pdf")){
            return ResponseEntity.badRequest().body("Only PDF files are allowed.");
        }
        Student student = (Student) user;
        Course course = courseService.getCourse(courseName);
        Assignment assignment = assessmentService.getAssignment(course, user, assignmentId);
        String response = assessmentService.uploadSubmissionFile(file, assignment, student);
        return ResponseEntity.ok(response);
    }

 
    public ResponseEntity<?> getSubmissionsList(@PathVariable("courseName") String courseName,
                                                @PathVariable("assignment_id") int assignmentId)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        if(user == null){
            return ResponseEntity.badRequest().body("User not found, Please register or login first.");
        }
        if(!(user instanceof Student)) {
            return ResponseEntity.status(403).body("You are not authorized to submit assignments.");
        }
        Course course = courseService.getCourse(courseName);
        Assignment assignment = assessmentService.getAssignment(course, user, assignmentId);
        List<AssignmentSubmissionDto> submissionsDto = assessmentService.getSubmissions(assignment);
        return ResponseEntity.ok(submissionsDto);
    }


    public ResponseEntity<?> getAssignmentSubmission(@PathVariable("courseName") String courseName,
                                                     @PathVariable("assignment_id") int assignmentId,
                                                     @PathVariable("submission_id") int submissionId)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        if(user == null){
            return ResponseEntity.badRequest().body("User not found, Please register or login first.");
        }
        if(!(user instanceof Student)) {
            return ResponseEntity.status(403).body("You are not authorized to submit assignments.");
        }
        Course course = courseService.getCourse(courseName);
        Assignment assignment = assessmentService.getAssignment(course, user, assignmentId);
        byte[] submissionFile = assessmentService.downloadSubmissionFile(assignment, submissionId);

        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_PDF).body(submissionFile);
    }


}
