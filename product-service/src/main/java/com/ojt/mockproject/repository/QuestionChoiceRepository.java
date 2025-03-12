package com.ojt.mockproject.repository;

import com.ojt.mockproject.entity.certificate_quiz.Question;
import com.ojt.mockproject.entity.certificate_quiz.QuestionChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionChoiceRepository extends JpaRepository<QuestionChoice, Integer> {
    Optional<QuestionChoice> findQuestionChoiceByQuestionAndIsCorrectTrue(Question question);
}
