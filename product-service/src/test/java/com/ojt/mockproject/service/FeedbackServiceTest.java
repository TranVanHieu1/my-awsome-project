package com.ojt.mockproject.service;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.ojt.mockproject.dto.Feedback.DeleteResponseDTO;
import com.ojt.mockproject.dto.Feedback.FeedBackRequestDTO;
import com.ojt.mockproject.dto.Feedback.FeedBackResponseDTO;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.Feedback;
import com.ojt.mockproject.exceptionhandler.account.NotLoginException;
import com.ojt.mockproject.repository.CourseRepository;
import com.ojt.mockproject.repository.FeedbackRepository;
import com.ojt.mockproject.utils.AccountUtils;
import com.ojt.mockproject.utils.UploadFileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceTest {

    @Mock
    private AccountUtils accountUtils;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private UploadFileUtils uploadFileUtils;

    @InjectMocks
    private FeedBackService feedbackService;

    private FeedBackRequestDTO feedBackRequestDTO;
    private Account account;
    private Course course;
    private Feedback feedback;

    @BeforeEach
    void setUp() {
        feedBackRequestDTO = new FeedBackRequestDTO();
        feedBackRequestDTO.setCourseID(1);
        feedBackRequestDTO.setDescrip("Great course!");
        feedBackRequestDTO.setRating(5);


        account = new Account();
        account.setId(1);
        account.setName("John Doe");

        course = new Course();
        course.setId(1);
        course.setName("Test Course");

        feedback = new Feedback(LocalDateTime.now(), "Great course!", 5, account, course);


    }


    @Test
    void testAddFeedback_Success() throws Exception {
        // Arrange
        Account account = new Account();
        account.setId(1);
        account.setName("John Doe");
        account.setAvatar("avatar.png");

        Course course = new Course();
        course.setId(1);
        course.setName("Course 1");
        course.setIsDeleted(false);

        FeedBackRequestDTO feedBackRequestDTO = new FeedBackRequestDTO();
        feedBackRequestDTO.setCourseID(1);
        feedBackRequestDTO.setDescrip("Good service");
        feedBackRequestDTO.setRating(4);

        when(accountUtils.getCurrentAccount()).thenReturn(account);
        when(courseRepository.findById(feedBackRequestDTO.getCourseID())).thenReturn(Optional.of(course));
        when(uploadFileUtils.getSignedAvatarUrl(account.getAvatar())).thenReturn("testImageUrl.png");

        // Act
        FeedBackResponseDTO response = feedbackService.addFeedBack(feedBackRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(feedBackRequestDTO.getDescrip(), response.getDescription());
        assertEquals(feedBackRequestDTO.getRating(), response.getRating());
        assertEquals("testImageUrl.png", response.getAccountAvatar());
        assertEquals(account.getName(), response.getAccountName());
        assertEquals(course.getName(), response.getCourseName());

        verify(feedbackRepository, times(1)).save(any(Feedback.class));
        verify(uploadFileUtils, times(1)).getSignedAvatarUrl(account.getAvatar());
    }


    @Test
    void testAddFeedback_NotLoggedIn() {
        when(accountUtils.getCurrentAccount()).thenThrow(new NotLoginException("Not Login"));

        Exception exception = assertThrows(NotLoginException.class, () -> {
            feedbackService.addFeedBack(feedBackRequestDTO);
        });

        assertEquals("Not Login", exception.getMessage());
    }

    @Test
    void testAddFeedback_InvalidRating() throws Exception {
        // Arrange
        // Tạo dữ liệu giả lập
        Integer courseId = 1;
        Account account = new Account();
        account.setAvatar("avatar.png");

        Course course = new Course();
        course.setId(courseId);
        course.setIsDeleted(false); // Đảm bảo rằng khóa học không bị xóa

        FeedBackRequestDTO feedBackRequestDTO = new FeedBackRequestDTO();
        feedBackRequestDTO.setCourseID(courseId);
        feedBackRequestDTO.setDescrip("Great course!");
        feedBackRequestDTO.setRating(6); // Đánh giá không hợp lệ

        // Mock hành vi của các phương thức
        when(accountUtils.getCurrentAccount()).thenReturn(account);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            feedbackService.addFeedBack(feedBackRequestDTO);
        });

        // Xác nhận thông báo lỗi
        assertEquals("Please rating from 1 to 5", exception.getMessage());

        // Xác minh rằng phương thức save không được gọi
        verify(feedbackRepository, never()).save(any());
        verify(uploadFileUtils, never()).getSignedAvatarUrl(anyString());
    }


    @Test
    void testAddFeedback_CourseNotFound() {
        when(courseRepository.findById(feedBackRequestDTO.getCourseID())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            feedbackService.addFeedBack(feedBackRequestDTO);
        });

        assertEquals("No value present", exception.getMessage());
    }

    @Test
    public void testDeleteFeedback_Success() throws Exception {
        // Mock current account
        Account mockAccount = new Account();
        when(accountUtils.getCurrentAccount()).thenReturn(mockAccount);

        // Mock feedback
        Feedback mockFeedback = new Feedback();
        mockFeedback.setId(1);
        mockFeedback.setAccount(mockAccount);

        // Mock repository behavior
        when(feedbackRepository.findFeedbacksByIdAndAccount(1, mockAccount))
                .thenReturn(mockFeedback);

        // Call the service method
        DeleteResponseDTO responseDTO = feedbackService.deleteFeedBack(1);

        // Verify the results
        assertEquals("Delete successfully!", responseDTO.getMessage());
        assertTrue(mockFeedback.getIsDeleted());

        // Verify that save method was called
        verify(feedbackRepository, times(1)).save(mockFeedback);
    }

    @Test
    public void testDeleteFeedback_NotFound() throws Exception {
        // Mock current account
        Account mockAccount = new Account();
        when(accountUtils.getCurrentAccount()).thenReturn(mockAccount);

        // Mock repository returning null (feedback not found)
        when(feedbackRepository.findFeedbacksByIdAndAccount(1, mockAccount))
                .thenReturn(null);

        // Call the service method and expect exception
        assertThrows(RuntimeException.class, () -> {
            feedbackService.deleteFeedBack(1);
        });

        // Verify that save method was not called
        verify(feedbackRepository, never()).save(any());
    }

    @Test
    public void testDeleteFeedback_SaveFailure() throws Exception {
        // Mock current account
        Account mockAccount = new Account();
        when(accountUtils.getCurrentAccount()).thenReturn(mockAccount);

        // Mock feedback
        Feedback mockFeedback = new Feedback();
        mockFeedback.setId(1);
        mockFeedback.setAccount(mockAccount);

        // Mock repository behavior to find the feedback
        when(feedbackRepository.findFeedbacksByIdAndAccount(1, mockAccount))
                .thenReturn(mockFeedback);

        // Mock repository behavior to throw exception when saving feedback
        doThrow(new RuntimeException("Save failed")).when(feedbackRepository).save(mockFeedback);

        // Call the service method and expect exception
        Exception exception = assertThrows(Exception.class, () -> {
            feedbackService.deleteFeedBack(1);
        });

        // Verify the exception message
        assertEquals("Delete fail!", exception.getMessage());

        // Verify that save method was called exactly once
        verify(feedbackRepository, times(1)).save(mockFeedback);
    }



    @Test
    public void testGetFeedbackByCourse_EmptyList() throws Exception {
        // Mock repository behavior to return an empty list
        when(feedbackRepository.findFeedbacksByCourseId(1)).thenReturn(new ArrayList<>());

        // Call the service method and expect exception
        Exception exception = assertThrows(Exception.class, () -> {
            feedbackService.getFeebackByCourse(1);
        });

        // Verify the exception message
        assertEquals("There is no feedback!", exception.getMessage());

        // Verify repository method call
        verify(feedbackRepository, times(1)).findFeedbacksByCourseId(1);
    }

    @Test
    public void testGetFeedbackByCourse_CourseDeleted() throws Exception {
        // Mock feedback data with deleted course
        LocalDateTime time = LocalDateTime.now();
        Feedback feedback1 = new Feedback();
        feedback1.setId(1);
        feedback1.setCreateAt(time);
        feedback1.setDescription("Good course");
        feedback1.setIsDeleted(false);
        feedback1.setRating(4);
        Course course = new Course();
        course.setId(1);
        course.setIsDeleted(true); // Simulate course is deleted
        feedback1.setCourse(course);

        List<Feedback> mockFeedbackList = new ArrayList<>();
        mockFeedbackList.add(feedback1);

        // Mock repository behavior to find feedbacks by course ID
        when(feedbackRepository.findFeedbacksByCourseId(1)).thenReturn(mockFeedbackList);

        // Call the service method and expect exception
        Exception exception = assertThrows(Exception.class, () -> {
            feedbackService.getFeebackByCourse(1);
        });

        // Verify the exception message
        assertEquals("There is no feedback!", exception.getMessage());

        // Verify repository method call
        verify(feedbackRepository, times(1)).findFeedbacksByCourseId(1);
    }

    @Test
    public void testGetAllFeedBack() throws Exception {
        // Mock data
        LocalDateTime time = LocalDateTime.now();
        Account account = new Account();
        account.setId(1);
        account.setName("John Doe");
        account.setAvatar("avatar.png");

        Feedback feedback1 = new Feedback();
        feedback1.setId(1);
        feedback1.setCreateAt(time);
        feedback1.setDescription("Good service");
        feedback1.setIsDeleted(false);
        feedback1.setRating(4);
        feedback1.setAccount(account);
        Course course1 = new Course();
        course1.setId(1);
        course1.setName("Course 1");
        course1.setIsDeleted(false); // Simulate course is not deleted
        feedback1.setCourse(course1);

        Feedback feedback2 = new Feedback();
        feedback2.setId(2);
        feedback2.setCreateAt(time);
        feedback2.setDescription("Needs improvement");
        feedback2.setIsDeleted(false);
        feedback2.setRating(5);
        feedback2.setAccount(account);
        Course course2 = new Course();
        course2.setId(2);
        course2.setName("Course 2");
        course2.setIsDeleted(false); // Simulate course is not deleted
        feedback2.setCourse(course2);

        List<Feedback> mockFeedbackList = new ArrayList<>();
        mockFeedbackList.add(feedback1);
        mockFeedbackList.add(feedback2);

        // Mock behavior of feedbackRepository.findAll()
        when(feedbackRepository.findAll()).thenReturn(mockFeedbackList);
        when(uploadFileUtils.getSignedAvatarUrl(account.getAvatar())).thenReturn("testImageUrl.png");

        // Call the method under test
        List<FeedBackResponseDTO> result = feedbackService.getAllFeedBack();

        // Verify the result
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify mappings
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        assertEquals(time.format(formatter), result.get(0).getTime());
        assertEquals("Good service", result.get(0).getDescription());
        assertEquals(feedback1.getRating(), result.get(0).getRating());
        assertEquals("testImageUrl.png", result.get(0).getAccountAvatar());
        assertEquals(account.getName(), result.get(0).getAccountName());
        assertEquals(course1.getName(), result.get(0).getCourseName());

        assertEquals(time.format(formatter), result.get(1).getTime());
        assertEquals("Needs improvement", result.get(1).getDescription());
        assertEquals(feedback2.getRating(), result.get(1).getRating());
        assertEquals("testImageUrl.png", result.get(1).getAccountAvatar());
        assertEquals(account.getName(), result.get(1).getAccountName());
        assertEquals(course2.getName(), result.get(1).getCourseName());

        // Verify interactions
        verify(feedbackRepository, times(1)).findAll();
        verify(uploadFileUtils, times(2)).getSignedAvatarUrl(account.getAvatar());
    }


    @Test
    public void testGetAllFeedBackEmpty() throws Exception {
        // Mock behavior of feedbackRepository.findAll()
        when(feedbackRepository.findAll()).thenReturn(new ArrayList<>());

        // Call the method under test and expect exception
        Exception exception = assertThrows(Exception.class, () -> {
            feedbackService.getAllFeedBack();
        });

        // Verify the exception message
        assertTrue(exception.getMessage().contains("There is no feedback!"));

        // Verify interactions
        verify(feedbackRepository, times(1)).findAll();
    }


    @Test
    public void testCopyFeedbackDetails() {
        // Create oldFeedback and newFeedback objects
        Feedback oldFeedback = new Feedback();
        oldFeedback.setAccount(new Account()); // Assume the Account object has been set up
        oldFeedback.setRating(4);
        oldFeedback.setDescription("Good course feedback");
        oldFeedback.setCreateAt(LocalDateTime.now());
        oldFeedback.setIsDeleted(true); // Assume oldFeedback is deleted

        Feedback newFeedback = new Feedback();
        Course newCourse = new Course();

        // Call the copyFeedbackDetails method
        FeedBackService feedbackService = new FeedBackService();
        feedbackService.copyFeedbackDetails(oldFeedback, newFeedback, newCourse);

        // Check that the details have been copied correctly
        assertEquals(oldFeedback.getAccount(), newFeedback.getAccount());
        assertEquals(oldFeedback.getRating(), newFeedback.getRating());
        assertEquals(oldFeedback.getDescription(), newFeedback.getDescription());
        assertEquals(newCourse, newFeedback.getCourse()); // Check that newCourse has been set
        assertEquals(false, newFeedback.getIsDeleted()); // Check that isDeleted of newFeedback has been set to false
    }

}
