package com.ojt.mockproject.dto.Quiz;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuizRequestDTO {

    private String quizTitle;
    private String quizDescription;
    List<QuestionInput> questionInputList;
}
