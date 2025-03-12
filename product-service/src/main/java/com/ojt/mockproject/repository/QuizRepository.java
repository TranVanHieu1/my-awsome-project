package com.ojt.mockproject.repository;

import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.certificate_quiz.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {

    Quiz findByCourse(Course course);
}
