package com.dev.LMS.service;

import com.dev.LMS.dto.AssignmentDto;
import com.dev.LMS.model.*;
import com.dev.LMS.repository.CourseRepository;
import com.dev.LMS.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.sql.Time;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class AssessmentService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    private final String UPLOAD_DIR = "../../../../../resources/uploads/assignment-submissions/";

    public void createQuestion(String courseName , Question question ){
        Course course = courseRepository.findByName(courseName).orElse(null);
        List<Question> questions = course.getQuestions();
        questions.add(question);
        course.setQuestions(questions);
    }
    // no need for course so I remove it
    public Question getQuestionById(String courseName, int questionId){
        Course course= courseRepository.findByName(courseName).orElse(null);
        List<Question> questions = course.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            Question temp = questions.get(i);
            if(temp.getId() == questionId ){
                return temp;
            }
        }
        return null;
    }
    // change parameter Course-> CourseId
    public List<Question> getQuestions(String courseName){
        Course course = courseRepository.findByName(courseName).orElse(null);
        List<Question> questionList = course.getQuestions();
        return questionList;
    }
    // change parameter Course-> CourseId
    public void createQuiz(String courseName , Quiz newQuiz){
        Course course = courseRepository.findByName(courseName)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseName));
        List<Quiz> quizzes = course.getQuizzes();
        quizzes.add(newQuiz);
        course.setQuizzes(quizzes);
    }
    // change parameter Course-> CourseId
    public Quiz generateQuiz(String courseName, String quizTitle) {
        Course course = courseRepository.findByName(courseName)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseName));

        List<Question> allQuestions = course.getQuestions();
        if (allQuestions.isEmpty()) {
            throw new IllegalStateException("No questions available for this course.");
        }
        List<Quiz> quizzes = course.getQuizzes();
        if(quizzes.isEmpty())
            throw new IllegalStateException("No quizzes available for this course.");
        Quiz currentQuiz = null;
        boolean isFound = false;
        for (int i = 0; i < quizzes.size(); i++) {
            Quiz temp = quizzes.get(i);
            if(temp.getQuizTitle().equals(quizTitle)){
                currentQuiz = temp;
                isFound = true;
                break;
            }
        }
        if(!isFound)
            throw new IllegalStateException("This quiz dose not exit.");

        Collections.shuffle(allQuestions);
        List<Question> selectedQuestions = allQuestions.subList(0, Math.min(10, allQuestions.size()));
        currentQuiz.setQuestions(selectedQuestions);
        return null;
    }

    public void submitQuiz(String courseName, String quizTitle,QuizSubmission quizSubmission){
        Course course = courseRepository.findByName(courseName)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseName));
        List<Quiz> quizzes = course.getQuizzes();
        if(quizzes.isEmpty())
            throw new IllegalStateException("No quizzes available for this course.");
        Quiz currentQuiz = null;
        boolean isFound = false;
        for (int i = 0; i < quizzes.size(); i++) {
            Quiz temp = quizzes.get(i);
            if(temp.getQuizTitle().equals(quizTitle)){
                currentQuiz = temp;
                isFound = true;
                break;
            }
        }
        if(!isFound)
            throw new IllegalStateException("This quiz dose not exit.");
        List<QuizSubmission> quizSubmissions = currentQuiz.getSubmissions();
        quizSubmissions.add(quizSubmission);

    }

    public void gradeQuiz(Quiz quiz){

    }
    public int getQuizGrade(Quiz quiz) {
        return 0;
    }
    public  List<Assignment> getAssignmentSub(Assignment assignment){
        List<Assignment> assignmentList = null;
        return assignmentList;
    }

    public boolean addAssignment(Course course, Assignment assignment, Instructor instructor){
        Set<Course> instructorCourses = instructor.getCreatedCourses();
        if(instructorCourses.contains(course)){
            course.addAssignment(assignment);
            courseRepository.save(course);
            return true;
        }
        return false;
    }

    public List<AssignmentDto> getAssignments(Course course, User user){
        List<Assignment> assignments = null;
        if(user instanceof Instructor){
            Instructor instructor = (Instructor) user;
            Set<Course> instructorCourses = instructor.getCreatedCourses();
            if(instructorCourses.contains(course))
                assignments = course.getAssignments();
        } else {
            Student student = (Student) user;
            Set<Course> instructorCourses = student.getEnrolled_courses();
            if (instructorCourses.contains(course))
                assignments = course.getAssignments();
        }
        List<AssignmentDto> assignmentDtos = new ArrayList<>();
        for (Assignment assignment : assignments) {
            assignmentDtos.add(new AssignmentDto(assignment));
        }
        return assignmentDtos;
    }

    public Assignment getAssignment(Course course, User user, int assignmentId){
        List<Assignment> assignments = List.of();
        if(user instanceof Instructor){
            Instructor instructor = (Instructor) user;
            Set<Course> instructorCourses = instructor.getCreatedCourses();
            if(instructorCourses.contains(course))
                assignments = course.getAssignments();
        } else {
            Student student = (Student) user;
            Set<Course> instructorCourses = student.getEnrolled_courses();
            if (instructorCourses.contains(course))
                assignments = course.getAssignments();
        }
        for (Assignment assignment : assignments) {
            if(assignment.getAssignmentId() == assignmentId)
                return assignment;
        }
        return null;
    }

    public String uploadSubmissionFile(MultipartFile file, Assignment assignment, Student student){
        String filePath = UPLOAD_DIR + file.getOriginalFilename();

        // database part
        AssignmentSubmisson a = new AssignmentSubmisson();
        a.setFileName(file.getOriginalFilename());
        a.setFileType(file.getContentType());
        a.setFilePath(filePath);
        a.setAssignment(assignment);
        // sets the submission's student and adds the submission to the student's submissions list
        student.addSubmission(a);
        // saving using user repo!!!
        userRepository.save(student);

        // storing in the actual file system
        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "file successfully uploaded to " + filePath;
    }

    public void downloadSubmissionFile(String fileName){

    }

    public void setAssignmentGrade(Assignment assignment) {

    }
    public int getAssignmentGrade(Assignment assignment) {
        return 0;
    }

    public List<Lesson> getLessonsAttended(Course course){
        List<Lesson> lessonList=null;
        return lessonList;
    }
    public String send_feedback(Student student) {
        return "11";
    }
}

