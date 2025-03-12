package com.ojt.mockproject.dto.Wallet;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class CreateWalletRequest {
    private String bankName;
    private String bankAccountNumber;
}
