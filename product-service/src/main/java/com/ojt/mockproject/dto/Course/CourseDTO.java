package com.ojt.mockproject.dto.Course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CourseDTO {
    private Integer Id;
    private String name;
    private String instructorName;
    private String category;
    private String shortDescription;
    private String requirement;
    private BigDecimal price;
    private Double rating;
    private String createAt;
    private Integer duration;
    private String description;
    private String imgUrl;
    private int view;
    private String studentWillLearn;
    private String audioLanguage;

    public CourseDTO(Integer Id, String name, String instructorName, String category, BigDecimal price) {
        this.Id = Id;
        this.name = name;
        this.instructorName = instructorName;
        this.category = category;
        this.price = price;
    }
}
