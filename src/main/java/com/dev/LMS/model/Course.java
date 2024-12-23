package com.dev.LMS.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long courseId;

    @Column(unique=true, nullable=false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Float duration;    // completion hours

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Instructor instructor;

    //Lessons
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "course_id")

    private List<Lesson> lessons = new ArrayList<>();


    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "enrolled_students",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")

    )
    private Set<Student> enrolled_students = new HashSet<>();

    // Assignment List
    @OneToMany(cascade = CascadeType.ALL)
    private List<Assignment> assignments = new ArrayList<>();

    // Quiz List
    @OneToMany(mappedBy = "course",cascade = CascadeType.MERGE)
    private List<Quiz> quizzes = new ArrayList<>();

    // Question Bank
    @OneToMany(mappedBy = "course",cascade = CascadeType.ALL)
    private List<Question> questions = new ArrayList<>();
    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
    public void addQuestion(Question question){
        this.questions.add(question);
        question.setCourse(this);
    } public void addQuiz(Quiz quiz){
        this.quizzes.add(quiz);
        quiz.setCourse(this);
    }public void setQuiz(Quiz quiz){
        for (int i = 0; i < this.quizzes.size(); i++) {
            if(quizzes.get(i).getQuizTitle().equals(quiz.getQuizTitle())){
                quizzes.set(i,quiz);
                break;
            }

        }
    }

    public Course() {}

    public long getCourseId() {
        return courseId;
    }


    public void setId(long courseId) {
        this.courseId = courseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getDuration() {
        return duration;
    }

    public void setDuration(Float duration) {
        this.duration = duration;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;

    }

    public Set<Student> getEnrolled_students() {
        return enrolled_students;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return courseId == course.courseId && Objects.equals(name, course.name) && Objects.equals(description, course.description) && Objects.equals(duration, course.duration) && Objects.equals(instructor, course.instructor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, name, description, duration, instructor);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + courseId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", instructor_id=" + instructor +
                '}' +
                ", Number of Lessons= " + lessons.size()
                ;
    }

    //for lessons
    public List<Lesson> getLesson() {
        return lessons;
    }
    public void setLesson(List<Lesson> lesson) {
        this.lessons = lesson;
    }

    public void addLesson(Lesson lesson) {
        if (this.lessons == null) {
            this.lessons = new ArrayList<>();
        }
        this.lessons.add(lesson);

        lesson.setCourse(this);
    }

    public void removeLesson(Lesson lesson) {
        this.lessons.remove(lesson);
        lesson.setCourse(null);
    }

    //for enrolled students
    public void setEnrolled_students(Set<Student> enrolled_students) {
        this.enrolled_students = enrolled_students;
    }
    public void removeStudent(Student user) {
        this.enrolled_students.remove(user);
        user.unenrollCourse(this);
    }
    public void addStudent(@NotNull Student user) {
            this.enrolled_students.add(user);
            user.enrollCourse(this);

    }

    //for assignments
    public List<Assignment> getAssignments() {
        return assignments;
    }
    public void setAssignments(List<Assignment> assignment_list) {
        this.assignments = assignment_list;
    }

    public void addAssignment(Assignment assignment) {
        if (this.assignments == null) {
            this.assignments = new ArrayList<>();
        }
        this.assignments.add(assignment);
        assignment.setCourse(this);
    }




}
