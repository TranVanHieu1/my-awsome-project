package com.ojt.mockproject.dto.Wallet.Responses;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetWalletResponseDTO {
    private Integer walletId;
    private String accountName;
    private Integer accountId;
    private BigDecimal balance;
    private String bankName;
    private String bankAccountNumber;
}
