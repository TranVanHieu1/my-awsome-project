package com.ojt.mockproject.service;


import com.ojt.mockproject.dto.Course.CourseCategoryRequestDTO;
import com.ojt.mockproject.dto.Course.CourseResponseDTO;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.Enum.CourseStatusEnum;
import com.ojt.mockproject.exceptionhandler.course.CourseAppException;
import com.ojt.mockproject.exceptionhandler.ErrorCode;
import com.ojt.mockproject.exceptionhandler.course.CourseException;
import com.ojt.mockproject.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Spy
    @InjectMocks
    private CategoryService categoryService;
    @Mock
    private CategoryService categoryServiceMock; // For mocking internal methods


    private Course course1;
    private Course course2;;

    @BeforeEach
    public void setUp() {
        // Initialize course objects for testing
        course1 = new Course();
        course1.setId(1);
        course1.setName("Course 1");
        course1.setCategory("Java;Spring");
        course1.setPrice(BigDecimal.valueOf(100));
        course1.setDescription("Course description 1");
        course1.setCreateAt(LocalDateTime.now());
        course1.setStatus(CourseStatusEnum.APPROVED);
        course1.setIsOld(false);
        course1.setIsDeleted(false);
        course1.setPurchasedStudents("1,2,3");
        course1.setVersion(1);

        course2 = new Course();
        course2.setId(2);
        course2.setName("Course 2");
        course2.setCategory("Python;Django");
        course2.setPrice(BigDecimal.valueOf(150));
        course2.setDescription("Course description 2");
        course2.setCreateAt(LocalDateTime.now());
        course2.setStatus(CourseStatusEnum.APPROVED);
        course2.setIsOld(false);
        course2.setIsDeleted(false);
        course2.setPurchasedStudents("4,5,6");
        course2.setVersion(1);
    }



    @Test
    public void testGetAllUniqueCategories() {
        when(courseRepository.findAll()).thenReturn(Arrays.asList(course1));

        Set<String> result = categoryService.getAllUniqueCategories();
        verify(courseRepository, times(1)).findAll();

        assertEquals(new HashSet<>(Arrays.asList("Java", "Spring")), result);
    }

    @Test
    public void testGetAllUniqueCategories_Exception() {
        when(courseRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        CourseException exception = assertThrows(CourseException.class, () -> {
            categoryService.getAllUniqueCategories();
        });

        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
        verify(courseRepository, times(1)).findAll();
    }


    @Test
    public void testGetCategoriesByCourseId() {
        when(courseRepository.findById(course1.getId())).thenReturn(Optional.of(course1));
        List<String> result = categoryService.getCategoriesByCourseId(course1.getId());
        verify(courseRepository, times(1)).findById(course1.getId());

        assertEquals(Arrays.asList("Java", "Spring"), result);
    }

    @Test
    public void testGetCategoriesByCourseId_CourseNotFound() {
        when(courseRepository.findById(course1.getId())).thenReturn(Optional.empty());
        CourseAppException exception = assertThrows(CourseAppException.class, () -> {
            categoryService.getCategoriesByCourseId(course1.getId());
        });

        assertEquals(ErrorCode.COURSE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void testGetCategoriesByCourseId_NoCategoryFound() {
        course1.setCategory("");
        when(courseRepository.findById(course1.getId())).thenReturn(Optional.of(course1));

        CourseAppException exception = assertThrows(CourseAppException.class, () -> {
            categoryService.getCategoriesByCourseId(course1.getId());
        });

        assertEquals(ErrorCode.NO_CATEGORY_FOUND, exception.getErrorCode());
        verify(courseRepository, times(1)).findById(course1.getId());
    }
    @Test
    public void testGetCategoriesByCourseId_InternalServerError() {
        when(courseRepository.findById(course1.getId())).thenThrow(new RuntimeException("Unexpected error"));

        CourseAppException exception = assertThrows(CourseAppException.class, () -> {
            categoryService.getCategoriesByCourseId(course1.getId());
        });

        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
        verify(courseRepository, times(1)).findById(course1.getId());
    }

    @Test
    public void testUpdateCourseCategory_Success() {
        when(courseRepository.findById(course1.getId())).thenReturn(Optional.of(course1));
        CourseCategoryRequestDTO request = new CourseCategoryRequestDTO();
        request.setCategories(Arrays.asList("Math", "Science"));
        categoryService.updateCourseCategory(course1.getId(), request.getCategories());
        verify(courseRepository, times(1)).save(course1);

        assertEquals("Math;Science", course1.getCategory());
    }

    @Test
    public void testUpdateCourseCategory_CourseNotFound() {
        when(courseRepository.findById(course1.getId())).thenReturn(Optional.empty());
        CourseCategoryRequestDTO request = new CourseCategoryRequestDTO();
        request.setCategories(Arrays.asList("Math", "Science"));
        CourseAppException exception = assertThrows(CourseAppException.class, () -> {
            categoryService.updateCourseCategory(course1.getId(), request.getCategories());
        });

        assertEquals(ErrorCode.COURSE_NOT_FOUND, exception.getErrorCode());
    }
    @Test
    public void testFindCoursesByCategory_InternalServerError() {
        // Mock data
        Set<String> uniqueCategories = new HashSet<>(Arrays.asList("Java", "Spring", "Python", "Django"));

        // Mock methods
        doReturn(uniqueCategories).when(categoryService).getAllUniqueCategories();
        doThrow(new RuntimeException("Database error")).when(courseRepository).findAll();

        // Call the method and verify the exception
        CourseException exception = assertThrows(CourseException.class, () -> {
            categoryService.findCoursesByCategory("Java");
        });

        // Verify results
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
        assertEquals("Failed to find courses by category: Java", exception.getMessage());
        verify(courseRepository, times(1)).findAll();
    }


    @Test
    public void testFindCoursesByCategory_CategoryExistsWithCourses() {
        // Mock data
        Set<String> uniqueCategories = new HashSet<>(Arrays.asList("Java", "Spring", "Python", "Django"));
        List<Course> courses = Arrays.asList(course1, course2);
        List<String> categoriesCourse1 = Arrays.asList("Java", "Spring");
        List<String> categoriesCourse2 = Arrays.asList("Python", "Django");

        // Mock methods
        doReturn(uniqueCategories).when(categoryService).getAllUniqueCategories();
        doReturn(courses).when(courseRepository).findAll();
        doReturn(categoriesCourse1).when(categoryService).getCategoriesByCourseId(1);
        doReturn(categoriesCourse2).when(categoryService).getCategoriesByCourseId(2);
        doReturn(new CourseResponseDTO()).when(categoryService).convertToDto(course1);

        // Call the method
        List<CourseResponseDTO> result = categoryService.findCoursesByCategory("Java");

        // Verify results
        assertNotNull(result);
        assertEquals(1, result.size()); // Only course1 has category "Java"
        verify(courseRepository, times(1)).findAll();
    }



    @Test
    public void testFindCoursesByCategory_CategoryDoesNotExist() {
        // Mock data
        Set<String> uniqueCategories = new HashSet<>(Arrays.asList("Java", "Spring", "Python", "Django"));

        // Mock methods
        doReturn(uniqueCategories).when(categoryService).getAllUniqueCategories();

        // Call the method
        Exception exception = assertThrows(CourseException.class, () -> {
            categoryService.findCoursesByCategory("NonExistingCategory");
        });

        // Verify results
        assertEquals(ErrorCode.NO_CATEGORY_FOUND, ((CourseException) exception).getErrorCode());
        verify(courseRepository, never()).findAll();
    }

    @Test
    public void testConvertToDto() {
        // Create a mock Course object
        Course course = new Course();
        course.setId(1);
        course.setCategory("Programming");
        course.setCreateAt(LocalDateTime.now());
        course.setIsDeleted(false);
        course.setName("Java Programming");
        course.setStatus(CourseStatusEnum.APPROVED);
        course.setUpdateAt(LocalDateTime.now());
        course.setVersion(1);

        // Create a mock Account object for Course
        Account account = new Account();
        account.setId(100);
        course.setAccount(account);

        // Call the method under test
        CourseResponseDTO dto = categoryService.convertToDto(course);

        // Verify the result
        assertNotNull(dto);
        assertEquals(course.getId(), dto.getId());
        assertEquals(course.getCategory(), dto.getCategory());
        assertEquals(course.getCreateAt(), dto.getCreateAt());
        assertEquals(course.getIsDeleted(), dto.getIsDeleted());
        assertEquals(course.getName(), dto.getName());
        assertEquals(course.getStatus(), dto.getStatus());
        assertEquals(course.getUpdateAt(), dto.getUpdateBy());
        assertEquals(course.getVersion(), dto.getVersion());
        assertEquals(course.getAccount().getId(), dto.getAccountId());
    }
    @Test
    public void testConvertToDto_NullAccount() {
        // Create a mock Course object with null account
        Course course = new Course();
        course.setId(1);
        course.setCategory("Programming");
        course.setCreateAt(LocalDateTime.now());
        course.setIsDeleted(false);
        course.setName("Java Programming");
        course.setStatus(CourseStatusEnum.APPROVED);
        course.setUpdateAt(LocalDateTime.now());
        course.setVersion(1);

        // Call the method under test and assert the exception
        CourseException exception = assertThrows(CourseException.class, () -> {
            categoryService.convertToDto(course);
        });

        // Verify the exception message and error code
        assertEquals("Course's account is null", exception.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
    }

    @Test
    public void testConvertToDto_NullCourse() {
        // Create a null Course object
        Course course = null;

        // Call the method under test and assert the exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.convertToDto(course);
        });

        // Verify the exception message
        assertEquals("Course must not be null", exception.getMessage());
    }

}

