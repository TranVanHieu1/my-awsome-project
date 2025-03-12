package com.ojt.mockproject.dto.Wallet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class WalletResponseDTO {
    private Integer walletId;
    private String accountName;
    private Integer accountId;
    private BigDecimal balance;
    private String bankName;
    private String bankAccountNumber;
    private BigDecimal totalMoneyInLast30Days;
    private BigDecimal totalMoneyOutLast30Days;

    public WalletResponseDTO(Integer walletId, String accountName, Integer accountId, BigDecimal balance, String bankName, String bankAccountNumber) {
        this.walletId = walletId;
        this.accountName = accountName;
        this.accountId = accountId;
        this.balance = balance;
        this.bankName = bankName;
        this.bankAccountNumber = bankAccountNumber;
        this.totalMoneyInLast30Days = BigDecimal.ZERO;
        this.totalMoneyOutLast30Days = BigDecimal.ZERO;
    }
}