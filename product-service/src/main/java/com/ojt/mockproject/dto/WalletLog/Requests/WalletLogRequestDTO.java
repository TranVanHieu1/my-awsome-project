package com.ojt.mockproject.dto.WalletLog.Requests;

import com.ojt.mockproject.entity.Enum.WalletLogTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class WalletLogRequestDTO {

    private Integer walletId;
    private Integer transactionId;
    private WalletLogTypeEnum type;
    private BigDecimal amount;
    private Boolean isDeleted;


    public WalletLogRequestDTO() {

    }
}
