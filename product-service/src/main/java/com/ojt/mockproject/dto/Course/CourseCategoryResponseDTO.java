package com.ojt.mockproject.dto.Course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseCategoryResponseDTO {
    private List<String> categories;

}
