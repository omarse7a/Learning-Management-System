package com.dev.LMS.model;

import jakarta.persistence.*;

import java.io.File;

@Entity
public class AssignmentSub {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long AssignmentSubId;
    @OneToOne
    private Assignment assignment;
    @ManyToOne
    private User user;
    @Column(name = "submission")
    private File submission;
    @Column(name = "grade")
    private int grade;

}
