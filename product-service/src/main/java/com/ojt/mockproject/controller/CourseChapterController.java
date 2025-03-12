package com.ojt.mockproject.controller;

import com.ojt.mockproject.dto.CourseChapter.*;
import com.ojt.mockproject.entity.CourseChapter;
import com.ojt.mockproject.service.CourseChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/course-chapter")
public class CourseChapterController {
    @Autowired
    private CourseChapterService courseChapterService;

    @GetMapping("/get/{id}")
    public ResponseEntity getCourseChaptersByCourse(@PathVariable Integer id){
        List<CourseChapter> courseChapeters = courseChapterService.getCourseChapterByCourse(id);
        return ResponseEntity.ok(courseChapeters);
    }


    @PostMapping("/create")
    public ResponseEntity createCourseChapter(@RequestBody CourseChapterRequestDTO courseChapterRequestDTO){
        CourseChapterResponseDTO courseChapter = courseChapterService.createCourseChapter(courseChapterRequestDTO);
        return ResponseEntity.ok(courseChapter);
    }

    @PostMapping("/create-with-lectures")
    public ResponseEntity<ChapterWithLectureResponseDTO> createCourseChapterWithLectures(
            @RequestBody ChapterWithLectureRequestDTO chapterWithLectureRequestDTO) {
        ChapterWithLectureResponseDTO responseDTO = courseChapterService.courseChapterWithLectures(chapterWithLectureRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/create-multiple-with-lectures")
    public ResponseEntity<ChaptersResponseDTO> createCourseChaptersWithLectures(
            @RequestBody ChaptersRequestDTO chaptersRequestDTO) {
        ChaptersResponseDTO responseDTO = courseChapterService.courseChaptersWithLectures(chaptersRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity updateCourseChapter(@RequestBody CourseChapterRequestDTO courseChapterRequestDTO, @PathVariable Integer id){
        return ResponseEntity.ok(courseChapterService.updateCourseChapter(courseChapterRequestDTO, id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteCourseChapter(@PathVariable Integer id){
        return ResponseEntity.ok(courseChapterService.deleteCourseChapter(id));
    }
}
