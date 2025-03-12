package com.ojt.mockproject.dto.FeedbackWeb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackWebResponse {
    private Integer Id;
    private String description;
    private String image;
}
