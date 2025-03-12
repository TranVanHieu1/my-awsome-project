package com.ojt.mockproject.dto.Order.Responses;


import com.ojt.mockproject.entity.Enum.OrderStatusEnum;
import com.ojt.mockproject.entity.Enum.PaymentMethodEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDTO {
    private Integer id;
    private BigDecimal totalPrice;
    private OrderStatusEnum status;
    private LocalDateTime createAt;
    private String courses;
    private PaymentMethodEnum paymentMethod;
}