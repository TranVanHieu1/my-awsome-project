package com.ojt.mockproject.dto.CourseChapter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class DeleteCourseChapterDTO {
    private String message;
    private Boolean isDeleted;
}
