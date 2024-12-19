package com.dev.LMS.service;

import com.dev.LMS.model.*;
import com.dev.LMS.repository.CourseRepositry;
import com.dev.LMS.repository.QuestionRepositry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssessmentService {
    CourseRepositry courseRepositry;
    QuestionRepositry questionRepositry;
    void createQuestion(int courseId , QuestionType type ,List<Choice> choices, String correctAnswer ){
        Question newQuestion = new Question();
        Course course = courseRepositry.findById(courseId);
        newQuestion.setChoices(choices);
        newQuestion.setCourse(course);
        newQuestion.setCorrectAnswer(correctAnswer);
        newQuestion.setType(type);
        List<Question> questions = course.getQuestions();
        questions.add(newQuestion);
        course.setQuestions(questions);
    }
    // no need for course so I remove it
    Question getQuestionById(int questionId){
        Question question = questionRepositry.findById(questionId);
        return question;
    }
    // change parameter Course-> CourseId
    List<Question> getQuestions(int courseId){
        List<Question> questionList = null;
        return questionList;
    }
    // change parameter Course-> CourseId
    void createQuiz(int courseId , Quiz newQuiz){
        Course course = courseRepositry.findById(courseId);
        List<Quiz> quizs = course.getQuizzes();
        quizs.add(newQuiz);
    }
    // change parameter Course-> CourseId
    Quiz generateQuiz(int courseId){
        Course course = courseRepositry.findById(courseId);
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
