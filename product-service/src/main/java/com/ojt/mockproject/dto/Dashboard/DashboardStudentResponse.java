package com.ojt.mockproject.dto.Dashboard;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStudentResponse {
    int purchasedCourse;
    int totalInstructorsSubcribing;

}
