package com.ojt.mockproject.dto.Wallet.Responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuyCourseResponse {
    private String message;
    private String error;
    private String courseId;
    private BigDecimal price;
    private BigDecimal remainingBalance;
    public BuyCourseResponse(String message, String error, String courseId, BigDecimal price, BigDecimal remainingBalance) {
        this.message = message;
        this.error = error;
        this.courseId = courseId;
        this.price = price;
        this.remainingBalance = remainingBalance;
    }
}
