package com.ojt.mockproject.dto.Transaction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
public class TransactionDetailsDTO {
    private String accountName;
    private String accountPhoneNumber;
    private String accountEmail;
    private Integer orderId;
    private LocalDateTime orderDate;
    private Integer transactionId;
    private BigDecimal transactionTotalPrice;
    private String transactionPaymentMethod;
    private LocalDateTime transactionCreateAt;
    private Integer numberOfItems;
    private List<InvoiceCoursesResponseDTO> courses;
}