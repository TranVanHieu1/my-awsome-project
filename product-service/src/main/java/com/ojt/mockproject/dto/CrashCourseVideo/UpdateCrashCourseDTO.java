package com.ojt.mockproject.dto.CrashCourseVideo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
@AllArgsConstructor
public class UpdateCrashCourseDTO {
    private String mediaLink;
    private Integer mediaDuration;
    private String mediaThumnail;
    private String updateAt;
}
