package com.ojt.mockproject.dto.SmallCourseVideo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class DeleteSmallCourseVideoDTO {
    private String message;
    private Boolean isDeleted;
}
