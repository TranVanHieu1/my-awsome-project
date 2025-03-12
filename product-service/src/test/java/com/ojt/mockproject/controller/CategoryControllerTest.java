package com.ojt.mockproject.controller;

import com.ojt.mockproject.dto.Course.CourseCategoryRequestDTO;
import com.ojt.mockproject.dto.Course.CourseCategoryResponseDTO;
import com.ojt.mockproject.dto.Course.CourseResponseDTO;
import com.ojt.mockproject.exceptionhandler.ErrorCode;
import com.ojt.mockproject.exceptionhandler.course.CourseAppException;
import com.ojt.mockproject.service.CategoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private CourseCategoryRequestDTO requestDTO;

    private CourseResponseDTO courseResponseDTO;

    @Before
    public void setUp() {
        // Khởi tạo CourseCategoryRequestDTO với danh sách các categories
        requestDTO = new CourseCategoryRequestDTO();
        requestDTO.setCategories(Arrays.asList("Programming", "C/C++", "Computer", "Science"));

        courseResponseDTO = new CourseResponseDTO();
        courseResponseDTO.setId(1);
        courseResponseDTO.setCategory("Java Programming");
    }

    @Test
    public void testGetAllUniqueCategories() {
        // Giả lập dữ liệu trả về từ service
        Set<String> mockCategories = new HashSet<>(Arrays.asList("Programming", "Design", "Data Science"));
        when(categoryService.getAllUniqueCategories()).thenReturn(mockCategories);

        // Gọi phương thức controller
        ResponseEntity<Set<String>> response = categoryController.getAllUniqueCategories();

        // Kiểm tra phản hồi
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCategories, response.getBody());
    }

    @Test
    public void testGetCourseCategories() {
        Integer courseId = 1;
        List<String> mockCategories = Arrays.asList("Programming", "C/C++", "Computer");
        when(categoryService.getCategoriesByCourseId(courseId)).thenReturn(mockCategories);
        ResponseEntity<CourseCategoryResponseDTO> response = categoryController.getCourseCategories(courseId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCategories, response.getBody().getCategories());
    }

    @Test
    public void testUpdateCourseCategory_Success() {
        Integer courseId = 3;
        doNothing().when(categoryService).updateCourseCategory(courseId, requestDTO.getCategories());
        ResponseEntity<Object> response = categoryController.updateCourseCategory(courseId, requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Category updated successfully for course with ID: " + courseId, response.getBody());
    }

    @Test
    public void testUpdateCourseCategory_CourseAppException() {
        Integer courseId = 3;
        doThrow(new CourseAppException(ErrorCode.INVALID_CATEGORY)).when(categoryService).updateCourseCategory(courseId, requestDTO.getCategories());
        ResponseEntity<Object> response = categoryController.updateCourseCategory(courseId, requestDTO);

        assertEquals(ErrorCode.INVALID_CATEGORY.getHttpStatus(), response.getStatusCode());
        assertEquals(ErrorCode.INVALID_CATEGORY.getMessage(), response.getBody());
    }
    @Test
    public void testFindCoursesByCategory() {
        String category = "Programming";
        List<CourseResponseDTO> mockResponse = Arrays.asList(courseResponseDTO);
        when(categoryService.findCoursesByCategory(category)).thenReturn(mockResponse);

        ResponseEntity<List<CourseResponseDTO>> response = categoryController.findCoursesByCategory(category);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(categoryService, times(1)).findCoursesByCategory(category);
    }
}
