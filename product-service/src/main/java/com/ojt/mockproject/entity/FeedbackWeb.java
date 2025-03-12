package com.ojt.mockproject.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FeedbackWeb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "image", nullable = true)
    private String image;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;


}
