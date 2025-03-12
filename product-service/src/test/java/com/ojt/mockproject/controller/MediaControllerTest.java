package com.ojt.mockproject.controller;
import com.ojt.mockproject.dto.CrashCourseVideo.CrashCourseRequestDTO;
import com.ojt.mockproject.dto.CrashCourseVideo.CrashCourseResponseDTO;
import com.ojt.mockproject.dto.CrashCourseVideo.DeleteCrashCourseDTO;
import com.ojt.mockproject.dto.CrashCourseVideo.UpdateCrashCourseDTO;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Media;
import com.ojt.mockproject.service.CrashCourseVideoService;
import com.ojt.mockproject.utils.AccountUtils;
import jakarta.servlet.http.HttpServletRequest;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class MediaControllerTest {

    @Mock
    private CrashCourseVideoService crashCourseVideoService;

    @Mock
    private AccountUtils accountUtils;

    @InjectMocks
    private MediaController mediaController;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(httpServletRequest.getRequestURI()).thenReturn("/api/media/create");
        when(accountUtils.getCurrentAccount()).thenReturn(new Account()); // Mock the AccountUtils behavior
    }

    @Test
    public void testGetCrashCourseVideosByCourse() {
        Integer courseId = 1;
        List<Media> mockVideos = new ArrayList<>();
        when(crashCourseVideoService.getCrashCourseVideoByCourse(courseId)).thenReturn(mockVideos);

        ResponseEntity<List<Media>> responseEntity = mediaController.getCrashCourseVideosByCourse(courseId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockVideos, responseEntity.getBody());
    }

    @Test
    public void testCreateCrashCourseVideo() {
        CrashCourseRequestDTO requestDTO = new CrashCourseRequestDTO();
        CrashCourseResponseDTO createdVideo = new CrashCourseResponseDTO();
        when(crashCourseVideoService.createCrashCourseVideo(any(CrashCourseRequestDTO.class), anyString(), any(Account.class))).thenReturn(createdVideo);

        ResponseEntity<CrashCourseResponseDTO> responseEntity = mediaController.createCrashCourseVideo(requestDTO, httpServletRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(createdVideo, responseEntity.getBody());
    }

    @Test
    public void testUpdateCrashCourseVideo() {
        Integer videoId = 1;
        CrashCourseRequestDTO requestDTO = new CrashCourseRequestDTO();
        UpdateCrashCourseDTO updatedVideo = new UpdateCrashCourseDTO();
        when(crashCourseVideoService.updateCrashCourseVideo(any(CrashCourseRequestDTO.class), eq(videoId), anyString(), any(Account.class))).thenReturn(updatedVideo);

        ResponseEntity<UpdateCrashCourseDTO> responseEntity = mediaController.updateCrashCourseVideo(requestDTO, videoId, httpServletRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedVideo, responseEntity.getBody());
    }

    @Test
    public void testDeleteCrashCourseVideo() {
        Integer videoId = 1;
        DeleteCrashCourseDTO deletedVideo = new DeleteCrashCourseDTO();
        deletedVideo.setIsDeleted(true);
        when(crashCourseVideoService.deleteCrashCourseVideo(videoId)).thenReturn(deletedVideo);

        ResponseEntity<DeleteCrashCourseDTO> responseEntity = mediaController.deleteCrashCourseVideo(videoId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(deletedVideo, responseEntity.getBody());
    }
}
