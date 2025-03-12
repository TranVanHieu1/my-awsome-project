package com.ojt.mockproject.repository;

import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository  extends JpaRepository<Report, Integer> {
    List<Report> findByCourseId(Integer courseId);

    List<Report> findByCourse(Course course);
}
