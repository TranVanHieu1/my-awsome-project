package com.ojt.mockproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnore
    private Course course;

    @Column(nullable = false)
    private String link;

    @Column(name = "thumbnail", nullable = false)
    private String thumbnail;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private LocalDateTime createAt;

    @Column(nullable = true)
    private LocalDateTime updateAt;

    @Column(nullable = false)
    private Boolean isOld;

    @Column(nullable = false)
    private Boolean isDeleted;

    // Getters and Setters
}
