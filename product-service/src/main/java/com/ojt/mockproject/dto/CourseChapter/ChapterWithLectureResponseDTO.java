package com.ojt.mockproject.dto.CourseChapter;

import com.ojt.mockproject.dto.SmallCourseVideo.LectureWithChapterRequestDTO;
import com.ojt.mockproject.dto.SmallCourseVideo.SmallVideoResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor
@Data
@AllArgsConstructor
public class ChapterWithLectureResponseDTO {
    private Integer id;
    private String title;
    private Integer chapterIndex;
    private String createAt;
    private List<SmallVideoResponseDTO> lectures;

}
