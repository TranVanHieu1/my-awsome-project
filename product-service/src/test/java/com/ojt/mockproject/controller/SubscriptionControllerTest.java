package com.ojt.mockproject.controller;

import com.ojt.mockproject.dto.Account.Requests.SubscriptionRequestDTO;
import com.ojt.mockproject.dto.Account.Responses.SubscriptionResponseDTO;
import com.ojt.mockproject.service.SubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {

    @InjectMocks
    private SubscriptionController subscriptionController;
////
    @Mock
    private SubscriptionService subscriptionService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(subscriptionController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetUserSubscriptions_Success() throws Exception {
        SubscriptionRequestDTO requestDTO = new SubscriptionRequestDTO();
        requestDTO.setAccountId(1);

        SubscriptionResponseDTO responseDTO = new SubscriptionResponseDTO();
        // Add mock data to responseDTO as needed

        when(subscriptionService.getUserSubscriptions(any(Integer.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/subscriptions/view-sub")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(responseDTO)));
    }

}



