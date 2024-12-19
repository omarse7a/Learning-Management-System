package com.dev.LMS.service;

import com.dev.LMS.model.*;
import com.dev.LMS.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class AssessmentService {
    private CourseRepository courseRepository;

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
        Course course = courseRepository.findByName(courseName).orElse(null);
        List<Quiz> quizs = course.getQuizzes();
        quizs.add(newQuiz);
    }
    // change parameter Course-> CourseId
    public Quiz generateQuiz(String courseName){
        Course course = courseRepository.findByName(courseName).orElse(null);
        Random random = new Random();
        Quiz quiz= null;
        return quiz;
    }
    public boolean addAssignment(String courseName, Assignment assignment, Instructor instructor){
        Course course = courseRepository.findByName(courseName).orElse(null);
        Set<Course> instructorCourses = instructor.getCreatedCourses();
        if(instructorCourses.contains(course)){
            course.addAssignment(assignment);
            courseRepository.save(course);
            return true;
        }
        return false;
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

