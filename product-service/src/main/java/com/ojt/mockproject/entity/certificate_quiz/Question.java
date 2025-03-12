package com.ojt.mockproject.entity.certificate_quiz;

import com.ojt.mockproject.entity.Course;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @Column(name = "questionText", nullable = false)
    private String questionText;

    @Column(name = "image", nullable = true)
    private String image;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = true)
    private LocalDateTime updateAt;

    @Column(name = "score", nullable = true)
    private Double score;

    @Column(name = "is_Deleted", nullable = false)
    private boolean is_Deleted;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<QuestionChoice> questionChoices;
}
