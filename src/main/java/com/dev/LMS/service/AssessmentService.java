package com.dev.LMS.service;

import com.dev.LMS.dto.*;
import com.dev.LMS.model.*;
import com.dev.LMS.repository.CourseRepository;
import com.dev.LMS.repository.QuizSubmissionRepositry;
import com.dev.LMS.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContextException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Service
public class AssessmentService {
    private CourseRepository courseRepository;
    private UserRepository userRepository;
    private QuizSubmissionRepositry quizSubmissionRepositry;
    private NotificationService notificationService;
    private EmailService emailService;
    @Value("${file.upload.base-path.assignment-submissions}")
    private String UPLOAD_DIR;

    AssessmentService(CourseRepository courseRepository, UserRepository userRepository, QuizSubmissionRepositry quizSubmissionRepositry, NotificationService NotificationService, EmailService EmailService){
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.quizSubmissionRepositry = quizSubmissionRepositry;
        this.notificationService =  NotificationService;
        this.emailService = EmailService;
    }

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

        Notification notificationMessage = notificationService.createNotification("A new quiz " + newQuiz.getQuizTitle() + " was added to your course work" );
        Set<Student> enrolled_students = course.getEnrolled_students();
        String subject = "New Quiz was added";
        String content = "A new quiz added " + newQuiz.getQuizTitle() + "in Course: " + course.getName();
        for (Student student : enrolled_students) {
            notificationService.addNotifcationStudent(notificationMessage, student);
            emailService.sendEmail(
                    student.getEmail(),
                    student.getName(),
                    subject,
                    content,
                    course.getInstructor().getName()
            );
        }

        courseRepository.save(course);
    }
    // change parameter Course-> CourseId
    public QuizSubmissionDto generateQuiz(String courseName, String quizTitle , Student student) {
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
        int index = 0;
        for (int i = 0; i < quizzes.size(); i++) {
            Quiz temp = quizzes.get(i);
            if(temp.getQuizTitle().equals(quizTitle)){
                currentQuiz = temp;
                isFound = true;
                index = i;
                break;
            }
        }
        if(!isFound)
            throw new IllegalStateException("This quiz dose not exit.");
        Collections.shuffle(allQuestions);
        List<Question> selectedQuestions = allQuestions.subList(0, Math.min(2, allQuestions.size()));
        QuizSubmission quizSubmission = new QuizSubmission();
        quizSubmission.setQuestions(selectedQuestions);
        for(Question q: quizSubmission.getQuestions()){
            q.addSubmission(quizSubmission);
        }
        quizSubmission.setSubmittedQuestions(new ArrayList<>());
        quizSubmission.setGrade(0);
        quizSubmission.setStudent(student);
        student.addQuizSubmission(quizSubmission);
        quizSubmission.setQuiz(currentQuiz);
        currentQuiz.addQuizSubmission(quizSubmission);
        course.setQuiz(currentQuiz);
        quizSubmissionRepositry.save(quizSubmission);
        courseRepository.save(course);
        System.out.println(QuestionDto.listToDto(course.getQuizzes().getFirst().getSubmissions().getFirst().getQuestions())+"\n\n\n\n\n\n\n\n\n");
        return QuizSubmissionDto.toDto(quizSubmission);
    }
    public void submitQuiz(String courseName, String quizTitle,List<SubmittedQuestion> studentSubmittedQuestions,Student student){
        Course course = courseRepository.findByName(courseName)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseName));
        System.out.println(QuestionDto.listToDto(course.getQuizzes().getFirst().getSubmissions().getFirst().getQuestions())+"\n\n\n\n\n\n\n\n\n");
        List<Quiz> quizzes = course.getQuizzes();
        if(quizzes.isEmpty())
            throw new IllegalStateException("No quizzes available for "+courseName+" course");
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
        List<QuizSubmission> quizSubmissions = course.getQuizzes().get(index).getSubmissions();
        QuizSubmission quizSubmission = null;
        for (QuizSubmission q : quizSubmissions){
            if(q.getStudent().getId() == student.getId())
                quizSubmission = q;
        }
        if(quizSubmission == null)
            throw new IllegalStateException("there is no submission.");
        if(studentSubmittedQuestions == null || studentSubmittedQuestions.isEmpty())
            throw new IllegalStateException("Your submission is empty.");
        if(quizSubmission.getQuestions().isEmpty())
            throw new IllegalStateException("Question is empty.");
        SubmittedQuestion submittedQuestion = null;
        List<SubmittedQuestion> submittedQuestions= new ArrayList<>();
        for (int i = 0; i < studentSubmittedQuestions.size(); i++) {
            submittedQuestion = studentSubmittedQuestions.get(i);
            if (submittedQuestion.getStudentAnswer() == null) {
                System.out.println(submittedQuestion.getStudentAnswer());
                throw new IllegalStateException("Student answer cannot be null");
            }
            submittedQuestion.setSubmission(quizSubmission);
            submittedQuestion.setQuestion(quizSubmission.getQuestions().get(i));
            submittedQuestions.add(submittedQuestion);
        }
        quizSubmission.setSubmittedQuestions(submittedQuestions);
        currentQuiz.addQuizSubmission(quizSubmission);
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
        }
        currentQuiz.setSubmissions(quizSubmissions);
        quizzes.set(index,currentQuiz);
        courseRepository.save(course);
    }
    public int getQuizGrade(String quizTitle,String courseName , Student user) {
        gradeQuiz(quizTitle,courseName);
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
                return quizSubmissions.get(i).getGrade();
            }
        }
        throw new IllegalStateException("There is no submission for this student: "+ user.getName());
    }
    public  List<Assignment> getAssignmentSub(Assignment assignment){
        List<Assignment> assignmentList = null;
        return assignmentList;
    }


    public AssignmentDto addAssignment(Course course, Assignment assignment, Instructor instructor){
        Set<Course> instructorCourses = instructor.getCreatedCourses();
        if(instructorCourses.contains(course)){
            course.addAssignment(assignment);

            Notification notificationMessage = notificationService.createNotification("A new assignment " + assignment.getTitle() + " was added to your course work" );
            Set<Student> enrolled_students = course.getEnrolled_students();
            String subject = "New Assignment was added";
            String content = "A new assignment added " + assignment.getTitle() + "in Course: " + course.getName();
            for (Student student : enrolled_students) {
                notificationService.addNotifcationStudent(notificationMessage, student);
                emailService.sendEmail(
                        student.getEmail(),
                        student.getName(),
                        subject,
                        content,
                        course.getInstructor().getName()
                );
            }

            courseRepository.save(course);
            return new AssignmentDto(assignment);
        }
        throw new IllegalStateException("You are not authorized to add assignments to this course");
    }

    public List<AssignmentDto> getAssignments(Course course, User user){
        List<Assignment> assignments = List.of();
        if(user instanceof Instructor){
            Instructor instructor = (Instructor) user;
            Set<Course> instructorCourses = instructor.getCreatedCourses();
            if(instructorCourses.contains(course))
                assignments = course.getAssignments();
            else
                throw new IllegalStateException("You are not the instructor of this course");
        } else {
            Student student = (Student) user;
            Set<Course> studentCourses = student.getEnrolled_courses();
            if (studentCourses.contains(course))
                assignments = course.getAssignments();
            else
                throw new IllegalStateException("You are not enrolled in this course");
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
            else
                throw new ApplicationContextException("You are not the instructor of this course");
        } else {
            Student student = (Student) user;
            Set<Course> studentCourses = student.getEnrolled_courses();
            if (studentCourses.contains(course))
                assignments = course.getAssignments();
            else
                throw new ApplicationContextException("You are not enrolled in this course");
        }
        for (Assignment assignment : assignments) {
            if(assignment.getAssignmentId() == assignmentId)
                return assignment;
        }
        throw new IllegalStateException("Assignment not found");
    }

    public String uploadSubmissionFile(MultipartFile file, Assignment assignment, Student student){
        String filePath = UPLOAD_DIR + file.getOriginalFilename();

        // database part
        AssignmentSubmission a = new AssignmentSubmission();
        a.setFileName(file.getOriginalFilename());
        a.setFileType(file.getContentType());
        a.setFilePath(filePath);
        a.setAssignment(assignment);
      
        // sets the submission's student and adds the submission to the student's submissions list
        student.addAssignmentSubmission(a);
        // saving through user repo
        userRepository.save(student);

        // storing in the actual file system
        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Unable to store the file at " + filePath);
        }
        return "file successfully uploaded to " + filePath;
    }

    public List<AssignmentSubmissionDto> getSubmissions(Assignment assignment){
        List<AssignmentSubmissionDto> dtos = new ArrayList<>();
        List<AssignmentSubmission> submissions = assignment.getSubmissions();
        for (AssignmentSubmission submisson : submissions) {
            dtos.add(new AssignmentSubmissionDto(submisson));
        }
        return dtos;
    }

    public AssignmentSubmission getSubmission(Assignment assignment, int submissionId){
        List<AssignmentSubmission> submissions = assignment.getSubmissions();
        for (AssignmentSubmission submission : submissions) {
            if(submission.getSubmissionId() == submissionId){
                return submission;
            }
        }
        throw new IllegalStateException("Submission not found");
    }

    public byte[] downloadSubmissionFile(Assignment assignment, int submissionId){ // String fileName
        // retrieving the assignment submission object by ID
        List<AssignmentSubmission> submissions = assignment.getSubmissions();
        AssignmentSubmission sub = new AssignmentSubmission();
        for (AssignmentSubmission submisson : submissions) {
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
            throw new RuntimeException("Unable to load the file from " + filePath);
        }
    }

    public AssignmentSubmissionDto setAssignmentGrade(AssignmentSubmission a, Course course, Map<String, Integer> gradeMap) {
        a.setGrade(gradeMap.get("grade"));
        a.setGraded(true);

        Notification notificationMessage = notificationService.createNotification("Your submission to " + a.getAssignment().getTitle() + " got graded " + " in Course: " + course.getName());
        Student student = a.getStudent();
        notificationService.addNotifcationStudent(notificationMessage, student);

        String subject = "Assignment got Graded";
        String content = "Your submission to " + a.getAssignment().getTitle() + " got graded " + " in Course: " + course.getName();

        emailService.sendEmail(
                student.getEmail(),
                student.getName(),
                subject,
                content,
                course.getInstructor().getName()
        );


        courseRepository.save(course);
        return new AssignmentSubmissionDto(a);
    }
    public int getAssignmentGrade(Assignment assignment, Student student) {
        List<AssignmentSubmission> studentSubmissions = student.getAssignmentSubmissions();
        for (AssignmentSubmission submission : studentSubmissions) {
            if(submission.getAssignment().equals(assignment)){
                if(submission.isGraded())
                    return submission.getGrade();
                else
                    throw new ApplicationContextException("Your submission wasn't graded yet.");
            }
        }
        throw new IllegalStateException("You have no submissions for this assignment .");
    }

}

