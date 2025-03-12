package com.ojt.mockproject.dto.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class InvoiceCoursesResponseDTO {
    private Integer courseId;
    private String courseName;
    private BigDecimal price;
}