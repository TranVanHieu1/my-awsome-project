package com.ojt.mockproject.dto.Dashboard;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"totalSalesInWeek", "inDay", "oneDayAgo", "twoDayAgo", "threeDayAgo", "fourDayAgo", "fiveDayAgo", "sixDayAgo"})
public class WeeklySalesResponse {
    int totalSalesInWeek;
    int inDay;
    int oneDayAgo;
    int twoDayAgo;
    int threeDayAgo;
    int fourDayAgo;
    int fiveDayAgo;
    int sixDayAgo;
}
