//package com.ojt.mockproject.controller;
//
//import com.ojt.mockproject.dto.CrashCourseVideo.DeleteCrashCourseDTO;
//import com.ojt.mockproject.dto.SmallCourseVideo.SmallVideoRequestDTO;
//import com.ojt.mockproject.dto.SmallCourseVideo.SmallVideoResponseDTO;
//import com.ojt.mockproject.dto.SmallCourseVideo.UpdateSmallCourseVideoDTO;
//import com.ojt.mockproject.entity.Lecture;
//import com.ojt.mockproject.service.SmallCourseVideoService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.when;
//
//public class LectureControllerTest {
//
//    @Mock
//    private SmallCourseVideoService smallCourseVideoService;
//
//    @InjectMocks
//    private LectureController lectureController;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//    }
//
//    @Test
//    public void testGetSmallCourseVideosByCourseChapter() {
//        Integer chapterId = 1;
//        List<Lecture> mockVideos = new ArrayList<>();
//        when(smallCourseVideoService.getSmallCourseVideosByCourseChapterId(chapterId)).thenReturn(mockVideos);
//
//        ResponseEntity responseEntity = lectureController.getSmallCourseVideosByCourseChapter(chapterId);
//
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//    }
//
//    @Test
//    public void testCreateSmallCourseVideo() {
//        SmallVideoRequestDTO requestDTO = new SmallVideoRequestDTO();
//        SmallVideoResponseDTO createdVideo = new SmallVideoResponseDTO();
//        when(smallCourseVideoService.createSmallCourseVideo(any(SmallVideoRequestDTO.class))).thenReturn(createdVideo);
//
//        ResponseEntity responseEntity = lectureController.createSmallCourseVideo(requestDTO);
//
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//    }
//
//    @Test
//    public void testUpdateSmallCourseVideo() {
//        Integer videoId = 1;
//        SmallVideoRequestDTO requestDTO = new SmallVideoRequestDTO();
//        UpdateSmallCourseVideoDTO updatedVideo = new UpdateSmallCourseVideoDTO();
//        when(smallCourseVideoService.updateSmallCourseVideo(any(SmallVideoRequestDTO.class), eq(videoId))).thenReturn(updatedVideo);
//
//        ResponseEntity responseEntity = lectureController.updateSmallCourseVideo(requestDTO, videoId);
//
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//    }
//
//    @Test
//    public void testDeleteSmallCourseVideo() {
//        Integer videoId = 1;
//        DeleteCrashCourseDTO deletedVideo = new DeleteCrashCourseDTO();
//        deletedVideo.setIsDeleted(true); // Set deleted flag as per service logic
//        when(smallCourseVideoService.deleteSmallCourseVideo(videoId)).thenReturn(deletedVideo);
//
//        ResponseEntity responseEntity = lectureController.deleteSmallCourseVideo(videoId);
//
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//    }
//}
//
