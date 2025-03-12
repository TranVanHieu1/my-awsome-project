package com.ojt.mockproject.dto.Wallet.Requests;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class BuyCourseRequest {
    private String courseId;
    private BigDecimal price;
}
