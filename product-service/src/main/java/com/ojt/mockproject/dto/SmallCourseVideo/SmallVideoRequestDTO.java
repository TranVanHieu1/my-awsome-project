package com.ojt.mockproject.dto.SmallCourseVideo;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Setter
@Getter
public class SmallVideoRequestDTO {
    private String lectureTitle;
    private String lectureDescription;
    private MultipartFile lectureThumbnail;
    private String lectureLink;
    private Integer lectureDuration;
    private Integer courseChapterId;
}
