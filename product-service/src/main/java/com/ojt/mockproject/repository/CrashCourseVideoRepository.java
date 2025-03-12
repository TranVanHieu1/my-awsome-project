package com.ojt.mockproject.repository;

import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrashCourseVideoRepository extends JpaRepository<Media, Integer> {

    Media findCrashCourseVideoById(Integer id);

    List<Media> findByCourse(Course course);

    Media findByCourseId(Integer courseId);

}
