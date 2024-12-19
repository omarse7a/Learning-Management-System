package com.dev.LMS.service;

import com.dev.LMS.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssessmentService {
    void createQuestion(Course course){

    }
    Question getQuestionById(Course course,int questionId){
        Question question = null;
        return question;
    }
    List<Question> getQuestions(Course course){
        List<Question> questionList = null;
        return questionList;
    }
    void createQuiz(Course course){

    }
    Quiz generateQuiz(Course course){
        Quiz quiz= null;
        return quiz;
    }
    void addAssignment(Course course){

    }
    void getAssignment(Course course){

    }
    void handAssignment(int assignmentId){

    }

    void gradeQuiz(Quiz quiz){

    }
    int getQuizGrade(Quiz quiz) {
        return 0;
    }
    List<Assignment> getAssignmentSub(Assignment assignment){
        List<Assignment> assignmentList = null;
        return assignmentList;
    }
    void setAssignmentGrade(Assignment assignment) {

    }
    int getAssignmentGrade(Assignment assignment) {
        return 0;
    }
    List<Lesson> getLessonsAttended(Course course){
        List<Lesson> lessonList=null;
        return lessonList;
    }
    String send_feedback(Student student) {
        return "11";
    }
}
