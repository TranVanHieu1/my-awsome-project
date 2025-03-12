package com.ojt.mockproject.dto.Transaction;

import com.ojt.mockproject.entity.Enum.PaymentMethodEnum;
import com.ojt.mockproject.entity.Enum.TransactionStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {

    private Integer id;
    private Integer accountId;
    private Integer orderId;
    private BigDecimal totalPrice;

    private PaymentMethodEnum paymentMethod;

    // id tu gen


}
