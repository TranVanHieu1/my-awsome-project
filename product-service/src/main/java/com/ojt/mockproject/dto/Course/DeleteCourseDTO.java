package com.ojt.mockproject.dto.Course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteCourseDTO {
    private String message;
    private Boolean isDeleted;
}
