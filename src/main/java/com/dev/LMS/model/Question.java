package com.dev.LMS.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Question {
    public Question() {
    ;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private QuestionType type;

    @Column(nullable = false)
    private String content;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<Choice> choices;

    @Column
    private String correctAnswer;


    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "course_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Course course;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "submissions",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "submission_id")
    )
    private List<QuizSubmission> submissions = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.PERSIST)
    private List<SubmittedQuestion> submittedQuestions = new ArrayList<>();


    public QuestionType getType() {
        return this.type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Choice> getChoices() {
        return this.choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public String getCorrectAnswer() {
        return this.correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }


    public List<SubmittedQuestion> getSubmittedQuestions() {
        return this.submittedQuestions;
    }

    public void setSubmittedQuestions(List<SubmittedQuestion> submittedQuestions) {
        this.submittedQuestions = submittedQuestions;
    }

    public List<QuizSubmission> getSubmissions() {
        return this.submissions;
    }

    public void setSubmissions(List<QuizSubmission> submissions) {
        this.submissions = submissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return Objects.equals(id, question.id) && type == question.type && Objects.equals(content, question.content) && Objects.equals(choices, question.choices) && Objects.equals(correctAnswer, question.correctAnswer) && Objects.equals(course, question.course);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, content, choices, correctAnswer, course);
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", choices=" + choices +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", course=" + course +
                '}';
    }

    public void addSubmission(QuizSubmission quizSubmission) {
        this.submissions.add(quizSubmission);
    }
}
