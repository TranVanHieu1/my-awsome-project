package com.ojt.mockproject.dto.Feedback;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedBackResponseDTO {
    private Integer Id;
    private String accountAvatar;
    private String accountName;
    private String courseName;
    private String description;
    private Integer rating;
    private String time;

    public FeedBackResponseDTO(String accountName, String courseName, String description, Integer rating, String time) {
        this.accountName = accountName;
        this.courseName = courseName;
        this.description = description;
        this.rating = rating;
        this.time = time;
    }
}
