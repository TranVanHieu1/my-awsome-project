package com.ojt.mockproject.dto.Quiz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceInput {

    private String questionOptionTitle;

    private boolean isOptionCorrect;

}
