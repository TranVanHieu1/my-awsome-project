package com.ojt.mockproject.dto.WalletLog.Response;

import com.ojt.mockproject.entity.Enum.WalletLogTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletLogAccountResponseDTO {
        private Integer id;
        private Integer walletId;
        private Integer orderId;
        private Integer transactionId;
        private WalletLogTypeEnum type;
        private BigDecimal amount;
        private LocalDateTime createAt;
        private Boolean isDeleted;
}
