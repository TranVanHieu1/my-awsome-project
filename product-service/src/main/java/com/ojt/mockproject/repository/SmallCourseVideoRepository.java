package com.ojt.mockproject.repository;

import com.ojt.mockproject.entity.CourseChapter;
import com.ojt.mockproject.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SmallCourseVideoRepository extends JpaRepository<Lecture, Integer> {


    List<Lecture> findByCourseChapter(CourseChapter courseChapter);


}
