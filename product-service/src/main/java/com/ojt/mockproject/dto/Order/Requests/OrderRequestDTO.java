package com.ojt.mockproject.dto.Order.Requests;

import com.ojt.mockproject.entity.Enum.OrderStatusEnum;
import com.ojt.mockproject.entity.Enum.PaymentMethodEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {
    private List<Integer> courses;
    private BigDecimal totalPrice;
    private PaymentMethodEnum paymentMethod;
}

