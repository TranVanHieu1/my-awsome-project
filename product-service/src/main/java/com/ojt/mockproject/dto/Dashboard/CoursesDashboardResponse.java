package com.ojt.mockproject.dto.Dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoursesDashboardResponse {
    String thumbnail;
    String name;
    int purchases;
    int views;

}
