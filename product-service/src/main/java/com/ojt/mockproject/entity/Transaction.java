package com.ojt.mockproject.entity;

import com.ojt.mockproject.entity.Enum.PaymentMethodEnum;
import com.ojt.mockproject.entity.Enum.TransactionStatusEnum;
import com.ojt.mockproject.entity.Enum.TransactionTypeEnum;
import com.ojt.mockproject.entity.Enum.WalletLogTypeEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Orderr orderr;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethodEnum paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionTypeEnum type;

    @Column(name = "transaction_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatusEnum status;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    // Getters and setters
}
