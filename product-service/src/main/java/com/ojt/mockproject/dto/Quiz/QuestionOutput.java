package com.ojt.mockproject.dto.Quiz;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class QuestionOutput {
    private Integer questionId;
    private String questionText;
    private String questionImage;
    private List<ChoiceOutput> choiceOutputList;
}
