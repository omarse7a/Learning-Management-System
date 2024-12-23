package com.dev.LMS.dto;


import com.dev.LMS.model.LessonResource;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class LessonResourceDto {
    private int resource_id;
    private String file_name;
    private String file_type;
    private String lesson_title;
    private String course_name;
    private String author_name;

    public LessonResourceDto(LessonResource lessonResource){
        this.resource_id = lessonResource.getResource_id();
        this.file_name = lessonResource.getFile_name();
        this.file_type = lessonResource.getFile_type();
        this.lesson_title  = lessonResource.getLesson().getTitle();
        this.course_name = lessonResource.getLesson().getCourse().getName();
       //this.author_name = lessonResource.getLesson().getCourse().getInstructor().getName();
    }
}

