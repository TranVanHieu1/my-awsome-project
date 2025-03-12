package com.ojt.mockproject.dto.Report;

import com.ojt.mockproject.entity.Enum.ReportCateEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportUpdateRequestDTO {
    String reportCategory;
    String description;
    Boolean isDeleted;
}

