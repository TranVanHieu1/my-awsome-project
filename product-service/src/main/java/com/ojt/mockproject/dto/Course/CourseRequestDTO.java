package com.ojt.mockproject.dto.Course;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseRequestDTO {
    private String basicCourseTittle;
    private String basicCourseCategory;
    private BigDecimal price;
    private String basicCourseDescription;
    private String basicShortDescription;
    private String basicStudentWillLearn;
    private String basicRequirements;
    private String basicAudioLanguage;
}
