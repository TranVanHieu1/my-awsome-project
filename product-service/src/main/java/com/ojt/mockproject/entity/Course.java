package com.ojt.mockproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ojt.mockproject.entity.Enum.CourseStatusEnum;
import com.ojt.mockproject.entity.certificate_quiz.Quiz;
import jakarta.persistence.Entity;
import lombok.*;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Course")
public class Course implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "student_will_learn", nullable = false)
    private String studentWillLearn;

    @Column(name = "requirements", nullable = false)
    private String requirements;

    @Column(name = "short_description", nullable = false)
    private String shortDescription;

    @Column(name = "audio_language", nullable = false)
    private String audioLanguage;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = true)
    private LocalDateTime updateAt;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CourseStatusEnum status;

    @Column(name = "is_old", nullable = false)
    private Boolean isOld;

    @Column(name = "old_version", nullable = true)
    private String oldVersion;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "purchased_students", nullable = true)
    private String purchasedStudents;

    @Column(name = "view", nullable = false)
    private Integer view;

    @Column(name = "thumbnail_url", nullable = true)
    private String thumbnailUrl;

    @OneToMany(mappedBy = "course")
    @JsonIgnore
    private List<CourseChapter> chapters;

    @OneToMany(mappedBy = "course")
    @JsonIgnore
    private List<Media> crashCourseVideos;

    @OneToMany(mappedBy = "course")
    @JsonIgnore
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "course")
    @JsonIgnore
    private List<Report> reports;

    @OneToOne(mappedBy = "course", cascade = CascadeType.ALL)
    private Quiz quiz;

    public List<Integer> getOldVersionList() {
        if (oldVersion == null || oldVersion.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(oldVersion.split(","))
                .map(Integer::valueOf)
                .toList();
    }

    public void setOldVersionList(List<Integer> oldVersionList) {
        this.oldVersion = oldVersionList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

}
