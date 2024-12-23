package com.dev.LMS;


import com.dev.LMS.controller.CourseController;
import com.dev.LMS.controller.UserController;
import com.dev.LMS.dto.CourseDto;
import com.dev.LMS.dto.RegisterDto;
import com.dev.LMS.dto.UpdateProfileDto;
import com.dev.LMS.model.Course;
import com.dev.LMS.model.Instructor;
import com.dev.LMS.model.Student;
import com.dev.LMS.model.User;
import com.dev.LMS.service.CourseService;
import com.dev.LMS.service.UserService;
import com.dev.LMS.util.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CourseControllerTest {
    @Mock
    private UserService userService;

    @Mock
    private CourseService courseService;

    @Mock
    private UserFactory userFactory;

    @InjectMocks
    private CourseController courseController;

    @Mock SecurityContext securityContext;

    @Mock Authentication authentication;

    @BeforeEach void setup() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }



    @Test
    void createCourse_whenValidInstructor_shouldReturnCreatedCourse() {
        // Arrange
        String email = "instructor@test.com";
        Instructor instructor = new Instructor();
        instructor.setEmail(email);

        Course course = new Course();
        course.setName("Test Course");
        course.setDescription("Test Description");
        course.setDuration((float) 40);

        Course createdCourse = new Course();
        createdCourse.setName("Test Course");
        createdCourse.setDescription("Test Description");
        createdCourse.setDuration((float) 40);
        createdCourse.setInstructor(instructor);

        when(authentication.getName()).thenReturn(email);
        when(userService.getUserByEmail(email)).thenReturn(instructor);
        when(courseService.createCourse(any(Course.class), any(Instructor.class))).thenReturn(createdCourse);

        // Act
        ResponseEntity<?> response = courseController.createCourse(course);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof CourseDto);

        CourseDto returnedCourseDto = (CourseDto) response.getBody();
        assertEquals(createdCourse.getName(), returnedCourseDto.getCourseName());
        assertEquals(createdCourse.getDescription(), returnedCourseDto.getCourseDescription());
        assertEquals(createdCourse.getInstructor().getName(), returnedCourseDto.getInstructorName());


    }

    @Test
    void createCourse_whenStudent_shouldReturnForbidden() {
        // Arrange
        String email = "student@test.com";
        User student = new Student();
        student.setEmail(email);
        Course course = new Course();
        course.setName("Test Course");

        when(authentication.getName()).thenReturn(email);
        when(userService.getUserByEmail(email)).thenReturn(student);

        // Act
        ResponseEntity<?> response = courseController.createCourse(course);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("You are not authorized to create a course", response.getBody());
        verify(userService).getUserByEmail(email);
        verify(courseService, never()).createCourse(any(Course.class), any(Instructor.class));
    }

    @Test
    void getCourse_courseExists_returnCourse() {
        // Arrange
        Course course = new Course();
        course.setName("Test Course");
        course.setDescription("Test Description");
        course.setDuration((float) 40);

        Instructor instructor = new Instructor();
        instructor.setName("Test Instructor");
        course.setInstructor(instructor);

        when(courseService.getCourse("Test Course")).thenReturn(course);

        // Act
        ResponseEntity<?> response = courseController.getCourse("Test Course");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof CourseDto);

        CourseDto returnedCourseDto = (CourseDto) response.getBody();
        assertEquals(course.getName(), returnedCourseDto.getCourseName());
        assertEquals(course.getDescription(), returnedCourseDto.getCourseDescription());
        assertEquals(course.getInstructor().getName(), returnedCourseDto.getInstructorName());
        verify(courseService).getCourse("Test Course");
    }

    @Test
    void getCourse_courseNotFound_returnBadRequest() {
        // Arrange
        when(courseService.getCourse("NonExistentCourse")).thenReturn(null);

        // Act
        ResponseEntity<?> response = courseController.getCourse("NonExistentCourse");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Course not found", response.getBody());
        verify(courseService).getCourse("NonExistentCourse");
    }

    @Test
    void getCourseById_courseExist_returnCourse(){
        // Arrange
        Course course = new Course();
        course.setName("Test Course");
        course.setDescription("Test Description");
        course.setDuration((float) 40);

        Instructor instructor = new Instructor();
        instructor.setName("Test Instructor");
        course.setInstructor(instructor);

        when(courseService.getCourseById(1)).thenReturn(course);

        // Act
        ResponseEntity<?> response = courseController.getCourse(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof CourseDto);

        CourseDto returnedCourseDto = (CourseDto) response.getBody();
        assertEquals(course.getName(), returnedCourseDto.getCourseName());
        assertEquals(course.getDescription(), returnedCourseDto.getCourseDescription());
        assertEquals(course.getInstructor().getName(), returnedCourseDto.getInstructorName());
        verify(courseService).getCourseById(1);

    }

    @Test
    void getCourseById_courseNotFound_returnBadRequest() {
        // Arrange
        when(courseService.getCourse("NonExistentCourse")).thenReturn(null);

        // Act
        ResponseEntity<?> response = courseController.getCourse("NonExistentCourse");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Course not found", response.getBody());
        verify(courseService).getCourse("NonExistentCourse");
    }


    @Test
    void getAllCourses(){
        // Arrange
        Instructor instructor = new Instructor();
        instructor.setName("Test Instructor");

        Course course1 = new Course();
        course1.setName("Test Course");
        course1.setDescription("This is Test Course");
        course1.setDuration((float) 40);
        course1.setInstructor(instructor);

        Course course2 = new Course();
        course2.setName("Test Course Two");
        course2.setDescription("This is Test Course Two");
        course2.setDuration((float) 30);
        course2.setInstructor(instructor);

        List<Course> courseList = new ArrayList<>();
        courseList.add(course1);
        courseList.add(course2);
        when(courseService.getAllCourses()).thenReturn(courseList);

        // Act
        ResponseEntity<?> response = courseController.getAllCourses();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);

        List<CourseDto> returnedCourses = (List<CourseDto>) response.getBody();
        assertEquals(2, returnedCourses.size());
        assertEquals("Test Course", returnedCourses.get(0).getCourseName());
        assertEquals("Test Course Two", returnedCourses.get(1).getCourseName());
        verify(courseService).getAllCourses();
    }





}
