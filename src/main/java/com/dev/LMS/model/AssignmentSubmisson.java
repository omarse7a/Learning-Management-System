package com.dev.LMS.model;
import jakarta.persistence.*;
import java.io.File;
@Entity

public class AssignmentSubmisson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int submission_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(name = "submission")
    private File submission;

    @Column(name = "grade")
    private int grade;

    public AssignmentSubmisson() {
    }

    public int getSubmission_id() {
        return submission_id;
    }

    public void setSubmission_id(int submission_id) {
        this.submission_id = submission_id;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public File getSubmission() {
        return submission;
    }

    public void setSubmission(File submission) {
        this.submission = submission;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}
