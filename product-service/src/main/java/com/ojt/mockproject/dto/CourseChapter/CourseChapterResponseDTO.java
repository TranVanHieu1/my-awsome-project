package com.ojt.mockproject.dto.CourseChapter;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class CourseChapterResponseDTO {
    private Integer id;
    private String title;
    private Integer chapterIndex;
    private String createAt;
}
