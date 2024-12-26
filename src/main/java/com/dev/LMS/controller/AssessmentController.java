package com.dev.LMS.controller;

import com.dev.LMS.dto.*;
import com.dev.LMS.exception.CourseNotFoundException;
import com.dev.LMS.model.*;
import com.dev.LMS.service.AssessmentService;
import com.dev.LMS.service.CourseService;
import com.dev.LMS.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContextException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@Controller
@RequestMapping("/course/{course-name}")
public class AssessmentController {

    AssessmentService assessmentService;
    UserService userService;
    CourseService courseService;
    @PostMapping("/create-question") //tested
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
    @GetMapping("/get-questions") //tested
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
    } @GetMapping("/get-question-by-id") //tested
    public ResponseEntity<?> getQuestions(@PathVariable("course-name") String courseName, @RequestBody int questionId){
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
            QuizSubmissionDto quizSubmission = assessmentService.generateQuiz(courseName, quizName ,(Student)user);
            System.out.println(quizSubmission);
            return ResponseEntity.status(HttpStatus.CREATED).body(quizSubmission);
        } catch (CourseNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @PostMapping("/{quizName}/submit-quiz")
    public ResponseEntity<?> submitQuiz(
            @PathVariable("course-name") String courseName,
            @PathVariable("quizName") String quizName,@RequestBody List<SubmittedQuestion> submittedQuestions) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found. Please register or login first.");
        }
        if (!(user instanceof Student)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only students can submit quizzes.");
        }
        try {
            assessmentService.submitQuiz(courseName, quizName, submittedQuestions,(Student) user);
            return ResponseEntity.status(HttpStatus.CREATED).body("submitted successfully");
        } catch (CourseNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/{quizName}/grade")
   // getQuizGrade(String quizTitle,String courseName , Student user)
    public ResponseEntity<?> getGrade(@PathVariable("course-name") String courseName , @PathVariable("quizName") String quizTitle){
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
    public ResponseEntity<?> createAssignment(@PathVariable("course-name") String courseName,
                                              @RequestBody Assignment assignment)
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
            // retrieving course
            Course course = courseService.getCourse(courseName);

            // returns true if the user is the instructor of this course
            AssignmentDto response = assessmentService.addAssignment(course,assignment,instructor);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }

    }

    @GetMapping("/assignments") // "/view-assignments"
    public ResponseEntity<?> viewAssignments(@PathVariable("course-name") String courseName)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found, Please register or login first");
        }
        // only instructor and student are authorized
        if (user instanceof Instructor || user instanceof Student) {
            try {
                // retrieving course
                Course course = courseService.getCourse(courseName);
                // retrieve the assignments from the course
                List<Assignment> assignments = course.getAssignments();
                List<AssignmentDto> assignmentDtos = assessmentService.getAssignments(course, user);
                // return assignments in response
                return ResponseEntity.ok(Map.of("assignments", assignmentDtos));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized to view this course assignment list: " + e.getMessage());
            }
        }
        return ResponseEntity.status(403).body("You are not authorized to view assignments.");
    }

    @GetMapping("/assignment/{assignment_id}/view")     // "/view-assignment/{id}"
    public ResponseEntity<?> viewAssignment(@PathVariable("course-name") String courseName,
                                            @PathVariable("assignment_id") int assignment_id)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found, Please register or login first");
        }
        // only instructor and student are authorized
        if (user instanceof Instructor || user instanceof Student) {
            try {
                // retrieving course
                Course course = courseService.getCourse(courseName);
                // retrieve the assignments from the course
                Assignment assignment = assessmentService.getAssignment(course, user, assignment_id);
                if (assignment == null) {
                    return ResponseEntity.status(404).body("Assignment not found");
                }
                // return the assignment in response
                return ResponseEntity.ok(new AssignmentDto(assignment));
            }
            catch (ApplicationContextException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized to view assignment: " + e.getMessage());
            }
            catch (IllegalStateException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
        }
        return ResponseEntity.status(403).body("You are not authorized to view assignments.");
    }

    @PostMapping("/assignment/{assignment_id}/submit")   // "submit-assignment/{assignment_id}"
    public ResponseEntity<?> submitAssignment(@PathVariable("course-name") String courseName,
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
        try {
            String response = assessmentService.uploadSubmissionFile(file, assignment, student);
            return ResponseEntity.ok(response);
        }
        catch (ApplicationContextException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized to submit assignment: " + e.getMessage());
        }
        catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/assignment/{assignment_id}/submissions")
    public ResponseEntity<?> getSubmissionsList(@PathVariable("course-name") String courseName,
                                                @PathVariable("assignment_id") int assignmentId)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        if(user == null){
            return ResponseEntity.badRequest().body("User not found, Please register or login first.");
        }
        if(!(user instanceof Instructor)) {
            return ResponseEntity.status(403).body("You are not authorized to view students' submissions list.");
        }
        try {
            Course course = courseService.getCourse(courseName);
            Assignment assignment = assessmentService.getAssignment(course, user, assignmentId);
            List<AssignmentSubmissionDto> submissionsDto = assessmentService.getSubmissions(assignment);
            return ResponseEntity.ok(submissionsDto);
        }
        catch (ApplicationContextException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized to view assignment submissions list: " + e.getMessage());
        }
        catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/assignment/{assignment_id}/submission/{submission_id}")
    public ResponseEntity<?> getAssignmentSubmission(@PathVariable("course-name") String courseName,
                                                     @PathVariable("assignment_id") int assignmentId,
                                                     @PathVariable("submission_id") int submissionId)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        if(user == null){
            return ResponseEntity.badRequest().body("User not found, Please register or login first.");
        }
        if(!(user instanceof Instructor)) {
            return ResponseEntity.status(403).body("You are not authorized to view a students' submissions.");
        }
        try {
            Course course = courseService.getCourse(courseName);
            Assignment assignment = assessmentService.getAssignment(course, user, assignmentId);
            byte[] submissionFile = assessmentService.downloadSubmissionFile(assignment, submissionId);

            return ResponseEntity.status(200).contentType(MediaType.APPLICATION_PDF).body(submissionFile);
        }
        catch (ApplicationContextException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized to view assignment submissions: " + e.getMessage());
        }
        catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/assignment/{assignment_id}/submission/{submission_id}/grade")
    public ResponseEntity<?> gradeAssignment(@PathVariable("course-name") String courseName,
                                             @PathVariable("assignment_id") int assignmentId,
                                             @PathVariable("submission_id") int submissionId,
                                             @RequestBody Map<String, Integer> gradeMap)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        if(user == null){
            return ResponseEntity.badRequest().body("User not found, Please register or login first.");
        }
        if(!(user instanceof Instructor)) {
            return ResponseEntity.status(403).body("You are not authorized to grade a students' submissions.");
        }
        try {
            Course course = courseService.getCourse(courseName);
            Assignment assignment = assessmentService.getAssignment(course, user, assignmentId);
            AssignmentSubmission submission = assessmentService.getSubmission(assignment, submissionId);
            AssignmentSubmissionDto gradedSubmission = assessmentService.setAssignmentGrade(submission, course, gradeMap);
            return ResponseEntity.status(HttpStatus.CREATED).body(gradedSubmission);
        }
        catch (ApplicationContextException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized to grade assignment submissions: " + e.getMessage());
        }
        catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/assignment/{assignment_id}/get-grade")
    public ResponseEntity<?> getAssignmentGrade(@PathVariable("course-name") String courseName,
                                             @PathVariable("assignment_id") int assignmentId,
                                             @RequestBody Map<String, Integer> gradeMap)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        if(user == null){
            return ResponseEntity.badRequest().body("User not found, Please register or login first.");
        }
        if(!(user instanceof Student)) {
            return ResponseEntity.status(403).body("You are not authorized to view a submission's grade.");
        }
        try {
            Student student = (Student) user;
            Course course = courseService.getCourse(courseName);
            Assignment assignment = assessmentService.getAssignment(course, user, assignmentId);
            int grade = assessmentService.getAssignmentGrade(assignment, student);
            Map<String, Integer> response = new HashMap<>();
            response.put("grade", grade);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        catch (ApplicationContextException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}