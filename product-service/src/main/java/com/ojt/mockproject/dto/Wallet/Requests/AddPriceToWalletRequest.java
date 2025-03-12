package com.ojt.mockproject.dto.Wallet.Requests;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Setter
@Getter
public class AddPriceToWalletRequest {
    private BigDecimal initialBalance;
}
