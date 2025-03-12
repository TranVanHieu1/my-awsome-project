package com.ojt.mockproject.dto.Transaction.Responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Enum.PaymentMethodEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetCashoutTransactionResponse {
    private Account account;
    private Integer transactionId;
    private BigDecimal amount;
    private PaymentMethodEnum paymentMethod;
    private LocalDateTime dateProcessed;

    public GetCashoutTransactionResponse(Integer transactionId, BigDecimal amount, PaymentMethodEnum paymentMethod, LocalDateTime dateProcessed) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.dateProcessed = dateProcessed;
    }
}
