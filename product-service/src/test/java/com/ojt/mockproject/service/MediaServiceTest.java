package com.ojt.mockproject.service;

import com.ojt.mockproject.dto.CrashCourseVideo.CrashCourseRequestDTO;
import com.ojt.mockproject.dto.CrashCourseVideo.CrashCourseResponseDTO;
import com.ojt.mockproject.dto.CrashCourseVideo.DeleteCrashCourseDTO;
import com.ojt.mockproject.dto.CrashCourseVideo.UpdateCrashCourseDTO;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.Media;
import com.ojt.mockproject.exceptionhandler.course.CourseException;
import com.ojt.mockproject.repository.CourseRepository;
import com.ojt.mockproject.repository.CrashCourseVideoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MediaServiceTest {

    @InjectMocks
    private CrashCourseVideoService crashCourseVideoService;

    @Mock
    private CrashCourseVideoRepository crashCourseVideoRepository;

    @Mock
    private CourseRepository courseRepository;

    @Test
    public void testGetCrashCourseVideoByCourse() {
        // Arrange
        Integer courseId = 1;
        Course course = new Course();
        course.setId(courseId);
        Media video = new Media();
        video.setIsDeleted(false);
        List<Media> crashCourseVideos = new ArrayList<>();
        crashCourseVideos.add(video);

        when(courseRepository.findCourseById(courseId)).thenReturn(course);
        when(crashCourseVideoRepository.findByCourse(course)).thenReturn(crashCourseVideos);

        // Act
        List<Media> result = crashCourseVideoService.getCrashCourseVideoByCourse(courseId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertFalse(result.get(0).getIsDeleted());
        verify(courseRepository, times(1)).findCourseById(courseId);
        verify(crashCourseVideoRepository, times(1)).findByCourse(course);
    }

    @Test
    public void testGetCrashCourseVideoByCourseNotFound() {
        // Arrange
        Integer courseId = 1;
        when(courseRepository.findCourseById(courseId)).thenReturn(null);

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            crashCourseVideoService.getCrashCourseVideoByCourse(courseId);
        });

        assertEquals("Course not found for ID: " + courseId, exception.getMessage());
        verify(courseRepository, times(1)).findCourseById(courseId);
        verify(crashCourseVideoRepository, times(0)).findByCourse(any(Course.class));
    }
    @Test
    public void testCreateCrashCourseVideo() {
        // Arrange
        Integer courseId = 1;
        CrashCourseRequestDTO requestDTO = new CrashCourseRequestDTO();
        requestDTO.setCourseId(courseId);
        requestDTO.setMediaLink("http://test.com");
        requestDTO.setMediaDuration(120);

        Course course = new Course();
        course.setId(courseId);
        List<Media> crashCourseVideos = new ArrayList<>();
        course.setCrashCourseVideos(crashCourseVideos);

        Media crashCourseVideo = new Media();
        when(courseRepository.findCourseById(courseId)).thenReturn(course);
        when(crashCourseVideoRepository.save(any(Media.class))).thenReturn(crashCourseVideo);

        // Act
        CrashCourseResponseDTO result = crashCourseVideoService.createCrashCourseVideo(requestDTO, "someUrl", new Account());

        // Assert
        assertNotNull(result);
        assertEquals("http://test.com", result.getMediaLink());
        assertEquals(120, result.getMediaDuration());
        verify(courseRepository, times(1)).findCourseById(courseId);
        verify(crashCourseVideoRepository, times(1)).save(any(Media.class));
    }

    @Test
    public void testCreateCrashCourseVideoCourseNotFound() {
        // Arrange
        Integer courseId = 1;
        CrashCourseRequestDTO requestDTO = new CrashCourseRequestDTO();
        requestDTO.setCourseId(courseId);
        requestDTO.setMediaLink("http://test.com");
        requestDTO.setMediaDuration(120);

        when(courseRepository.findCourseById(courseId)).thenReturn(null);

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            crashCourseVideoService.createCrashCourseVideo(requestDTO, "someUrl", new Account());
        });

        assertEquals("Course not found for ID: " + courseId, exception.getMessage());
        verify(courseRepository, times(1)).findCourseById(courseId);
        verify(crashCourseVideoRepository, times(0)).save(any(Media.class));
    }

    @Test
    public void testCreateCrashCourseVideoInvalidDuration() {
        // Arrange
        Integer courseId = 1;
        CrashCourseRequestDTO requestDTO = new CrashCourseRequestDTO();
        requestDTO.setCourseId(courseId);
        requestDTO.setMediaLink("http://test.com");
        requestDTO.setMediaDuration(-1);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            crashCourseVideoService.createCrashCourseVideo(requestDTO, "someUrl", new Account());
        });

        assertEquals("Duration must be a positive integer", exception.getMessage());
        verify(courseRepository, times(0)).findCourseById(anyInt());
        verify(crashCourseVideoRepository, times(0)).save(any(Media.class));
    }

    @Test
    public void testUpdateCrashCourseVideo() {
        // Arrange
        Integer videoId = 1;
        CrashCourseRequestDTO requestDTO = new CrashCourseRequestDTO();
        requestDTO.setMediaLink("http://updated.com");
        requestDTO.setMediaDuration(200);

        Media crashCourseVideo = new Media();
        crashCourseVideo.setId(videoId);
        crashCourseVideo.setLink("http://old.com");
        crashCourseVideo.setDuration(100);

        when(crashCourseVideoRepository.findCrashCourseVideoById(videoId)).thenReturn(crashCourseVideo);
        when(crashCourseVideoRepository.save(any(Media.class))).thenReturn(crashCourseVideo);

        // Act
        UpdateCrashCourseDTO result = crashCourseVideoService.updateCrashCourseVideo(requestDTO, videoId, "someUrl", new Account());

        // Assert
        assertNotNull(result);
        assertEquals("http://updated.com", result.getMediaLink());
        assertEquals(200, result.getMediaDuration());
        verify(crashCourseVideoRepository, times(1)).findCrashCourseVideoById(videoId);
        verify(crashCourseVideoRepository, times(1)).save(any(Media.class));
    }

    @Test
    public void testUpdateCrashCourseVideoNotFound() {
        // Arrange
        Integer videoId = 1;
        CrashCourseRequestDTO requestDTO = new CrashCourseRequestDTO();
        requestDTO.setMediaLink("http://updated.com");
        requestDTO.setMediaDuration(200);

        when(crashCourseVideoRepository.findCrashCourseVideoById(videoId)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            crashCourseVideoService.updateCrashCourseVideo(requestDTO, videoId, "someUrl", new Account());
        });

        assertEquals("Media not found with id: " + videoId, exception.getMessage());
        verify(crashCourseVideoRepository, times(1)).findCrashCourseVideoById(videoId);
        verify(crashCourseVideoRepository, times(0)).save(any(Media.class));
    }

    @Test
    public void testUpdateCrashCourseVideoInvalidDuration() {
        // Arrange
        Integer videoId = 1;
        CrashCourseRequestDTO requestDTO = new CrashCourseRequestDTO();
        requestDTO.setMediaLink("http://updated.com");
        requestDTO.setMediaDuration(-1);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            crashCourseVideoService.updateCrashCourseVideo(requestDTO, videoId, "someUrl", new Account());
        });

        assertEquals("Duration must be a positive integer", exception.getMessage());
        verify(crashCourseVideoRepository, times(0)).findCrashCourseVideoById(anyInt());
        verify(crashCourseVideoRepository, times(0)).save(any(Media.class));
    }


    @Test
    public void testDeleteCrashCourseVideo() {
        // Arrange
        Integer videoId = 1;

        Media crashCourseVideo = new Media();
        crashCourseVideo.setId(videoId);
        crashCourseVideo.setIsDeleted(false);

        when(crashCourseVideoRepository.findCrashCourseVideoById(videoId)).thenReturn(crashCourseVideo);
        when(crashCourseVideoRepository.save(any(Media.class))).thenReturn(crashCourseVideo);

        // Act
        DeleteCrashCourseDTO result = crashCourseVideoService.deleteCrashCourseVideo(videoId);

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsDeleted());
        verify(crashCourseVideoRepository, times(1)).findCrashCourseVideoById(videoId);
        verify(crashCourseVideoRepository, times(1)).save(any(Media.class));
    }

    @Test
    public void testDeleteCrashCourseVideoNotFound() {
        // Arrange
        Integer videoId = 1;
        when(crashCourseVideoRepository.findCrashCourseVideoById(videoId)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            crashCourseVideoService.deleteCrashCourseVideo(videoId);
        });

        assertEquals("Media not found with id: " + videoId, exception.getMessage());
        verify(crashCourseVideoRepository, times(1)).findCrashCourseVideoById(videoId);
        verify(crashCourseVideoRepository, times(0)).save(any(Media.class));
    }

}

