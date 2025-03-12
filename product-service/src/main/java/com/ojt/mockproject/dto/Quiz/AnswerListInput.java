package com.ojt.mockproject.dto.Quiz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Comparator;
import java.util.Collections;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AnswerListInput {
    private Integer questionId;
    private Integer choiceId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnswerListInput that = (AnswerListInput) o;
        return Objects.equals(questionId, that.questionId) &&
                Objects.equals(choiceId, that.choiceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, choiceId);
    }
}
