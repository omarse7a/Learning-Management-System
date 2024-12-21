package com.dev.LMS.dto;

import java.util.*;

import com.dev.LMS.model.Lesson;
import com.dev.LMS.model.LessonResource;
import lombok.Data;

@Data
public class DetailedLessonDto extends LessonDto {

    private List<LessonResource> lessonResource;

    public DetailedLessonDto(Lesson lesson){
        super(lesson);
        this.lessonResource = lesson.getLessonResources();
    }

}

