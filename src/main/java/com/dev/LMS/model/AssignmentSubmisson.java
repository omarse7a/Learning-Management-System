package com.dev.LMS.model;
import jakarta.persistence.*;
import lombok.Data;

import java.io.File;
@Entity
@Table(name = "assignment_submissions")
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

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "grade")
    private int grade = 0;

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}
