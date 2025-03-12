package com.ojt.mockproject.repository;

import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Feedback;
import com.ojt.mockproject.entity.FeedbackWeb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackWebRepository extends JpaRepository<FeedbackWeb, Integer> {
        FeedbackWeb findFeedbackWebByAccount(Account account);
}
