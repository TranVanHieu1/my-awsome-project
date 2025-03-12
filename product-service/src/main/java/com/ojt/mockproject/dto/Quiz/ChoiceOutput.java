package com.ojt.mockproject.dto.Quiz;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceOutput {
    private Integer choiceId;
    private String answer;
}
