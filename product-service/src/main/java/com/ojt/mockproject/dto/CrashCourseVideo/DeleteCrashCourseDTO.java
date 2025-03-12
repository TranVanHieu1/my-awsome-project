package com.ojt.mockproject.dto.CrashCourseVideo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class DeleteCrashCourseDTO {
    private String message;
    private Boolean isDeleted;
}
