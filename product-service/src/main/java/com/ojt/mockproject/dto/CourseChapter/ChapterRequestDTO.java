package com.ojt.mockproject.dto.CourseChapter;

import com.ojt.mockproject.dto.SmallCourseVideo.LectureWithChapterRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class ChapterRequestDTO {
    private String title;
    private Integer chapterIndex;
    private List<LectureWithChapterRequestDTO> lectures;
}
