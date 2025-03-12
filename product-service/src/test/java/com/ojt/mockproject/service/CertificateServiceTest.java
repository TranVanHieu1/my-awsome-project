package com.ojt.mockproject.service;

import com.ojt.mockproject.dto.Certificate.CertificateResponseDTO;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.certificate_quiz.Certificate;
import com.ojt.mockproject.exceptionhandler.ErrorCode;
import com.ojt.mockproject.exceptionhandler.course.CourseException;
import com.ojt.mockproject.repository.CertificateRepository;
import com.ojt.mockproject.repository.CourseRepository;
import com.ojt.mockproject.utils.AccountUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class CertificateServiceTest {
    @Mock
    private AccountUtils accountUtils;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CertificateRepository certificateRepository;

    @InjectMocks
    private CertificateService certificateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testCopyCertificateDetails() {
        // Arrange
        Course newCourse = new Course();
        Certificate oldCertificate = new Certificate();
        oldCertificate.setName("Old Certificate");
        oldCertificate.setDescription("Old Description");

        Certificate newCertificate = new Certificate();

        // Act
        certificateService.copyCertificateDetails(oldCertificate, newCertificate, newCourse);

        // Assert
        assertEquals(newCourse, newCertificate.getCourse());
        assertEquals("Old Certificate", newCertificate.getName());
        assertEquals("Old Description", newCertificate.getDescription());
        assertNotNull(newCertificate.getCreateAt());
        assertFalse(newCertificate.is_Deleted());
    }

    @Test
    void testGetCertificateByCourse_Success() {
        // Arrange
        Integer courseId = 1;
        Course course = new Course();
        when(courseRepository.findCourseById(courseId)).thenReturn(course);

        Certificate certificate = new Certificate();
        certificate.setId(1);
        certificate.setName("Test Certificate");
        certificate.setDescription("Test Description");
        certificate.setCreateAt(LocalDateTime.of(2022, 1, 1, 12, 0));
        when(certificateRepository.findByCourse(course)).thenReturn(certificate);

        Account account = new Account();
        account.setName("Test User");
        when(accountUtils.getCurrentAccount()).thenReturn(account);

        // Act
        CertificateResponseDTO response = certificateService.getCertificateByCourse(courseId);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Test Certificate", response.getName());
        assertEquals("Test Description", response.getDescription());
        assertEquals("01/01/2022 12:00", response.getCreateAt());
        assertEquals("Test User", response.getNameUser());
    }

    @Test
    void testGetCertificateByCourse_CourseNotFound() {
        // Arrange
        Integer courseId = 1;
        when(courseRepository.findCourseById(courseId)).thenReturn(null);

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            certificateService.getCertificateByCourse(courseId);
        });

        assertEquals("Course not found for ID: " + courseId, exception.getMessage());
        assertEquals(ErrorCode.COURSE_NOT_FOUND, exception.getErrorCode());
    }
}
