package com.ojt.mockproject.entity.certificate_quiz;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.Orderr;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne
    @JoinColumn(name = "course_id")
    @JsonIgnore
    private Course course;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = true)
    private LocalDateTime updateAt;

    @Column(name = "is_Deleted", nullable = false)
    private boolean is_Deleted;
}
