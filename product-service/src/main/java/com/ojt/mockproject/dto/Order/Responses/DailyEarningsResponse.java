package com.ojt.mockproject.dto.Order.Responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DailyEarningsResponse {
    private LocalDate date;
    private BigDecimal totalAmount;
    private int totalSaleInDay;
}
