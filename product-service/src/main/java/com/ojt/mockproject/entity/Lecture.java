package com.ojt.mockproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "thumbnail", nullable = false)
    private String thumbnail;

    @Column(nullable = false)
    private String link;

    @Column(nullable = false)
    private Integer duration;

    @ManyToOne
    @JoinColumn(name = "courseChapterId", nullable = false)
    @JsonIgnore
    private CourseChapter courseChapter;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;


}
