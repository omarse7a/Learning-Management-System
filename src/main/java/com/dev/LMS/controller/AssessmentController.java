package com.dev.LMS.controller;

import com.dev.LMS.model.Question;
import com.dev.LMS.model.Quiz;
import com.dev.LMS.service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@Controller
@RequestMapping("/Course/{courseId}")
public class AssessmentController {

    @Autowired
    AssessmentService assessmentService;

    @PostMapping("/Create Question")
    public ResponseEntity<?> addQuestion(@PathVariable int courseId,
                                         @RequestBody Question question){
        try {
            System.out.println(question);
            assessmentService.createQuestion(courseId,question);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
@PostMapping("/Create Quiz")
    public ResponseEntity<?> createQuiz(@PathVariable int courseId,
                                        @RequestBody Quiz quiz){
        try {
            System.out.println(quiz);
            assessmentService.createQuiz(courseId,quiz);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
