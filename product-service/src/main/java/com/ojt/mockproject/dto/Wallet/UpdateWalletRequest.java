package com.ojt.mockproject.dto.Wallet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateWalletRequest {
    // Getters and setters
    private BigDecimal balance;
    private String bankName;
    private String bankAccountNumber;
}
