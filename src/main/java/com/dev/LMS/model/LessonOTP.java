package com.dev.LMS.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
//Don't change to Data gives error
@Getter
@Setter
public class LessonOTP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int otpValue;

    private LocalDateTime expireAt;


    @OneToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;


}
