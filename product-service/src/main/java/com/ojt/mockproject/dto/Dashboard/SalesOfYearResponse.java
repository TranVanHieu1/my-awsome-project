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
@JsonPropertyOrder({"january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december"})
public class SalesOfYearResponse {
    float january;
    float february;
    float march;
    float april;
    float may;
    float june;
    float july;
    float august;
    float september;
    float october;
    float november;
    float december;
}
