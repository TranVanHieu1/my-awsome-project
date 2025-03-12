package com.ojt.mockproject.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ojt.mockproject.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.ojt.mockproject.dto.Account.Requests.RemoveWishListRequest;
import com.ojt.mockproject.dto.Account.Responses.RemoveWishListResponse;


public class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private RemoveWishListRequest removeWishListRequest;
    private RemoveWishListResponse removeWishListResponse;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        removeWishListRequest = new RemoveWishListRequest("1,2,3");
    }

    @Test
    public void testRemoveWishListResponse() {
        // Given
        String token = "Bearer someToken";
        when(accountService.removeWishList(token, removeWishListRequest)).thenReturn(new ResponseEntity<>(removeWishListResponse, HttpStatus.OK));

        // When
        ResponseEntity<RemoveWishListResponse> responseEntity = accountController.removeWishListResponse(token, removeWishListRequest);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(removeWishListResponse, responseEntity.getBody());
        verify(accountService, times(1)).removeWishList(token, removeWishListRequest);
    }
}
