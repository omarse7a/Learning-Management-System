package com.dev.LMS.service;

import com.dev.LMS.model.*;
import com.dev.LMS.repository.CourseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@AllArgsConstructor
@Service
public class AssessmentService {
    private CourseRepository courseRepository;
    public void createQuestion(String courseName , Question question ){
        Course course = courseRepository.findByName(courseName)
             .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseName));
        course.addQuestion(question);
        courseRepository.save(course);
    }
    // no need for course so I remove it
    public Question getQuestionById(String courseName, int questionId){
        Course course= courseRepository.findByName(courseName).
                orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseName));;
        List<Question> questions = course.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            Question temp = questions.get(i);
            if(temp.getId() == questionId ){
                return temp;
            }
        }
        throw  new IllegalArgumentException("No question by this Id: "+questionId);
    }
    // change parameter Course-> CourseId
    public List<Question> getQuestions(String courseName){
        Course course = courseRepository.findByName(courseName).
                orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseName));
        List<Question> questionList = course.getQuestions();
        if(questionList.isEmpty())
            throw new IllegalArgumentException("No questions available for this course.");
        return questionList;
    }
    // change parameter Course-> CourseId
    public void createQuiz(String courseName , Quiz newQuiz){
        Course course = courseRepository.findByName(courseName)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseName));
        course.addQuiz(newQuiz);
        courseRepository.save(course);
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
        currentQuiz.setQuestions(selectedQuestions);
        return null;
    }
    public void submitQuiz(String courseName, String quizTitle,QuizSubmission quizSubmission,Student user){
        Course course = courseRepository.findByName(courseName)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseName));
        List<Quiz> quizzes = course.getQuizzes();
        if(quizzes.isEmpty())
            throw new IllegalStateException("No quizzes available for "+courseName+" course");
        quizSubmission.setStudent(user);
        Quiz currentQuiz = null;
        boolean isFound = false;
        for (int i = 0; i < quizzes.size(); i++) {
            Quiz temp = quizzes.get(i);
            if(temp.getQuizTitle().equals(quizTitle)){
                currentQuiz.addQuizSubmission(quizSubmission);
                quizzes.set(i,currentQuiz);
                courseRepository.save(course);
                isFound = true;
                break;
            }
        }
        if(!isFound)
            throw new IllegalStateException("This quiz dose not exit.");
    }


    public void addAssignment(Course course){

    }
    public void getAssignment(Course course){

    }
    public void handAssignment(int assignmentId){

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

        return 0;
    }
    public  List<Assignment> getAssignmentSub(Assignment assignment){
        List<Assignment> assignmentList = null;
        return assignmentList;
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

