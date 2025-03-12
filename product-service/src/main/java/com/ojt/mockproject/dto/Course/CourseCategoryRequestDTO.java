package com.ojt.mockproject.dto.Course;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CourseCategoryRequestDTO {
    private List<String> categories;
    private String category;
}
