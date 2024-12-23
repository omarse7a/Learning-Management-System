package com.dev.LMS.dto;

import com.dev.LMS.model.AssignmentSubmission;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentSubmissionDto {
    private int submissionId;
    private String assignmentTitle;
    private int studentId;
    private String studentName;
    private String fileName;
    private String fileType;
    private int grade;
    private boolean isGraded;
    private LocalDateTime submissionDate;

    public AssignmentSubmissionDto(AssignmentSubmission sub){
        submissionId = sub.getSubmissionId();
        assignmentTitle = sub.getAssignment().getTitle();
        studentId = sub.getStudent().getId();
        studentName = sub.getStudent().getName();
        fileName = sub.getFileName();
        fileType = sub.getFileType();
        grade = sub.getGrade();
        isGraded = sub.isGraded();
        submissionDate = sub.getSubmissionDate();
    }

    public AssignmentSubmissionDto(int submissionId, String assignmentTitle, int studentId, String studentName,
                                   String fileName, String fileType, int grade, boolean isGraded,
                                   LocalDateTime submissionDate) {
        this.submissionId = submissionId;
        this.assignmentTitle = assignmentTitle;
        this.studentId = studentId;
        this.studentName = studentName;
        this.fileName = fileName;
        this.fileType = fileType;
        this.grade = grade;
        this.isGraded = isGraded;
        this.submissionDate = submissionDate;
    }
}
