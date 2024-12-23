package com.dev.LMS.util;

import com.dev.LMS.model.Instructor;
import com.dev.LMS.model.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OTPEmail {
    Set<Student> students;
    Instructor instructor;
    String subject;
    String body;
    int OTPCode;
    int duration;




}
