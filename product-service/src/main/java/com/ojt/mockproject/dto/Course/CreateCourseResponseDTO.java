package com.ojt.mockproject.dto.Course;

import com.ojt.mockproject.entity.Enum.CourseStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCourseResponseDTO {
    private Integer Id;
    private String basicCourseCategory;
    private String createAt;
    private String basicCourseTittle;
    private CourseStatusEnum courseStatus;
    private BigDecimal price;
    private String basicCourseDescription;
    private String basicShortDescription;
    private String basicStudentWillLearn;
    private String basicRequirements;
    private String basicAudioLanguage;
}
