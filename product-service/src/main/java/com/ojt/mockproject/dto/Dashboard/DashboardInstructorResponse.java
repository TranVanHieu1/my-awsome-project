package com.ojt.mockproject.dto.Dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardInstructorResponse {
    double totalSales;
    int totalCourse;
    long totalStudentPurchasedCourse;
    long totalSubcribers;
    int totalView;
    int totalPurchasedCouresHour;
}
