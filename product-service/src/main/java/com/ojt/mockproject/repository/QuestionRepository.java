package com.ojt.mockproject.repository;

import com.ojt.mockproject.entity.certificate_quiz.Question;
import com.ojt.mockproject.entity.certificate_quiz.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findQuestionsByQuiz(Quiz quiz);

}
