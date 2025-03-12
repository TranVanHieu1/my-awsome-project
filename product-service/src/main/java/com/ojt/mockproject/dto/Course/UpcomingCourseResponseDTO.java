package com.ojt.mockproject.dto.Course;

import com.ojt.mockproject.entity.Enum.CourseStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpcomingCourseResponseDTO {

    private String courseTitle;
    private String courseThumbnail;
    private String courseCategory;
    private BigDecimal coursePrice;
    private LocalDateTime courseCreateDate;
    private CourseStatusEnum courseStatusEnum;

}
