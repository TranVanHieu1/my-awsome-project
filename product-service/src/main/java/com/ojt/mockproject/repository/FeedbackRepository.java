package com.ojt.mockproject.repository;

import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    Feedback findFeedbacksByIdAndAccount(Integer id, Account account);

    List<Feedback> findFeedbacksByCourseId(Integer id);

    List<Feedback> findByCourse(Course course);
}
