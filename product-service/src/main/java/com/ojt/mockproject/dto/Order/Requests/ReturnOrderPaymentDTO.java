package com.ojt.mockproject.dto.Order.Requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnOrderPaymentDTO {

    private Integer orderId;
    private String courseId;
    private String account;
    private Integer iat;
    private Integer exp;
}
