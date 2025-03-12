package com.ojt.mockproject.dto.CrashCourseVideo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class CrashCourseRequestDTO {
    private String mediaLink;
    private MultipartFile mediaThumnail;
    private Integer mediaDuration;
    private Integer courseId;
}
