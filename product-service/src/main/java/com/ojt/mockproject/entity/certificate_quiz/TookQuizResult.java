package com.ojt.mockproject.entity.certificate_quiz;

import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Orderr;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TookQuizResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "certificate_id")
    private Certificate certificate;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "resultEnum", nullable = false)
    @Enumerated(EnumType.STRING)
    private ResultEnum resultEnum;

    @Column(name = "correct", nullable = false)
    private Integer correct;

    @Column(name = "wrong", nullable = false)
    private Integer wrong;

    @Column(name = "percentage", nullable = false)
    private Double percentage;
}
