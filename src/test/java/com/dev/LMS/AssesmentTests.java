package com.dev.LMS;

import com.dev.LMS.controller.AssessmentController;
import com.dev.LMS.dto.QuizSubmissionDto;
import com.dev.LMS.exception.CourseNotFoundException;
import com.dev.LMS.model.*;
import com.dev.LMS.service.AssessmentService;
import com.dev.LMS.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AssesmentTests {

    @InjectMocks
    AssessmentController assessmentController;

    @Mock
    AssessmentService assessmentService;

    @Mock
    UserService userService;

    @Mock
    SecurityContext securityContext;

    @Mock
    Authentication authentication;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testCreateQuiz_UserNotFound() {
        // Mocking
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userService.getUserByEmail("test@example.com")).thenReturn(null);

        // Act
        ResponseEntity<?> response = assessmentController.createQuiz("courseName", new Quiz());

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("User not found, Please register or login first", response.getBody());
    }

    @Test
    public void testCreateQuiz_UserNotAuthorized() {
        // Mocking
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        User user = new Student();
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);

        // Act
        ResponseEntity<?> response = assessmentController.createQuiz("courseName", new Quiz());

        // Assert
        assertEquals(403, response.getStatusCodeValue());
        assertEquals("You are not authorized to create an assignment", response.getBody());
    }

    @Test
    public void testCreateQuiz_Success() {
        // Mocking
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        Instructor instructor = new Instructor();
        when(userService.getUserByEmail("test@example.com")).thenReturn(instructor);

        doNothing().when(assessmentService).createQuiz(anyString(), any(Quiz.class));

        // Act
        ResponseEntity<?> response = assessmentController.createQuiz("courseName", new Quiz());

        // Assert
        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    public void testCreateQuiz_InternalServerError() {
        // Mocking
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        Instructor instructor = new Instructor();
        when(userService.getUserByEmail("test@example.com")).thenReturn(instructor);

        doThrow(new RuntimeException("Database error")).when(assessmentService).createQuiz(anyString(), any(Quiz.class));

        // Act
        ResponseEntity<?> response = assessmentController.createQuiz("courseName", new Quiz());

        // Assert
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Database error", response.getBody());
    }

    @Test
    public void testAddQuestion_UserNotFound() {
        // Mocking
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userService.getUserByEmail("test@example.com")).thenReturn(null);

        // Act
        ResponseEntity<?> response = assessmentController.addQuestion("courseName", new Question());

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("User not found, Please register or login first", response.getBody());
    }


    @Test
    public void testAddQuestion_Success() {
        // Mocking
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        Instructor instructor = new Instructor();
        when(userService.getUserByEmail("test@example.com")).thenReturn(instructor);

        doNothing().when(assessmentService).createQuestion(anyString(), any(Question.class));

        // Act
        ResponseEntity<?> response = assessmentController.addQuestion("courseName", new Question());

        // Assert
        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    public void testAddQuestion_InternalServerError() {
        // Mocking
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        Instructor instructor = new Instructor();
        when(userService.getUserByEmail("test@example.com")).thenReturn(instructor);

        doThrow(new RuntimeException("Database error")).when(assessmentService).createQuestion(anyString(), any(Question.class));

        // Act
        ResponseEntity<?> response = assessmentController.addQuestion("courseName", new Question());

        // Assert
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Database error", response.getBody());
    }
    @Test
    public void testTakeQuiz_Success() {
        // Mocking
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        Student student = new Student();
        when(userService.getUserByEmail("test@example.com")).thenReturn(student);

        QuizSubmissionDto quizSubmissionDto = new QuizSubmissionDto();
        when(assessmentService.generateQuiz("courseName", "quizName", student)).thenReturn(quizSubmissionDto);

        // Act
        ResponseEntity<?> response = assessmentController.takeQuiz("courseName", "quizName");

        // Assert
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(quizSubmissionDto, response.getBody());
    }

    @Test
    public void testTakeQuiz_CourseNotFound() {
        // Mocking
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        Student student = new Student();
        when(userService.getUserByEmail("test@example.com")).thenReturn(student);

        when(assessmentService.generateQuiz("courseName", "quizName", student))
                .thenThrow(new CourseNotFoundException("Course not found"));

        // Act
        ResponseEntity<?> response = assessmentController.takeQuiz("courseName", "quizName");

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Course not found", response.getBody());
    }

    @Test
    public void testTakeQuiz_InternalServerError() {
        // Mocking
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        Student student = new Student();
        when(userService.getUserByEmail("test@example.com")).thenReturn(student);

        when(assessmentService.generateQuiz("courseName", "quizName", student))
                .thenThrow(new RuntimeException("Internal server error"));

        // Act
        ResponseEntity<?> response = assessmentController.takeQuiz("courseName", "quizName");

        // Assert
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Internal server error", response.getBody());
    }
    @Test
    public void testGetGrade_Success() {
        // Mocking
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        Student student = new Student();
        when(userService.getUserByEmail("test@example.com")).thenReturn(student);

        int expectedGrade = 85;
        when(assessmentService.getQuizGrade("quizName", "courseName", student)).thenReturn(expectedGrade);

        // Act
        ResponseEntity<?> response = assessmentController.getGrade("courseName", "quizName");

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedGrade, response.getBody());
    }

    @Test
    public void testGetGrade_InternalServerError() {
        // Mocking
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        Student student = new Student();
        when(userService.getUserByEmail("test@example.com")).thenReturn(student);

        when(assessmentService.getQuizGrade("quizName", "courseName", student))
                .thenThrow(new RuntimeException("Internal server error"));

        // Act
        ResponseEntity<?> response = assessmentController.getGrade("courseName", "quizName");

        // Assert
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Internal server error", response.getBody());
    }
}
