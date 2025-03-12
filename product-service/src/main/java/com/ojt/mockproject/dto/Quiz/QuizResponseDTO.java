package com.ojt.mockproject.dto.Quiz;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponseDTO {
    private Integer courseId;
    private String quizTitle;
    private Integer numberOfQuestion;
    private List<QuestionOutput> questionOutputList;


}
