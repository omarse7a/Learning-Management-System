package com.dev.LMS;


import com.dev.LMS.controller.CourseController;
import com.dev.LMS.controller.UserController;
import com.dev.LMS.dto.*;
import com.dev.LMS.model.*;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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


    @Test
    void getMyCourses_Instructor_returnCreatedCourses() {
        // Arrange
        String email = "instructor@test.com";
        Instructor instructor = new Instructor();
        instructor.setName("Test Instructor");
        instructor.setEmail(email);

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

        Set<Course> courseList = new HashSet<>();
        courseList.add(course1);
        courseList.add(course2);

        when(authentication.getName()).thenReturn(email);
        when(userService.getUserByEmail(email)).thenReturn(instructor);
        when(courseService.getCreatedCourses(instructor)).thenReturn(courseList);

        // Act
        ResponseEntity<?> response = courseController.getMyCourses();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Set);

        Set<CourseDto> returnedCourses = (Set<CourseDto>) response.getBody();
        assertEquals(2, returnedCourses.size());
        assertTrue(returnedCourses.stream().anyMatch(dto -> dto.getCourseName().equals("Test Course")));
        assertTrue(returnedCourses.stream().anyMatch(dto -> dto.getCourseName().equals("Test Course Two")));
        verify(courseService).getCreatedCourses(instructor);
        verify(userService).getUserByEmail(email);
    }

    @Test
    void getMyCourses_Student_returnEnrolledCourses() {

        // Arrange
        String email = "instructor@test.com";
        Instructor instructor = new Instructor();
        instructor.setName("Test Instructor");
        instructor.setEmail(email);

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


        String student_email = "student@test.com";
        Student student = new Student();
        student.setName("Test Student");
        student.setEmail(student_email);
        course1.addStudent(student);
        course2.addStudent(student);

        Set<Course> courseList = new HashSet<>();
        courseList.add(course1);
        courseList.add(course2);

        when(authentication.getName()).thenReturn(student_email);
        when(userService.getUserByEmail(student_email)).thenReturn(student);
        when(courseService.getEnrolledCourses(student)).thenReturn(courseList);

        // Act
        ResponseEntity<?> response = courseController.getMyCourses();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Set);

        Set<CourseDto> returnedCourses = (Set<CourseDto>) response.getBody();
        assertEquals(2, returnedCourses.size());
        assertTrue(returnedCourses.stream().anyMatch(dto -> dto.getCourseName().equals("Test Course")));
        assertTrue(returnedCourses.stream().anyMatch(dto -> dto.getCourseName().equals("Test Course Two")));
        verify(courseService).getEnrolledCourses(student);
        verify(userService).getUserByEmail(student_email);
    }

    @Test
    void getLessonsOfCourse_returnListOfLessons() {
        // Arrange
        Instructor instructor = new Instructor();
        instructor.setName("Test Instructor");

        Course course = new Course();
        course.setName("Test Course");
        course.setDescription("This is Test Course");
        course.setDuration((float) 40);
        course.setInstructor(instructor);

        Lesson lesson1 = new Lesson();
        lesson1.setTitle("Test Lesson One");
        lesson1.setDescription("This is Test Lesson one");
        Lesson lesson2 = new Lesson();
        lesson2.setTitle("Test Lesson Two");
        lesson2.setDescription("This is Test Lesson two");

        course.addLesson(lesson1);
        course.addLesson(lesson2);

        when(courseService.getCourse(course.getName())).thenReturn(course);

        //Act
        ResponseEntity<?> response = courseController.getAllLessons(course.getName());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);

        List<LessonDto> returnedLessons = (List<LessonDto>) response.getBody();
        assertEquals(2, returnedLessons.size());
        assertTrue(returnedLessons.stream().anyMatch(dto -> dto.getTitle().equals("Test Lesson One")));
        assertTrue(returnedLessons.stream().anyMatch(dto -> dto.getTitle().equals("Test Lesson Two")));
        verify(courseService).getCourse(course.getName());


    }

    @Test
    void getLesson_InstructorOfCourse_ReturnDetailedLessonDto() {
        // Arrange
        String instructorEmail = "instructor@test.com";
        Instructor instructor = new Instructor();
        instructor.setName("Test Instructor");
        instructor.setEmail(instructorEmail);

        Course course = new Course();
        course.setName("Test Course");
        course.setDescription("This is Test Course");
        course.setDuration((float) 40);
        course.setInstructor(instructor);

        Lesson lesson = new Lesson();
        lesson.setTitle("Test Lesson");
        lesson.setDescription("This is a detailed description of the test lesson.");
        course.addLesson(lesson);

        when(courseService.getCourse(course.getName())).thenReturn(course);
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(instructorEmail);
        when(userService.getUserByEmail(instructorEmail)).thenReturn(instructor);
        when(courseService.getLessonbyId(course,lesson.getLesson_id())).thenReturn(lesson);

        // Act
        ResponseEntity<?> response = courseController.getLesson(course.getName(), lesson.getLesson_id());

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof DetailedLessonDto);

        DetailedLessonDto returnedLessonDto = (DetailedLessonDto) response.getBody();
        assertEquals(lesson.getTitle(), returnedLessonDto.getTitle());
        assertEquals(lesson.getDescription(), returnedLessonDto.getDescription());
        verify(courseService).getCourse(course.getName());
        verify(userService).getUserByEmail(instructorEmail);
        verify(courseService).getLessonbyId(course, lesson.getLesson_id());
    }


    @Test
    void addResource_Instructor_ReturnSuccessMessage() throws Exception {
        // Arrange
        String instructorEmail = "instructor@test.com";
        Instructor instructor = new Instructor();
        instructor.setName("Test Instructor");
        instructor.setEmail(instructorEmail);

        Course course = new Course();
        course.setName("Test Course");
        course.setDescription("This is Test Course");
        course.setDuration((float) 40);
        course.setInstructor(instructor);

        Lesson lesson = new Lesson();
        lesson.setTitle("Test Lesson");
        lesson.setDescription("This is a detailed description of the test lesson.");
        course.addLesson(lesson);

        // Create a mock MultipartFile
        MockMultipartFile file = new MockMultipartFile("file", "testfile.txt", "text/plain", "Test file content".getBytes());

        when(courseService.getCourse(course.getName())).thenReturn(course);
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(instructorEmail);
        when(userService.getUserByEmail(instructorEmail)).thenReturn(instructor);
        when(courseService.addLessonResource(course, lesson.getLesson_id(), file)).thenReturn("Resource added successfully");

        // Act
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/course/{courseName}/lessons/{lessonId}/add-resource", course.getName(), lesson.getLesson_id())
                        .file(file)
                        .param("file", "testfile.txt"))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String responseMessage = result.getResponse().getContentAsString();
        assertEquals("Resource added successfully", responseMessage);
        verify(courseService).getCourse(course.getName());
        verify(userService).getUserByEmail(instructorEmail);
        verify(courseService).addLessonResource(course, lesson.getLesson_id(), file);
    }

    @Test
    void getAllResources_Instructor_ReturnListOfResources() throws Exception {
        // Arrange
        String instructorEmail = "instructor@test.com";
        Instructor instructor = new Instructor();
        instructor.setName("Test Instructor");
        instructor.setEmail(instructorEmail);

        Course course = new Course();
        course.setName("Test Course");
        course.setDescription("This is Test Course");
        course.setDuration(40f);

        Lesson lesson = new Lesson();
        lesson.setTitle("Test Lesson");
        lesson.setDescription("This is a detailed description of the test lesson.");
        course.addLesson(lesson);

        // Create some mock LessonResource objects for the lesson
        LessonResource resource1 = new LessonResource();
        resource1.setFile_name("Resource 1");
        resource1.setFile_type("pdf");
        LessonResource resource2 = new LessonResource();
        resource2.setFile_name("Resource 2");
        resource2.setFile_type("mp4");

        // Add resources to the lesson
        lesson.addLessonResource(resource1);
        lesson.addLessonResource(resource2);

        // Create corresponding LessonResourceDto objects
        LessonResourceDto resourceDto1 = new LessonResourceDto(resource1);
        LessonResourceDto resourceDto2 = new LessonResourceDto(resource2);

        List<LessonResourceDto> resources = Arrays.asList(resourceDto1, resourceDto2);

        // Mock the necessary services
        when(courseService.getCourse(course.getName())).thenReturn(course);
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(instructorEmail);
        when(userService.getUserByEmail(instructorEmail)).thenReturn(instructor);
        when(courseService.getLessonResources(course, instructor, lesson.getLesson_id())).thenReturn(resources);

        // Act
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/course/{courseName}/lessons/{lessonId}/resources", course.getName(), lesson.getLesson_id()))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String responseContent = result.getResponse().getContentAsString();
        assertTrue(responseContent.contains("Resource 1"));
        assertTrue(responseContent.contains("Resource 2"));
        verify(courseService).getCourse(course.getName());
        verify(userService).getUserByEmail(instructorEmail);
        verify(courseService).getLessonResources(course, instructor, lesson.getLesson_id());
    }


}
