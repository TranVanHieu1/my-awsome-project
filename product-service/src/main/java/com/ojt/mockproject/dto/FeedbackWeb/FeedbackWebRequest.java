package com.ojt.mockproject.dto.FeedbackWeb;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackWebRequest {

    private String description;
    private String screenshot;
}
