package com.ojt.mockproject.dto.Course;

import com.ojt.mockproject.entity.Enum.CourseStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponseDTO {
    private Integer id;
    private String category;
    private LocalDateTime createAt;
    private Boolean isDeleted;
    private String name;
    private CourseStatusEnum status;
    private LocalDateTime updateBy;
    private Integer version;
    private Integer accountId;


}
