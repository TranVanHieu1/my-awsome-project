package com.ojt.mockproject.dto.WalletLog.Response;

import com.ojt.mockproject.entity.Enum.WalletLogTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class WalletLogResponseDTO {
    private Integer id;
    private Integer walletId;
    private WalletLogTypeEnum type;
    private BigDecimal amount;
    private LocalDateTime createAt;
    private Boolean isDeleted;

    public WalletLogResponseDTO() {

    }
}