package com.ojt.mockproject.entity;

import jakarta.persistence.Entity;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "update_at", nullable = true)
    private LocalDateTime updateAt;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    public Feedback(LocalDateTime createAt, String description, Integer rating, Account account, Course course){
        this.createAt = createAt;
        this.description = description;
        this.rating = rating;
        this.account = account;
        this.course = course;
        this.isDeleted = false;
    }



}
