package com.ojt.mockproject.dto.Course;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCourseDTO {
    private String courseTittle;
    private String courseCategory;
    private BigDecimal price;
    private String courseDescription;
    private String shortDescription;
    private String studentWillLearn;
    private String requirements;
    private String audioLanguage;
}
