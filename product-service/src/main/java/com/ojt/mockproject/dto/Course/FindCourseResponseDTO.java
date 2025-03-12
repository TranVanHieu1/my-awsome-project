package com.ojt.mockproject.dto.Course;

import com.ojt.mockproject.entity.Enum.CourseStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FindCourseResponseDTO {
    private Integer Id;
    private String title;
    private String date;
    private int sales;
    private int parts;
    private String category;
    private CourseStatusEnum status;
}
