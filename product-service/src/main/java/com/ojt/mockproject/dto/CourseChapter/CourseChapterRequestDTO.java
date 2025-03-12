package com.ojt.mockproject.dto.CourseChapter;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class CourseChapterRequestDTO {
    private String title;
    private Integer chapterIndex;
    private Integer courseId;
}
