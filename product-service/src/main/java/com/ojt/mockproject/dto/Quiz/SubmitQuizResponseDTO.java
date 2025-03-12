package com.ojt.mockproject.dto.Quiz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SubmitQuizResponseDTO {
    private Integer numberOfCorrectQuestion;
    private Integer numberOfWrongQuestion;
    private Integer outOfQuestion;
    private String accountName;
}
