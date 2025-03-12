package com.ojt.mockproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "CourseChapter")
public class CourseChapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnore
    private Course course;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "chapter_index", nullable = false)
    private Integer chapterIndex;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = true)
    private LocalDateTime updateAt;

    @Column(name = "is_old", nullable = false)
    private Boolean isOld;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @OneToMany(mappedBy = "courseChapter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lecture> smallCourseVideos = new ArrayList<>();

}
