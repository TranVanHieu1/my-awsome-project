package com.ojt.mockproject.dto.Course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCourseResponseDTO {
    private String courseTittle;
    private String courseCategory;
    private String courseDescription;
    private BigDecimal price;
    private String updateAt;
    private Integer version;
    private String description;
    private String studentWillLearn;
    private String requirements;
    private String audioLanguage;



}
