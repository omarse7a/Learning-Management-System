package com.dev.LMS;

import com.dev.LMS.controller.AssessmentController;
import com.dev.LMS.controller.UserController;
import com.dev.LMS.dto.AssignmentDto;
import com.dev.LMS.dto.AssignmentSubmissionDto;
import com.dev.LMS.dto.CourseDto;
import com.dev.LMS.model.*;
import com.dev.LMS.service.AssessmentService;
import com.dev.LMS.service.CourseService;
import com.dev.LMS.service.UserService;
import com.dev.LMS.util.UserFactory;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AssessmentControllerTest {

    @InjectMocks
    private AssessmentController assessmentController;

    @Mock
    private AssessmentService assessmentService;

    @Mock
    private UserService userService;

    @Mock
    private CourseService courseService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        String email = "instructor@test.com";
        Instructor instructor = new Instructor();
        instructor.setEmail(email);

        Course course = new Course();
        course.setName("course-1");
        course.setDescription("course description");
        course.setDuration(22.0f);
        course.setInstructor(instructor);

        when(courseService.getCourse("course-1")).thenReturn(course);
    }

    @Test
    void createAssignment_user_is_instructor() {
        String email = "instructor@test.com";
        Instructor instructor = new Instructor();
        instructor.setEmail(email);

        Course course = courseService.getCourse("course-1");

        Assignment a_expected = new Assignment();
        a_expected.setTitle("A-1");
        a_expected.setDescription("This is a test assignment");
        a_expected.setDueDate(LocalDateTime.of(2024, 12, 28, 10, 15, 30));
        a_expected.setCourse(course);
        AssignmentDto expectedDto = new AssignmentDto(a_expected);

        Assignment a_test = new Assignment();
        a_test.setTitle("A-1");
        a_test.setDescription("This is a test assignment");
        a_test.setDueDate(LocalDateTime.of(2024, 12, 28, 10, 15, 30));

        when(authentication.getName()).thenReturn(email);
        when(userService.getUserByEmail(email)).thenReturn(instructor);
        when(assessmentService.addAssignment(any(Course.class), any(Assignment.class), any(Instructor.class)))
                .thenReturn(expectedDto);

        ResponseEntity<?> response = assessmentController.createAssignment("course-1", a_test);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertInstanceOf(AssignmentDto.class, response.getBody());

        AssignmentDto returnedResponse = (AssignmentDto) response.getBody();
        assertEquals(expectedDto.getId(), returnedResponse.getId());
        assertEquals(expectedDto.getTitle(), returnedResponse.getTitle());
        assertEquals(expectedDto.getDescription(), returnedResponse.getDescription());
        assertEquals(expectedDto.getCourseName(), returnedResponse.getCourseName());
        assertEquals(expectedDto.getDueDate(), returnedResponse.getDueDate());
    }


    @Test
    void viewAllAssignments_user_is_authorized_and_assignment_exists() {
        String email = "student@test.com";
        String courseName = "course-1";

        // test user
        Student student = new Student();
        student.setEmail(email);

        // test course
        Course course = new Course();
        course.setName(courseName);
        course.setDescription("course description");
        course.setDuration(22.0f);
        course.setInstructor(new Instructor());
        course.setAssignments(new ArrayList<>()); // Empty assignments for simplicity

        // test assignment's list
        List<AssignmentDto> assignmentDtos = List.of(
                new AssignmentDto("Assignment 1", "Description 1", courseName, LocalDateTime.of(2024, 12, 28, 10, 0)),
                new AssignmentDto("Assignment 2", "Description 2", courseName, LocalDateTime.of(2025, 1, 15, 15, 30))
        );

        // mimicking the controller calls
        when(authentication.getName()).thenReturn(email);
        when(userService.getUserByEmail(email)).thenReturn(student);
        when(courseService.getCourse(courseName)).thenReturn(course);
        when(assessmentService.getAssignments(course, student)).thenReturn(assignmentDtos);

        // send the request
        ResponseEntity<?> response = assessmentController.viewAssignments(courseName);

        // assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());

        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertTrue(responseBody.containsKey("assignments"));
        List<?> returnedAssignments = (List<?>) responseBody.get("assignments");
        assertEquals(assignmentDtos.size(), returnedAssignments.size());

        for (int i = 0; i < assignmentDtos.size(); i++) {
            AssignmentDto expectedDto = assignmentDtos.get(i);
            AssignmentDto actualDto = (AssignmentDto) returnedAssignments.get(i);

            assertEquals(expectedDto.getTitle(), actualDto.getTitle());
            assertEquals(expectedDto.getDescription(), actualDto.getDescription());
            assertEquals(expectedDto.getCourseName(), actualDto.getCourseName());
            assertEquals(expectedDto.getDueDate(), actualDto.getDueDate());
        }

        // verifying method calls
        verify(userService).getUserByEmail(email);
        verify(courseService).getCourse(courseName);
        verify(assessmentService).getAssignments(course, student);
    }


    @Test
    void viewAssignment_user_is_authorized_and_assignment_exists() {
        String email = "student@test.com";
        String courseName = "course-1";
        int assignmentId = 101;

        // test user
        Student student = new Student();
        student.setEmail(email);

        // test course
        Course course = new Course();
        course.setName(courseName);
        course.setDescription("course description");
        course.setDuration(22.0f);

        // test assignment
        Assignment assignment = new Assignment();
        assignment.setAssignmentId(assignmentId);
        assignment.setTitle("Test Assignment");
        assignment.setDescription("This is a test assignment.");
        assignment.setDueDate(LocalDateTime.of(2024, 12, 28, 10, 0));
        assignment.setCourse(course);

        // mimicking calls by the controller
        when(authentication.getName()).thenReturn(email);
        when(userService.getUserByEmail(email)).thenReturn(student);
        when(courseService.getCourse(courseName)).thenReturn(course);
        when(assessmentService.getAssignment(course, student, assignmentId)).thenReturn(assignment);

        // send the request
        ResponseEntity<?> response = assessmentController.viewAssignment(courseName, assignmentId);

        // assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(AssignmentDto.class, response.getBody());

        AssignmentDto returnedDto = (AssignmentDto) response.getBody();
        assertEquals(assignment.getAssignmentId(), returnedDto.getId());
        assertEquals(assignment.getTitle(), returnedDto.getTitle());
        assertEquals(assignment.getDescription(), returnedDto.getDescription());
        assertEquals(assignment.getCourse().getName(), returnedDto.getCourseName());
        assertEquals(assignment.getDueDate(), returnedDto.getDueDate());

        // verifying method calls
        verify(userService).getUserByEmail(email);
        verify(courseService).getCourse(courseName);
        verify(assessmentService).getAssignment(course, student, assignmentId);
    }


    @Test
    void submitAssignment_successfulSubmission() throws Exception {
        String email = "student@test.com";
        String courseName = "course-1";
        int assignmentId = 101;

        // creating test student, course, and assignment
        Student student = new Student();
        student.setEmail(email);

        Course course = new Course();
        course.setName(courseName);

        Assignment assignment = new Assignment();
        assignment.setAssignmentId(assignmentId);
        assignment.setTitle("Test Assignment");
        assignment.setCourse(course);

        // creating a mock file
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "submission.pdf",
                "application/pdf",
                "Test PDF Content".getBytes()
        );

        String expectedResponse = "Assignment submitted successfully";

        // mimicking calls by the controller
        when(authentication.getName()).thenReturn(email);
        when(userService.getUserByEmail(email)).thenReturn(student);
        when(courseService.getCourse(courseName)).thenReturn(course);
        when(assessmentService.getAssignment(course, student, assignmentId)).thenReturn(assignment);
        when(assessmentService.uploadSubmissionFile(mockFile, assignment, student)).thenReturn(expectedResponse);

        // send the request
        ResponseEntity<?> response = assessmentController.submitAssignment(courseName, assignmentId, mockFile);

        // assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());

        // verifying method calls
        verify(userService).getUserByEmail(email);
        verify(courseService).getCourse(courseName);
        verify(assessmentService).getAssignment(course, student, assignmentId);
        verify(assessmentService).uploadSubmissionFile(mockFile, assignment, student);
    }


    @Test
    void getSubmissionsList_successfulResponse() throws Exception {
        String email = "instructor@test.com";
        String courseName = "course-1";
        int assignmentId = 101;

        // creating test instructor
        Instructor instructor = new Instructor();
        instructor.setEmail(email);

        // test course
        Course course = new Course();
        course.setName(courseName);

        // test assignment
        Assignment assignment = new Assignment();
        assignment.setAssignmentId(assignmentId);
        assignment.setCourse(course);

        // mock submission DTOs
        AssignmentSubmissionDto submission1 = new AssignmentSubmissionDto(1, "Assignment 1", 1, "Student1", "submission1.pdf", "pdf", 85, true, LocalDateTime.now());
        AssignmentSubmissionDto submission2 = new AssignmentSubmissionDto(2, "Assignment 1", 2, "Student2", "submission2.pdf", "pdf", 0, false, LocalDateTime.now());
        List<AssignmentSubmissionDto> submissionsDto = List.of(submission1, submission2);

        // mimicking controller's calls
        when(authentication.getName()).thenReturn(email);
        when(userService.getUserByEmail(email)).thenReturn(instructor);
        when(courseService.getCourse(courseName)).thenReturn(course);
        when(assessmentService.getAssignment(course, instructor, assignmentId)).thenReturn(assignment);
        when(assessmentService.getSubmissions(assignment)).thenReturn(submissionsDto);

        // send the request
        ResponseEntity<?> response = assessmentController.getSubmissionsList(courseName, assignmentId);

        // assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(List.class, response.getBody());

        List<AssignmentSubmissionDto> returnedSubmissions = (List<AssignmentSubmissionDto>) response.getBody();
        assertEquals(2, returnedSubmissions.size());
        assertEquals("Student1", returnedSubmissions.get(0).getStudentName());
        assertEquals("submission1.pdf", returnedSubmissions.get(0).getFileName());

        verify(userService).getUserByEmail(email);
        verify(courseService).getCourse(courseName);
        verify(assessmentService).getAssignment(course, instructor, assignmentId);
        verify(assessmentService).getSubmissions(assignment);
    }

    @Test
    void getAssignmentSubmission_successfulResponse() throws Exception {
        String email = "instructor@test.com";
        String courseName = "course-1";
        int assignmentId = 101;
        int submissionId = 1001;

        // creating test instructor, course, and assignment
        Instructor instructor = new Instructor();
        instructor.setEmail(email);

        Course course = new Course();
        course.setName(courseName);

        Assignment assignment = new Assignment();
        assignment.setAssignmentId(assignmentId);
        assignment.setCourse(course);

        // test submission file
        byte[] submissionFile = "Test PDF Content".getBytes();

        // mimicking controller's calls
        when(authentication.getName()).thenReturn(email);
        when(userService.getUserByEmail(email)).thenReturn(instructor);
        when(courseService.getCourse(courseName)).thenReturn(course);
        when(assessmentService.getAssignment(course, instructor, assignmentId)).thenReturn(assignment);
        when(assessmentService.downloadSubmissionFile(assignment, submissionId)).thenReturn(submissionFile);

        // send the request
        ResponseEntity<?> response = assessmentController.getAssignmentSubmission(courseName, assignmentId, submissionId);

        // assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertArrayEquals(submissionFile, (byte[]) response.getBody());

        // verifications
        verify(userService).getUserByEmail(email);
        verify(courseService).getCourse(courseName);
        verify(assessmentService).getAssignment(course, instructor, assignmentId);
        verify(assessmentService).downloadSubmissionFile(assignment, submissionId);
    }


    @Test
    void gradeAssignment_success() {
        String email = "instructor@test.com";
        String courseName = "course-1";
        int assignmentId = 1;
        int submissionId = 100;
        int gradeValue = 85;

        // test user
        Instructor instructor = new Instructor();
        instructor.setEmail(email);

        // test course
        Course course = new Course();
        course.setName(courseName);

        // test assignment
        Assignment assignment = new Assignment();
        assignment.setAssignmentId(assignmentId);

        // test submission
        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setSubmissionId(submissionId);

        // mocked graded submissionn
        AssignmentSubmissionDto gradedSubmissionDto = new AssignmentSubmissionDto(
                submissionId, "Assignment 1", 1, "Student Name", "submission.pdf", "pdf", gradeValue, true, LocalDateTime.now()
        );

        Map<String, Integer> gradeMap = Map.of("grade", gradeValue);

        // controller calls
        when(authentication.getName()).thenReturn(email);
        when(userService.getUserByEmail(email)).thenReturn(instructor);
        when(courseService.getCourse(courseName)).thenReturn(course);
        when(assessmentService.getAssignment(course, instructor, assignmentId)).thenReturn(assignment);
        when(assessmentService.getSubmission(assignment, submissionId)).thenReturn(submission);
        when(assessmentService.setAssignmentGrade(submission, course, gradeMap)).thenReturn(gradedSubmissionDto);

        // sending the request
        ResponseEntity<?> response = assessmentController.gradeAssignment(courseName, assignmentId, submissionId, gradeMap);

        // assertions
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertInstanceOf(AssignmentSubmissionDto.class, response.getBody());

        AssignmentSubmissionDto returnedSubmission = (AssignmentSubmissionDto) response.getBody();
        assertEquals(gradedSubmissionDto.getSubmissionId(), returnedSubmission.getSubmissionId());
        assertEquals(gradedSubmissionDto.getGrade(), returnedSubmission.getGrade());
        assertTrue(returnedSubmission.isGraded());
    }


    @Test
    void getAssignmentGrade_success() {
        String email = "student@test.com";
        String courseName = "course-1";
        int assignmentId = 1;
        int expectedGrade = 85;

        // test user
        Student student = new Student();
        student.setEmail(email);

        // test course
        Course course = new Course();
        course.setName(courseName);

        // test assignment
        Assignment assignment = new Assignment();
        assignment.setAssignmentId(assignmentId);

        // expected grade map
        Map<String, Integer> gradeMap = Map.of("grade", expectedGrade);

        // mimicking controller calls
        when(authentication.getName()).thenReturn(email);
        when(userService.getUserByEmail(email)).thenReturn(student);
        when(courseService.getCourse(courseName)).thenReturn(course);
        when(assessmentService.getAssignment(course, student, assignmentId)).thenReturn(assignment);
        when(assessmentService.getAssignmentGrade(assignment, student)).thenReturn(expectedGrade);

        // sending the request
        ResponseEntity<?> response = assessmentController.getAssignmentGrade(courseName, assignmentId, gradeMap);

        // assertions
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());

        Map<String, Integer> responseBody = (Map<String, Integer>) response.getBody();
        assertEquals(expectedGrade, responseBody.get("grade"));
    }

}