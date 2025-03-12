package com.ojt.mockproject.entity;

import com.ojt.mockproject.entity.Enum.ReportCateEnum;
import jakarta.persistence.Entity;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_cate", nullable = false)
    private ReportCateEnum reportCategory;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "update_at", nullable = true)
    private LocalDateTime updateAt;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

}
