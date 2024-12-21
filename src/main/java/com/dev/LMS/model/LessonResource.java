package com.dev.LMS.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
public class LessonResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int resource_id;

    @Column(nullable = false)
    private String file_name;

    @Column(nullable = false)
    private String file_type;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lessonResources", nullable = false)
    @JsonIgnore
    private Lesson lesson;

    public LessonResource() {
    }

    public LessonResource(String file_name, String file_type) {
        this.file_name = file_name;
        this.file_type = file_type;

    }


}
