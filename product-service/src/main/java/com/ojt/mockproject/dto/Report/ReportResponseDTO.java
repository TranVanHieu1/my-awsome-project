package com.ojt.mockproject.dto.Report;

import com.ojt.mockproject.entity.Enum.ReportCateEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponseDTO {
    int id;
    String description;
    ReportCateEnum reportCategory;
    LocalDateTime updateAt;
    LocalDateTime createAt;
    Integer accountId;
    Integer courseId;
    Boolean isDeleted;
}
