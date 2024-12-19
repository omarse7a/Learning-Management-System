package com.dev.LMS.service;

import com.dev.LMS.model.*;
import com.dev.LMS.repository.CourseRepository;
import jakarta.validation.constraints.Null;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class AssessmentService {
    private CourseRepository courseRepositry;

    public void createQuestion(int courseId , Question question ){
        Course course = courseRepositry.findById(courseId).orElse(null);
        List<Question> questions = course.getQuestions();
        questions.add(question);
        course.setQuestions(questions);
    }
    // no need for course so I remove it
    public Question getQuestionById(int courseId, int questionId){
        Course course= courseRepositry.findById(courseId).orElse(null);
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
    public List<Question> getQuestions(int courseId){
        List<Question> questionList = null;
        return questionList;
    }
    // change parameter Course-> CourseId
    public void createQuiz(int courseId , Quiz newQuiz){
        Course course = courseRepositry.findById(courseId).orElse(null);
        List<Quiz> quizs = course.getQuizzes();
        quizs.add(newQuiz);
    }
    // change parameter Course-> CourseId
    public Quiz generateQuiz(int courseId){
        Course course = courseRepositry.findById(courseId).orElse(null);
        Random random = new Random();
        Quiz quiz= null;
        return quiz;
    }
    public void addAssignment(Course course){

    }
    public void getAssignment(Course course){

    }
    public void handAssignment(int assignmentId){

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
