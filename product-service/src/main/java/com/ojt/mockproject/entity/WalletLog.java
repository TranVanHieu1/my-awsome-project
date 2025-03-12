package com.ojt.mockproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ojt.mockproject.entity.Enum.WalletLogTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Builder
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wallet_log")
public class WalletLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @OneToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletLogTypeEnum type;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

}

