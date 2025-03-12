package com.ojt.mockproject.repository;

import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.Enum.CourseStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    List<Course> findByIdIn(List<Integer> ids);

    List<Course> findByNameContainingIgnoreCase(String name);

    Course findCourseById(Integer id);

    List<Course> findByCategoryContaining(String category);

    List<Course> findByAccountId(Integer accountId);

    List<Course> findCourseByAccount(Account account);

    List<Course> findByAccountAndStatus(Account account, CourseStatusEnum status);

    List<Course> findCourseByAccountAndStatus(Account account, CourseStatusEnum courseStatusEnum);

    List<Course> findCoursesByAccount(Account account);

    List<Course> findByAccountIdAndIsDeleted(Integer accountId, boolean b);

    List<Course> findByStatusAndIsDeleted(CourseStatusEnum status, boolean b);

    Optional<Course> findByIdAndIsDeleted(Integer id, boolean b);
}

