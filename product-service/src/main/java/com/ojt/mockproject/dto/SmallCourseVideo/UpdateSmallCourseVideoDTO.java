package com.ojt.mockproject.dto.SmallCourseVideo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
@AllArgsConstructor
public class UpdateSmallCourseVideoDTO {
    private String lectureTittle;
    private String lectureDescription;
    private String lectureThumbnail;
    private String lectureLink;
    private Integer lectureDuration;
}
