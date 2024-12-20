package com.dev.LMS.service;

import com.dev.LMS.dto.AssignmentDto;
import com.dev.LMS.model.*;
import com.dev.LMS.repository.CourseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class AssessmentService {
    @Autowired
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
        for (Assignment assignment : assignments) {
            if(assignment.getAssignmentId() == assignmentId)
                return assignment;
        }
        return null;
    }
    public void submitAssignment(Course course, Assignment assignment){

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

