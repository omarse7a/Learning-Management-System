package com.dev.LMS.service;

import com.dev.LMS.dto.AssignmentDto;
import com.dev.LMS.dto.AssignmentSubmissionDto;
import com.dev.LMS.dto.QuestionDto;
import com.dev.LMS.dto.QuizDto;
import com.dev.LMS.model.*;
import com.dev.LMS.repository.CourseRepository;
import com.dev.LMS.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Time;
import java.util.*;

@AllArgsConstructor
@Service
public class AssessmentService {
    private CourseRepository courseRepository;
    private UserRepository userRepository;
    private final String UPLOAD_DIR = "../../../../../resources/uploads/assignment-submissions/";
    public void createQuestion(String courseName , Question question ){
        Course course = courseRepository.findByName(courseName)
             .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseName));
        course.addQuestion(question);
        courseRepository.save(course);
    }
    // no need for course so I remove it
    public QuestionDto getQuestionById(String courseName, int questionId){
        Course course= courseRepository.findByName(courseName).
                orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseName));;
        List<Question> questions = course.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            Question temp = questions.get(i);
            if(temp.getId() == questionId ){
                return QuestionDto.toDto(temp);
            }
        }
        throw  new IllegalArgumentException("No question by this Id: "+questionId);
    }
    // change parameter Course-> CourseId
    public List<QuestionDto> getQuestions(String courseName){
        Course course = courseRepository.findByName(courseName).
                orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseName));
        List<Question> questionList = course.getQuestions();
        List<QuestionDto> questionDtoList = new ArrayList<>();
        for (int i = 0; i < questionList.size(); i++){
            questionDtoList.add(QuestionDto.toDto(questionList.get(i)));
        }
        if(questionList.isEmpty())
            throw new IllegalArgumentException("No questions available for this course.");
        return questionDtoList;
    }
    // change parameter Course-> CourseId
    public void createQuiz(String courseName , Quiz newQuiz){
        Course course = courseRepository.findByName(courseName)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseName));
        course.addQuiz(newQuiz);
        courseRepository.save(course);
    }
    // change parameter Course-> CourseId
    public QuizDto generateQuiz(String courseName, String quizTitle) {
        Course course = courseRepository.findByName(courseName)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseName));

        List<Question> allQuestions = new ArrayList<>(course.getQuestions());
        if (allQuestions.isEmpty()) {
            throw new IllegalStateException("No questions available for this course.");
        }
        List<Quiz> quizzes = course.getQuizzes();
        if(quizzes.isEmpty())
            throw new IllegalStateException("No quizzes available for "+courseName+" course");
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
        List<Question> questions1 = selectedQuestions;
        List<QuestionDto> questionDtos = new ArrayList<>();
        for (int i = 0; i < questions1.size(); i++) {
            questionDtos.add(QuestionDto.toDto(questions1.get(i)));
        }
        currentQuiz.setQuestions(selectedQuestions);
        course.setQuiz(currentQuiz);
        courseRepository.save(course);
        return QuizDto.toDto(currentQuiz);
    }
    public void submitQuiz(String courseName, String quizTitle,QuizSubmission quizSubmission,Student user){
        Course course = courseRepository.findByName(courseName)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseName));
        List<Quiz> quizzes = course.getQuizzes();
        if(quizzes.isEmpty())
            throw new IllegalStateException("No quizzes available for "+courseName+" course");
        quizSubmission.setStudent(user);
        Quiz currentQuiz = null;
        int index = 0;
        boolean isFound = false;
        for (int i = 0; i < quizzes.size(); i++) {
            currentQuiz = quizzes.get(i);
            if(currentQuiz.getQuizTitle().equals(quizTitle)){
                index = i;
                isFound = true;
                break;
            }
        }
        if(!isFound) {
            throw new IllegalStateException("This quiz dose not exit.");
        }
        if(quizSubmission.equals(null))
            throw new IllegalStateException("Your submission is empty.");
        SubmittedQuestion submittedQuestion = null;
        List<SubmittedQuestion> submittedQuestions= new ArrayList<>();
        for (int i = 0; i < quizSubmission.getSubmittedQuestions().size(); i++) {
            submittedQuestion = quizSubmission.getSubmittedQuestions().get(i);
            if (submittedQuestion.getStudentAnswer() == null) {
                System.out.println(submittedQuestion.getStudentAnswer());
                throw new IllegalStateException("Student answer cannot be null");
            }
            submittedQuestion.setSubmission(quizSubmission);
            submittedQuestions.add(submittedQuestion);
        }
        quizSubmission.setSubmittedQuestions(submittedQuestions);
        currentQuiz.addQuizSubmission(quizSubmission);
        quizSubmission.setQuiz(currentQuiz);
        quizzes.set(index,currentQuiz);
        course.setQuizzes(quizzes);
        courseRepository.save(course);

    }

    public void gradeQuiz(String quizTitle,String courseName){
        Course course = courseRepository.findByName(courseName)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseName));
        List<Quiz> quizzes = course.getQuizzes();
        if(quizzes.isEmpty()) throw new IllegalArgumentException("No quizzes available for "+courseName+" course");
        Quiz currentQuiz = null;
        boolean isFound = false;
        int index = 0;
        for (int i = 0; i < quizzes.size(); i++) {
            Quiz temp = quizzes.get(i);
            if(temp.getQuizTitle().equals(quizTitle)){
                index = i;
                currentQuiz = temp;
                isFound = true;
                break;
            }
        }
        if(!isFound)
            throw new IllegalStateException("This quiz dose not exit.");
        List<QuizSubmission> quizSubmissions = currentQuiz.getSubmissions();
        if(quizSubmissions.isEmpty())
            throw new IllegalStateException("There is no submission ");
        for (int i = 0; i < quizSubmissions.size(); i++) {
            QuizSubmission currentQuizSubmission = quizSubmissions.get(i);
            List<SubmittedQuestion> submittedQuestions = currentQuizSubmission.getSubmittedQuestions();
            int grade=0;
            for (int j = 0; j < submittedQuestions.size(); j++){
                SubmittedQuestion CurrentSubmittedQuestion =submittedQuestions.get(j);
                Question currentQuestion = CurrentSubmittedQuestion.getQuestion();
                if(CurrentSubmittedQuestion.getStudentAnswer().
                        equals(currentQuestion.getCorrectAnswer()))
                   grade++;
            }
            quizSubmissions.get(i).setGrade(grade);
            quizSubmissions.get(i).setGraded(true);
        }
        currentQuiz.setSubmissions(quizSubmissions);
        quizzes.set(index,currentQuiz);
        courseRepository.save(course);
    }
    public int getQuizGrade(String quizTitle,String courseName , Student user) {
        Course course = courseRepository.findByName(courseName)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseName));
        List<Quiz> quizzes = course.getQuizzes();
        if(quizzes.isEmpty()) throw new IllegalArgumentException("No quizzes available for "+courseName+" course");
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
        for (int i = 0; i < quizSubmissions.size(); i++) {
            if(quizSubmissions.get(i).getStudent().equals(user)){
                if(quizSubmissions.get(i).getGraded()){
                    return quizSubmissions.get(i).getGrade();
                }else{
                    throw new IllegalArgumentException("Not graded yet");
                }

            }
        }
        throw new IllegalStateException("There is no submission for this student: "+ user.getName());
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

    public List<AssignmentSubmissionDto> getSubmissions(Assignment assignment){
        List<AssignmentSubmissionDto> dtos = new ArrayList<>();
        List<AssignmentSubmisson> submissions = assignment.getSubmissions();
        for (AssignmentSubmisson submisson : submissions) {
            dtos.add(new AssignmentSubmissionDto(submisson));
        }
        return dtos;
    }

    public byte[] downloadSubmissionFile(Assignment assignment, int submissionId){ // String fileName
        // retrieving the assignment submission object by ID
        List<AssignmentSubmisson> submissions = assignment.getSubmissions();
        AssignmentSubmisson sub = new AssignmentSubmisson();
        for (AssignmentSubmisson submisson : submissions) {
            if(submisson.getSubmissionId() == submissionId){
                sub = submisson;
                break;
            }
        }
        // storing the file into a byte array
        String filePath = sub.getFilePath();
        try {
            byte[] submissionData = Files.readAllBytes(new File(filePath).toPath());
            return submissionData;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

