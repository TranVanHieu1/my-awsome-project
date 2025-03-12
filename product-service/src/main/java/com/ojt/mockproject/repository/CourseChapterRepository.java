package com.ojt.mockproject.repository;

import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.CourseChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseChapterRepository extends JpaRepository<CourseChapter, Integer> {

    CourseChapter findCourseChapterById(Integer id);

    List<CourseChapter> findByCourse(Course course);

}
