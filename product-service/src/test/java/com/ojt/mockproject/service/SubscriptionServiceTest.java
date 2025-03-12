package com.ojt.mockproject.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ojt.mockproject.dto.Account.Responses.SubscriptionResponseDTO;
import com.ojt.mockproject.dto.Course.CourseSubscriptionDTO;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.repository.AccountRepository;
import com.ojt.mockproject.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ObjectMapper objectMapper;

    private Account account;
    private Course course;
    private List<Map<String, String>> purchasedCourses;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Thiết lập đối tượng Course
        course = new Course();
        course.setId(1);
        course.setName("Introduction to Programming");
        course.setCategory("Programming;Computer Science");
        course.setDescription("Introduction to programming fundamentals");

        Account instructor = new Account();
        instructor.setId(1);
        instructor.setName("Minh Chau");
        course.setAccount(instructor);

        // Thiết lập đối tượng Account
        account = new Account();
        account.setId(1);
        account.setName("Minh Chau");

        // JSON string có thể chứa ký tự đặc biệt nên dùng escaped double quotes
        account.setPurchasedCourse("[{\"course_id\": 1, \"purchase_date\": \"2023-05-01\"}]");

        // Thiết lập danh sách purchasedCourses
        purchasedCourses = new ArrayList<>();
        Map<String, String> courseMap = new HashMap<>();
        courseMap.put("course_id", "1");
        courseMap.put("purchase_date", "2023-05-01");
        purchasedCourses.add(courseMap);
    }


    @Test
    public void testGetUserSubscriptions_Success() throws Exception {
        when(accountRepository.findById(1)).thenReturn(Optional.of(account));
        when(objectMapper.readValue(eq(account.getPurchasedCourse()), any(TypeReference.class)))
                .thenReturn(purchasedCourses);
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        SubscriptionResponseDTO responseDTO = subscriptionService.getUserSubscriptions(1);

        assertNotNull(responseDTO);
        assertEquals(1, responseDTO.getSubscriptions().size());

        CourseSubscriptionDTO subscription = responseDTO.getSubscriptions().get(0);
        assertEquals(1, subscription.getCourseId());
        assertEquals("Introduction to Programming", subscription.getCourseName());
        assertEquals("2023-05-01", subscription.getPurchaseDate());
        assertEquals("Minh Chau", subscription.getInstructorName());

        verify(accountRepository, times(1)).findById(1);
        verify(objectMapper, times(1)).readValue(eq(account.getPurchasedCourse()), any(TypeReference.class));
        verify(courseRepository, times(1)).findById(1);
    }


    @Test
    public void testGetUserSubscriptionsUser_NotFound() {
        when(accountRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            subscriptionService.getUserSubscriptions(1);
        });

        assertEquals("User not found", exception.getMessage());

        verify(accountRepository, times(1)).findById(1);
        verifyNoInteractions(objectMapper);
        verifyNoInteractions(courseRepository);
    }

    @Test
    public void testGetUserSubscriptions_ExceptionHandling() throws JsonProcessingException {
        when(accountRepository.findById(1)).thenReturn(java.util.Optional.of(account));
        when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenThrow(new JsonProcessingException("Error parsing JSON") {});

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            subscriptionService.getUserSubscriptions(1);
        });

        assertTrue(exception.getMessage().contains("Error parsing purchased courses JSON"));
        verify(objectMapper, times(1)).readValue(anyString(), any(TypeReference.class));
    }
}