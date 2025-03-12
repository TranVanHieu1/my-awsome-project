package com.ojt.mockproject.dto.CourseChapter;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class ChaptersResponseDTO {
    private List<ChapterWithLectureResponseDTO> chapters;
}
