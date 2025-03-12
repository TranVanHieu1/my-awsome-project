package com.ojt.mockproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ojt.mockproject.dto.CourseChapter.*;
import com.ojt.mockproject.entity.CourseChapter;
import com.ojt.mockproject.service.CourseChapterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class CourseChapterControllerTest {

    @Mock
    private CourseChapterService courseChapterService;

    @InjectMocks
    private CourseChapterController courseChapterController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetCourseChaptersByCourse() {
        Integer courseId = 1;
        List<CourseChapter> mockChapters = new ArrayList<>();
        when(courseChapterService.getCourseChapterByCourse(courseId)).thenReturn(mockChapters);

        ResponseEntity responseEntity = courseChapterController.getCourseChaptersByCourse(courseId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testCreateCourseChapterWithLectures() {
        // Arrange
        ChapterWithLectureRequestDTO requestDTO = new ChapterWithLectureRequestDTO();
        ChapterWithLectureResponseDTO responseDTO = new ChapterWithLectureResponseDTO();
        when(courseChapterService.courseChapterWithLectures(any(ChapterWithLectureRequestDTO.class)))
                .thenReturn(responseDTO);

        // Act
        ResponseEntity<ChapterWithLectureResponseDTO> responseEntity = courseChapterController
                .createCourseChapterWithLectures(requestDTO);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseDTO, responseEntity.getBody());
    }

    @Test
    public void testCreateCourseChaptersWithLectures() {
        // Arrange
        ChaptersRequestDTO requestDTO = new ChaptersRequestDTO();
        ChaptersResponseDTO responseDTO = new ChaptersResponseDTO();
        when(courseChapterService.courseChaptersWithLectures(any(ChaptersRequestDTO.class)))
                .thenReturn(responseDTO);

        // Act
        ResponseEntity<ChaptersResponseDTO> responseEntity = courseChapterController
                .createCourseChaptersWithLectures(requestDTO);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseDTO, responseEntity.getBody());
    }

    @Test
    public void testCreateCourseChapter() {
        CourseChapterRequestDTO requestDTO = new CourseChapterRequestDTO();
        CourseChapterResponseDTO createdChapter = new CourseChapterResponseDTO();
        when(courseChapterService.createCourseChapter(any(CourseChapterRequestDTO.class))).thenReturn(createdChapter);

        ResponseEntity responseEntity = courseChapterController.createCourseChapter(requestDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testUpdateCourseChapter() {
        Integer chapterId = 1;
        CourseChapterRequestDTO requestDTO = new CourseChapterRequestDTO();
        UpdateCourseChapterDTO updatedChapter = new UpdateCourseChapterDTO();
        when(courseChapterService.updateCourseChapter(any(CourseChapterRequestDTO.class), eq(chapterId))).thenReturn(updatedChapter);

        ResponseEntity responseEntity = courseChapterController.updateCourseChapter(requestDTO, chapterId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testDeleteCourseChapter() {
        Integer chapterId = 1;
        DeleteCourseChapterDTO deletedChapter = new DeleteCourseChapterDTO();
        deletedChapter.setIsDeleted(true); // Set deleted flag as per service logic
        when(courseChapterService.deleteCourseChapter(chapterId)).thenReturn(deletedChapter);

        ResponseEntity responseEntity = courseChapterController.deleteCourseChapter(chapterId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}
