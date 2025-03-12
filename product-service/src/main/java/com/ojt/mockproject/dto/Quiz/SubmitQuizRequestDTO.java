package com.ojt.mockproject.dto.Quiz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitQuizRequestDTO {
    private Integer courseId;
    private Integer quizId;
    List<AnswerListInput> answerList;
}
