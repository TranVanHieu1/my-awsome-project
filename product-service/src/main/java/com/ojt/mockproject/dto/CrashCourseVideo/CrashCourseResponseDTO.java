package com.ojt.mockproject.dto.CrashCourseVideo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
@AllArgsConstructor
public class CrashCourseResponseDTO {
    private Integer id;
    private String mediaLink;
    private String mediaThumnail;
    private Integer mediaDuration;
    private String createAt;
}
