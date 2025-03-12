package com.ojt.mockproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Builder
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wallet")
public class Wallet {
    // Getters and setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "bank_account_number", nullable = false)
    private String bankAccountNumber;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @OneToMany(mappedBy = "wallet")
    @Transient
    @JsonIgnore
    private List<WalletLog> walletLog;
    // Constructor with parameters
    public Wallet(Account account, String bankName, String bankAccountNumber) {
        this.account = account;
        this.balance = BigDecimal.ZERO; //de mac dinh so du la 0
        this.isDeleted = false;
        this.bankName = bankName;
        this.bankAccountNumber = bankAccountNumber;
        this.createAt = LocalDateTime.now();
    }

}