package com.ojt.mockproject.service;

import com.ojt.mockproject.dto.FeedbackWeb.FeedbackWebRequest;
import com.ojt.mockproject.dto.FeedbackWeb.FeedbackWebResponse;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.FeedbackWeb;
import com.ojt.mockproject.exceptionhandler.BadRequest;
import com.ojt.mockproject.exceptionhandler.account.NotLoginException;
import com.ojt.mockproject.repository.FeedbackWebRepository;
import com.ojt.mockproject.utils.AccountUtils;
import com.ojt.mockproject.utils.UploadFileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FeedbackWebServiceTest {

    @InjectMocks
    private FeedbackWebService feedbackWedService;

    @Mock
    private AccountUtils accountUtils;

    @Mock
    private UploadFileUtils uploadFileUtils;

    @Mock
    private FeedbackWebRepository feedbackWebRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddFeedbackWeb_Success() {
        // Arrange
        Account account = new Account();
        account.setEmail("test@example.com");
        FeedbackWebRequest feedbackWebRequest = new FeedbackWebRequest();
        feedbackWebRequest.setDescription("Test description");
        feedbackWebRequest.setScreenshot("test.png");

        when(accountUtils.getCurrentAccount()).thenReturn(account);
        when(feedbackWebRepository.save(any(FeedbackWeb.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        FeedbackWebResponse response = feedbackWedService.addFeedbackWeb(feedbackWebRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Test description", response.getDescription());
        assertEquals("test.png", response.getImage());

        verify(accountUtils, times(1)).getCurrentAccount();
        verify(feedbackWebRepository, times(1)).save(any(FeedbackWeb.class));
    }

    @Test
    void testAddFeedbackWeb_NotLoggedIn() {
        // Arrange
        FeedbackWebRequest feedbackWebRequest = new FeedbackWebRequest();
        feedbackWebRequest.setDescription("Test description");
        feedbackWebRequest.setScreenshot("test.png");

        when(accountUtils.getCurrentAccount()).thenThrow(new NotLoginException("Not Login"));

        // Act & Assert
        assertThrows(NotLoginException.class, () -> feedbackWedService.addFeedbackWeb(feedbackWebRequest));

        verify(accountUtils, times(1)).getCurrentAccount();
        verify(feedbackWebRepository, never()).save(any(FeedbackWeb.class));
    }

    @Test
    void testGetFeedbackWeb_Success() throws IOException {
        // Arrange
        Account account = new Account();
        FeedbackWeb feedbackWeb = new FeedbackWeb();
        feedbackWeb.setId(1);
        feedbackWeb.setCreateAt(LocalDateTime.now());
        feedbackWeb.setDescription("Test description");
        feedbackWeb.setImage("test.png");
        feedbackWeb.setAccount(account);

        when(accountUtils.getCurrentAccount()).thenReturn(account);
        when(feedbackWebRepository.findFeedbackWebByAccount(account)).thenReturn(feedbackWeb);
        when(uploadFileUtils.getSignedImageUrl("test.png")).thenReturn("signedTestUrl.png");

        // Act
        FeedbackWebResponse response = feedbackWedService.getFeedbackWeb();

        // Assert
        assertNotNull(response);
        assertEquals("Test description", response.getDescription());
        assertEquals("signedTestUrl.png", response.getImage());

        verify(accountUtils, times(1)).getCurrentAccount();
        verify(feedbackWebRepository, times(1)).findFeedbackWebByAccount(account);
        verify(uploadFileUtils, times(1)).getSignedImageUrl("test.png");
    }

    @Test
    void testGetFeedbackWeb_NotLoggedIn() {
        // Arrange
        when(accountUtils.getCurrentAccount()).thenThrow(new NotLoginException("Not Login"));

        // Act & Assert
        assertThrows(NotLoginException.class, () -> feedbackWedService.getFeedbackWeb());

        verify(accountUtils, times(1)).getCurrentAccount();
        verify(feedbackWebRepository, never()).findFeedbackWebByAccount(any(Account.class));
    }

    @Test
    void testGetFeedbackWeb_NoImage() throws IOException {
        // Arrange
        Account account = new Account();
        FeedbackWeb feedbackWeb = new FeedbackWeb();
        feedbackWeb.setId(1);
        feedbackWeb.setCreateAt(LocalDateTime.now());
        feedbackWeb.setDescription("Test description");
        feedbackWeb.setImage("test.png");
        feedbackWeb.setAccount(account);

        when(accountUtils.getCurrentAccount()).thenReturn(account);
        when(feedbackWebRepository.findFeedbackWebByAccount(account)).thenReturn(feedbackWeb);
        when(uploadFileUtils.getSignedImageUrl("test.png")).thenThrow(new RuntimeException("There is no image!"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> feedbackWedService.getFeedbackWeb());
        assertEquals("There is no image!", exception.getMessage());

        verify(accountUtils, times(1)).getCurrentAccount();
        verify(feedbackWebRepository, times(1)).findFeedbackWebByAccount(account);
        verify(uploadFileUtils, times(1)).getSignedImageUrl("test.png");
    }
}
