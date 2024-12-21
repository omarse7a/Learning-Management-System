package com.dev.LMS.dto;

import com.dev.LMS.model.AssignmentSubmisson;
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

    public AssignmentSubmissionDto(AssignmentSubmisson sub){
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
}
