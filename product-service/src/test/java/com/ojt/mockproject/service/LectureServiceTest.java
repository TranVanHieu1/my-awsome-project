//package com.ojt.mockproject.service;
//
//import com.ojt.mockproject.dto.CrashCourseVideo.DeleteCrashCourseDTO;
//import com.ojt.mockproject.dto.SmallCourseVideo.SmallVideoRequestDTO;
//import com.ojt.mockproject.dto.SmallCourseVideo.SmallVideoResponseDTO;
//import com.ojt.mockproject.dto.SmallCourseVideo.UpdateSmallCourseVideoDTO;
//import com.ojt.mockproject.entity.CourseChapter;
//import com.ojt.mockproject.entity.Lecture;
//import com.ojt.mockproject.repository.CourseChapterRepository;
//import com.ojt.mockproject.repository.SmallCourseVideoRepository;
//import com.ojt.mockproject.utils.AccountUtils;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class LectureServiceTest {
//
//    @InjectMocks
//    private SmallCourseVideoService smallCourseVideoService;
//
//    @Mock
//    private AccountUtils accountUtils;
//
//    @Mock
//    private CourseChapterRepository courseChapterRepository;
//
//    @Mock
//    private SmallCourseVideoRepository smallCourseVideoRepository;
//
//    private CourseChapter courseChapter;
//    private Lecture smallCourseVideo;
//    private SmallVideoRequestDTO smallVideoRequestDTO;
//
//    @BeforeEach
//    void setUp() {
//        courseChapter = new CourseChapter();
//        courseChapter.setId(1);
//
//        smallCourseVideo = new Lecture();
//        smallCourseVideo.setId(1);
//        smallCourseVideo.setCourseChapter(courseChapter);
//        smallCourseVideo.setName("Test Video");
//        smallCourseVideo.setLink("http://test.com");
//        smallCourseVideo.setDuration(10);
//        smallCourseVideo.setCreateAt(LocalDateTime.now());
//        smallCourseVideo.setIsDeleted(false);
//
//        smallVideoRequestDTO = new SmallVideoRequestDTO();
//        smallVideoRequestDTO.setCourseChapterId(1);
//        smallVideoRequestDTO.setLectureTitle("Test Video");
//        smallVideoRequestDTO.setLectureLink("http://test.com");
//        smallVideoRequestDTO.setLectureDuration(10);
//    }
//
//    @Test
//    void testCopySmallCourseVideoDetails() {
//        Lecture newVideo = new Lecture();
//        CourseChapter newChapter = new CourseChapter();
//        smallCourseVideoService.copySmallCourseVideoDetails(smallCourseVideo, newVideo, newChapter);
//        assertEquals(newChapter, newVideo.getCourseChapter());
//        assertEquals(smallCourseVideo.getName(), newVideo.getName());
//        assertEquals(smallCourseVideo.getLink(), newVideo.getLink());
//        assertNotNull(newVideo.getCreateAt());
//        assertFalse(newVideo.getIsDeleted());
//    }
//
//    @Test
//    void testGetSmallCourseVideosByCourseChapterId() {
//        when(courseChapterRepository.findById(1)).thenReturn(Optional.of(courseChapter));
//        when(smallCourseVideoRepository.findByCourseChapter(courseChapter)).thenReturn(Arrays.asList(smallCourseVideo));
//
//        List<Lecture> smallCourseVideos = smallCourseVideoService.getSmallCourseVideosByCourseChapterId(1);
//        assertEquals(1, smallCourseVideos.size());
//        verify(courseChapterRepository, times(1)).findById(1);
//        verify(smallCourseVideoRepository, times(1)).findByCourseChapter(courseChapter);
//    }
//
//    @Test
//    void testGetSmallCourseVideosByCourseChapterIdNotFound() {
//        when(courseChapterRepository.findById(1)).thenReturn(Optional.empty());
//
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            smallCourseVideoService.getSmallCourseVideosByCourseChapterId(1);
//        });
//
//        assertEquals("Course Chapter not found with id: 1", exception.getMessage());
//        verify(courseChapterRepository, times(1)).findById(1);
//        verify(smallCourseVideoRepository, times(0)).findByCourseChapter(any());
//    }
//
//    @Test
//    void testCreateSmallCourseVideo() {
//        when(courseChapterRepository.findById(1)).thenReturn(Optional.of(courseChapter));
//        when(smallCourseVideoRepository.save(any(Lecture.class))).thenReturn(smallCourseVideo);
//
//        SmallVideoResponseDTO responseDTO = smallCourseVideoService.createSmallCourseVideo(smallVideoRequestDTO);
//        assertEquals("Test Video", responseDTO.getLectureTitle());
//        assertEquals("http://test.com", responseDTO.getLectureLink());
//        assertEquals(10, responseDTO.getLectureDuration());
//        assertNotNull(responseDTO.getCreateAt());
//
//        verify(courseChapterRepository, times(1)).findById(1);
//        verify(smallCourseVideoRepository, times(1)).save(any(Lecture.class));
//    }
//
//    @Test
//    void testCreateSmallCourseVideoInvalidDuration() {
//        smallVideoRequestDTO.setLectureDuration(-1);
//
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            smallCourseVideoService.createSmallCourseVideo(smallVideoRequestDTO);
//        });
//
//        assertEquals("Duration must be a positive integer", exception.getMessage());
//        verify(courseChapterRepository, times(0)).findById(any());
//        verify(smallCourseVideoRepository, times(0)).save(any(Lecture.class));
//    }
//
//    @Test
//    void testCreateSmallCourseVideoCourseChapterNotFound() {
//        when(courseChapterRepository.findById(1)).thenReturn(Optional.empty());
//
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            smallCourseVideoService.createSmallCourseVideo(smallVideoRequestDTO);
//        });
//
//        assertEquals("Course Chapter not found with id: 1", exception.getMessage());
//        verify(courseChapterRepository, times(1)).findById(1);
//        verify(smallCourseVideoRepository, times(0)).save(any(Lecture.class));
//    }
//
//    @Test
//    void testUpdateSmallCourseVideo() {
//        when(smallCourseVideoRepository.findById(1)).thenReturn(Optional.of(smallCourseVideo));
//        when(smallCourseVideoRepository.save(any(Lecture.class))).thenReturn(smallCourseVideo);
//
//        UpdateSmallCourseVideoDTO updateDTO = smallCourseVideoService.updateSmallCourseVideo(smallVideoRequestDTO, 1);
//        assertEquals("Test Video", updateDTO.getLectureTittle());
//        assertEquals("http://test.com", updateDTO.getLectureLink());
//        assertEquals(10, updateDTO.getLectureDuration());
//
//        verify(smallCourseVideoRepository, times(1)).findById(1);
//        verify(smallCourseVideoRepository, times(1)).save(smallCourseVideo);
//    }
//
//    @Test
//    void testUpdateSmallCourseVideoInvalidDuration() {
//        smallVideoRequestDTO.setLectureDuration(-1);
//
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            smallCourseVideoService.updateSmallCourseVideo(smallVideoRequestDTO, 1);
//        });
//
//        assertEquals("Duration must be a positive integer", exception.getMessage());
//        verify(smallCourseVideoRepository, times(0)).findById(any());
//        verify(smallCourseVideoRepository, times(0)).save(any(Lecture.class));
//    }
//
//    @Test
//    void testUpdateSmallCourseVideoNotFound() {
//        when(smallCourseVideoRepository.findById(1)).thenReturn(Optional.empty());
//
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            smallCourseVideoService.updateSmallCourseVideo(smallVideoRequestDTO, 1);
//        });
//
//        assertEquals("Lecture not found with id: 1", exception.getMessage());
//        verify(smallCourseVideoRepository, times(1)).findById(1);
//        verify(smallCourseVideoRepository, times(0)).save(any(Lecture.class));
//    }
//
//    @Test
//    void testDeleteSmallCourseVideo() {
//        when(smallCourseVideoRepository.findById(1)).thenReturn(Optional.of(smallCourseVideo));
//        when(smallCourseVideoRepository.save(any(Lecture.class))).thenReturn(smallCourseVideo);
//
//        DeleteCrashCourseDTO deleteDTO = smallCourseVideoService.deleteSmallCourseVideo(1);
//        assertTrue(deleteDTO.getIsDeleted());
//        assertEquals("Delete Successful!!", deleteDTO.getMessage());
//
//        verify(smallCourseVideoRepository, times(1)).findById(1);
//        verify(smallCourseVideoRepository, times(1)).save(smallCourseVideo);
//    }
//
//    @Test
//    void testDeleteSmallCourseVideoNotFound() {
//        when(smallCourseVideoRepository.findById(1)).thenReturn(Optional.empty());
//
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            smallCourseVideoService.deleteSmallCourseVideo(1);
//        });
//
//        assertEquals("Lecture not found with id: 1", exception.getMessage());
//        verify(smallCourseVideoRepository, times(1)).findById(1);
//        verify(smallCourseVideoRepository, times(0)).save(any(Lecture.class));
//    }
//}
