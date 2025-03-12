package com.ojt.mockproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ojt.mockproject.dto.Feedback.DeleteResponseDTO;
import com.ojt.mockproject.dto.Feedback.FeedBackRequestDTO;
import com.ojt.mockproject.dto.Feedback.FeedBackResponseDTO;
import com.ojt.mockproject.service.FeedBackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FeedbackControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FeedBackService feedBackService;

    @InjectMocks
    private FeedBackController feedBackController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(feedBackController).build();
    }

    @Test
    @WithMockUser(authorities = "STUDENT")
    public void testCreateFeedBack_Success() throws Exception {
        // Mock input DTO
        FeedBackRequestDTO requestDTO = new FeedBackRequestDTO();
        requestDTO.setDescrip("Great course");
        requestDTO.setRating(5);

        // Mock service response
        FeedBackResponseDTO mockResponseDTO = new FeedBackResponseDTO("", "", "Great course", 5, "01/01/2024");
        when(feedBackService.addFeedBack(any(FeedBackRequestDTO.class))).thenReturn(mockResponseDTO);

        // Perform POST request and verify response
        mockMvc.perform(post("/feedback/create-feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value("Great course"))
                .andExpect(jsonPath("$.rating").value(5));

        // Verify service method invocation
        verify(feedBackService, times(1)).addFeedBack(any(FeedBackRequestDTO.class));
    }

    @Test
    @WithMockUser(authorities = "STUDENT")
    public void testRemoveFeedBack_Success() throws Exception {
        // Mock service response
        DeleteResponseDTO mockResponseDTO = new DeleteResponseDTO("Delete successfully!", "");
        when(feedBackService.deleteFeedBack(1)).thenReturn(mockResponseDTO);

        // Perform DELETE request and verify response
        mockMvc.perform(delete("/feedback/delete-feedback/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Delete successfully!"));

        // Verify service method invocation
        verify(feedBackService, times(1)).deleteFeedBack(1);
    }

    @Test
    public void testReadFeedBack_Success() throws Exception {
        // Mock service response
        List<FeedBackResponseDTO> mockResponseList = new ArrayList<>();
        mockResponseList.add(new FeedBackResponseDTO("", "", "Good course", 4, "01/01/2024"));
        mockResponseList.add(new FeedBackResponseDTO("", "", "Excellent course", 5, "01/01/2024"));
        when(feedBackService.getAllFeedBack()).thenReturn(mockResponseList);

        // Perform GET request and verify response
        mockMvc.perform(get("/feedback/read-feedback"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].description").value("Good course"))
                .andExpect(jsonPath("$[0].rating").value(4))
                .andExpect(jsonPath("$[1].description").value("Excellent course"))
                .andExpect(jsonPath("$[1].rating").value(5));

        // Verify service method invocation
        verify(feedBackService, times(1)).getAllFeedBack();
    }

    @Test
    public void testReadFeedbackByCourse_Success() throws Exception {
        // Mock service response
        List<FeedBackResponseDTO> mockResponseList = new ArrayList<>();
        mockResponseList.add(new FeedBackResponseDTO("", "", "Good course", 4, "01/01/2024"));
        mockResponseList.add(new FeedBackResponseDTO("", "", "Excellent course", 5, "01/01/2024"));
        when(feedBackService.getFeebackByCourse(1)).thenReturn(mockResponseList);

        // Perform GET request and verify response
        mockMvc.perform(get("/feedback/read-feedback-by-course/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].description").value("Good course"))
                .andExpect(jsonPath("$[0].rating").value(4))
                .andExpect(jsonPath("$[1].description").value("Excellent course"))
                .andExpect(jsonPath("$[1].rating").value(5));

        // Verify service method invocation
        verify(feedBackService, times(1)).getFeebackByCourse(1);
    }
}
