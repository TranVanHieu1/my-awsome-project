package com.ojt.mockproject.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ojt.mockproject.dto.CourseChapter.*;
import com.ojt.mockproject.dto.SmallCourseVideo.LectureWithChapterRequestDTO;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.CourseChapter;
import com.ojt.mockproject.entity.Lecture;
import com.ojt.mockproject.exceptionhandler.course.CourseException;
import com.ojt.mockproject.repository.CourseChapterRepository;
import com.ojt.mockproject.repository.CourseRepository;
import com.ojt.mockproject.repository.SmallCourseVideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class CourseChapterServiceTest {


    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseChapterRepository courseChapterRepository;

    @InjectMocks
    private CourseChapterService courseChapterService;

    @Mock
    private SmallCourseVideoRepository smallCourseVideoRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCourseChapterByCourse() {
        // Arrange
        Integer courseId = 1;
        Course course = new Course();
        course.setId(courseId);

        CourseChapter chapter1 = new CourseChapter();
        chapter1.setId(1);
        chapter1.setCourse(course);
        chapter1.setIsDeleted(false); // Not deleted

        CourseChapter chapter2 = new CourseChapter();
        chapter2.setId(2);
        chapter2.setCourse(course);
        chapter2.setIsDeleted(false); // Not deleted

        CourseChapter chapter3 = new CourseChapter();
        chapter3.setId(3);
        chapter3.setCourse(course);
        chapter3.setIsDeleted(true); // Deleted

        List<CourseChapter> chapters = Arrays.asList(chapter1, chapter2, chapter3);

        when(courseRepository.findCourseById(courseId)).thenReturn(course);
        when(courseChapterRepository.findByCourse(course)).thenReturn(chapters);

        // Act
        List<CourseChapter> result = courseChapterService.getCourseChapterByCourse(courseId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(chapter1));
        assertTrue(result.contains(chapter2));
        assertFalse(result.contains(chapter3)); // Ensure deleted chapter is not included

        verify(courseRepository, times(1)).findCourseById(courseId);
        verify(courseChapterRepository, times(1)).findByCourse(course);
    }


    @Test
    public void testGetCourseChapterByCourseNotFound() {
        // Arrange
        Integer courseId = 1;
        when(courseRepository.findCourseById(courseId)).thenReturn(null);

        // Act and Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseChapterService.getCourseChapterByCourse(courseId);
        });
        assertEquals("Course not found for ID: " + courseId, exception.getMessage());
        verify(courseRepository, times(1)).findCourseById(courseId);
        verify(courseChapterRepository, times(0)).findByCourse(any());
    }

    @Test
    public void testCreateCourseChapter() {
        // Arrange
        CourseChapterRequestDTO requestDTO = new CourseChapterRequestDTO();
        requestDTO.setCourseId(1);
        requestDTO.setTitle("Chapter 1");
        requestDTO.setChapterIndex(1);

        Course course = new Course();
        course.setId(1);
        course.setChapters(new ArrayList<>());

        CourseChapter courseChapter = new CourseChapter();
        courseChapter.setId(1);
        courseChapter.setName("Chapter 1");

        when(courseRepository.findCourseById(1)).thenReturn(course);
        when(courseChapterRepository.save(any(CourseChapter.class))).thenReturn(courseChapter);

        // Act
        CourseChapterResponseDTO result = courseChapterService.createCourseChapter(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Chapter 1", result.getTitle());
        verify(courseRepository, times(1)).findCourseById(1);
        verify(courseChapterRepository, times(1)).save(any(CourseChapter.class));
    }

    @Test
    public void testCreateCourseChapterCourseNotFound() {
        // Arrange
        CourseChapterRequestDTO requestDTO = new CourseChapterRequestDTO();
        requestDTO.setCourseId(1);
        requestDTO.setTitle("Chapter 1");
        requestDTO.setChapterIndex(1);

        when(courseRepository.findCourseById(1)).thenReturn(null);

        // Act and Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseChapterService.createCourseChapter(requestDTO);
        });
        assertEquals("Course not found for ID: " + requestDTO.getCourseId(), exception.getMessage());
        verify(courseRepository, times(1)).findCourseById(1);
        verify(courseChapterRepository, times(0)).save(any(CourseChapter.class));
    }

    @Test
    public void testCreateCourseChapterInvalidIndex() {
        // Arrange
        CourseChapterRequestDTO requestDTO = new CourseChapterRequestDTO();
        requestDTO.setCourseId(1);
        requestDTO.setTitle("Chapter 1");
        requestDTO.setChapterIndex(-1);

        // Act and Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            courseChapterService.createCourseChapter(requestDTO);
        });
        assertEquals("Chapter must be a positive integer", exception.getMessage());
        verify(courseRepository, times(0)).findCourseById(anyInt());
        verify(courseChapterRepository, times(0)).save(any(CourseChapter.class));
    }

    @Test
    public void testCourseChapterWithLectures() {
        // Arrange
        ChapterWithLectureRequestDTO requestDTO = new ChapterWithLectureRequestDTO();
        requestDTO.setCourseId(1);
        requestDTO.setTitle("Chapter 1");
        requestDTO.setChapterIndex(1);

        LectureWithChapterRequestDTO lectureDTO = new LectureWithChapterRequestDTO();
        lectureDTO.setLectureTitle("Lecture 1");
        lectureDTO.setLectureDescription("Description");
        lectureDTO.setLectureThumbnail("thumbnail.jpg");
        lectureDTO.setLectureLink("link");
        lectureDTO.setLectureDuration(30);
        requestDTO.setLectures(List.of(lectureDTO));

        Course course = new Course();
        course.setId(1);

        when(courseRepository.findCourseById(1)).thenReturn(course);
        when(courseChapterRepository.save(any(CourseChapter.class))).thenAnswer(i -> i.getArguments()[0]);
        when(smallCourseVideoRepository.save(any(Lecture.class))).thenAnswer(i -> {
            Lecture savedLecture = i.getArgument(0);
            savedLecture.setId(1);
            return savedLecture;
        });

        // Act
        ChapterWithLectureResponseDTO response = courseChapterService.courseChapterWithLectures(requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("Chapter 1", response.getTitle());
        assertEquals(1, response.getChapterIndex());
        assertEquals(1, response.getLectures().size());
        assertEquals("Lecture 1", response.getLectures().get(0).getLectureTitle());

        verify(courseRepository, times(1)).findCourseById(1);
        verify(courseChapterRepository, times(1)).save(any(CourseChapter.class));
        verify(smallCourseVideoRepository, times(1)).save(any(Lecture.class));
    }

    @Test
    public void testCourseChaptersWithLectures() {
        // Arrange
        ChaptersRequestDTO requestDTO = new ChaptersRequestDTO();
        requestDTO.setCourseId(1);

        ChapterRequestDTO chapterRequestDTO = new ChapterRequestDTO();
        chapterRequestDTO.setTitle("Chapter 1");
        chapterRequestDTO.setChapterIndex(1);

        LectureWithChapterRequestDTO lectureDTO = new LectureWithChapterRequestDTO();
        lectureDTO.setLectureTitle("Lecture 1");
        lectureDTO.setLectureDescription("Description");
        lectureDTO.setLectureThumbnail("thumbnail.jpg");
        lectureDTO.setLectureLink("link");
        lectureDTO.setLectureDuration(30);
        chapterRequestDTO.setLectures(List.of(lectureDTO));
        requestDTO.setChapters(List.of(chapterRequestDTO));

        Course course = new Course();
        course.setId(1);

        when(courseRepository.findCourseById(1)).thenReturn(course);
        when(courseChapterRepository.save(any(CourseChapter.class))).thenAnswer(i -> i.getArguments()[0]);
        when(smallCourseVideoRepository.save(any(Lecture.class))).thenAnswer(i -> {
            Lecture savedLecture = i.getArgument(0);
            savedLecture.setId(1);
            return savedLecture;
        });

        // Act
        ChaptersResponseDTO response = courseChapterService.courseChaptersWithLectures(requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getChapters().size());
        ChapterWithLectureResponseDTO chapterResponse = response.getChapters().get(0);
        assertEquals("Chapter 1", chapterResponse.getTitle());
        assertEquals(1, chapterResponse.getChapterIndex());
        assertEquals(1, chapterResponse.getLectures().size());
        assertEquals("Lecture 1", chapterResponse.getLectures().get(0).getLectureTitle());

        verify(courseRepository, times(1)).findCourseById(1);
        verify(courseChapterRepository, times(1)).save(any(CourseChapter.class));
        verify(smallCourseVideoRepository, times(1)).save(any(Lecture.class));
    }


    @Test
    public void testUpdateCourseChapter() {
        // Arrange
        Integer chapterId = 1;
        CourseChapterRequestDTO requestDTO = new CourseChapterRequestDTO();
        requestDTO.setTitle("Updated Chapter");
        requestDTO.setChapterIndex(2);

        CourseChapter courseChapter = new CourseChapter();
        courseChapter.setId(chapterId);
        courseChapter.setName("Old Chapter");
        courseChapter.setChapterIndex(1);

        when(courseChapterRepository.findCourseChapterById(chapterId)).thenReturn(courseChapter);
        when(courseChapterRepository.save(any(CourseChapter.class))).thenReturn(courseChapter);

        // Act
        UpdateCourseChapterDTO result = courseChapterService.updateCourseChapter(requestDTO, chapterId);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Chapter", result.getTitle());
        assertEquals(2, result.getChapterIndex());
        verify(courseChapterRepository, times(1)).findCourseChapterById(chapterId);
        verify(courseChapterRepository, times(1)).save(any(CourseChapter.class));
    }

    @Test
    public void testUpdateCourseChapterNotFound() {
        // Arrange
        Integer chapterId = 1;
        CourseChapterRequestDTO requestDTO = new CourseChapterRequestDTO();
        requestDTO.setTitle("Updated Chapter");
        requestDTO.setChapterIndex(2);

        when(courseChapterRepository.findCourseChapterById(chapterId)).thenReturn(null);

        // Act and Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            courseChapterService.updateCourseChapter(requestDTO, chapterId);
        });
        assertEquals("Course Chapter not found with id: " + chapterId, exception.getMessage());
        verify(courseChapterRepository, times(1)).findCourseChapterById(chapterId);
        verify(courseChapterRepository, times(0)).save(any(CourseChapter.class));
    }

    @Test
    public void testUpdateCourseChapterInvalidIndex() {
        // Arrange
        Integer chapterId = 1;
        CourseChapterRequestDTO requestDTO = new CourseChapterRequestDTO();
        requestDTO.setTitle("Updated Chapter");
        requestDTO.setChapterIndex(-1);

        // Act and Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            courseChapterService.updateCourseChapter(requestDTO, chapterId);
        });
        assertEquals("Chapter must be a positive integer", exception.getMessage());
        verify(courseChapterRepository, times(0)).findCourseChapterById(anyInt());
        verify(courseChapterRepository, times(0)).save(any(CourseChapter.class));
    }

    @Test
    public void testDeleteCourseChapter() {
        // Arrange
        Integer chapterId = 1;

        CourseChapter courseChapter = new CourseChapter();
        courseChapter.setId(chapterId);
        courseChapter.setIsDeleted(false);

        when(courseChapterRepository.findCourseChapterById(chapterId)).thenReturn(courseChapter);
        when(courseChapterRepository.save(any(CourseChapter.class))).thenReturn(courseChapter);

        // Act
        DeleteCourseChapterDTO result = courseChapterService.deleteCourseChapter(chapterId);

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsDeleted());
        verify(courseChapterRepository, times(1)).findCourseChapterById(chapterId);
        verify(courseChapterRepository, times(1)).save(any(CourseChapter.class));
    }

    @Test
    public void testDeleteCourseChapterNotFound() {
        // Arrange
        Integer chapterId = 1;
        when(courseChapterRepository.findCourseChapterById(chapterId)).thenReturn(null);

        // Act and Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            courseChapterService.deleteCourseChapter(chapterId);
        });
        assertEquals("Course Chapter not found with id: " + chapterId, exception.getMessage());
        verify(courseChapterRepository, times(1)).findCourseChapterById(chapterId);
        verify(courseChapterRepository, times(0)).save(any(CourseChapter.class));
    }
}
