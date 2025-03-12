package com.ojt.mockproject.entity.certificate_quiz;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class QuestionChoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "answer", nullable = false)
    private String answer;

    @Column(name = "isCorrect", nullable = false)
    private boolean isCorrect;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = true)
    private LocalDateTime updateAt;

    @Column(name = "is_Deleted", nullable = false)
    private boolean is_Deleted;

    public QuestionChoice(Question question, String answer, boolean isCorrect, LocalDateTime createAt) {
        this.question = question;
        this.answer = answer;
        this.isCorrect = isCorrect;
        this.createAt = createAt;
        this.is_Deleted = false;
    }
}
