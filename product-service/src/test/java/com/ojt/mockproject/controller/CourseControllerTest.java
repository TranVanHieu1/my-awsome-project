package com.ojt.mockproject.controller;

import com.ojt.mockproject.dto.Course.*;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.service.CourseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CourseControllerTest {

    @InjectMocks
    private CourseController courseController;

    @Mock
    private CourseService courseService;

    @Test
    public void testGetAllCourses() {
        List<CourseDTO> mockCourses = new ArrayList<>();
        when(courseService.getAllCourses()).thenReturn(mockCourses);

        List<CourseDTO> result = courseController.getAllCourses();

        assertEquals(mockCourses, result);
    }

    @Test
    public void testSearchCourses() {
        String searchTerm = "Java";
        List<CourseDTO> mockCourses = new ArrayList<>();
        when(courseService.getCourseByName(searchTerm)).thenReturn(mockCourses);

        List<CourseDTO> result = courseController.searchCourses(searchTerm);

        assertEquals(mockCourses, result);
    }

    @Test
    public void testCreateCourse() {
        CourseRequestDTO courseRequestDTO = new CourseRequestDTO(); // Create a sample request DTO
        CreateCourseResponseDTO mockCourse = new CreateCourseResponseDTO(); // Create a sample mock Course
        when(courseService.createCourse(any(CourseRequestDTO.class))).thenReturn(mockCourse);

        ResponseEntity<CreateCourseResponseDTO> responseEntity = courseController.createCourse(courseRequestDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockCourse, responseEntity.getBody());
    }

    @Test
    public void testUpdateCourse() {
        UpdateCourseDTO updateCourseDTO = new UpdateCourseDTO(); // Create a sample UpdateCourseDTO
        Integer courseId = 1; // Sample course ID

        UpdateCourseResponseDTO updatedCourse = new UpdateCourseResponseDTO(); // Sample updated course object
        when(courseService.updateCourse(any(UpdateCourseDTO.class), eq(courseId))).thenReturn(updatedCourse);

        ResponseEntity responseEntity = courseController.updateCourse(updateCourseDTO, courseId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedCourse, responseEntity.getBody());
    }

    @Test
    public void testDeleteCourse() {
        Integer courseId = 1;

        DeleteCourseDTO deletedCourse = new DeleteCourseDTO();
        deletedCourse.setIsDeleted(true);
        when(courseService.deleteCourse(any(Integer.class))).thenReturn(deletedCourse);

        ResponseEntity responseEntity = courseController.deleteCourse(courseId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }



    @Test
    public void testGetCoursesByCategory() {
        CourseCategoryRequestDTO requestDTO = new CourseCategoryRequestDTO(); // Create a sample request DTO
        requestDTO.setCategory("Programming"); // Set sample category

        List<Course> mockCourses = new ArrayList<>(); // Sample list of courses
        when(courseService.getCoursesByCategory(requestDTO.getCategory())).thenReturn(mockCourses);

        ResponseEntity<List<Course>> responseEntity = courseController.getCoursesByCategory(requestDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockCourses, responseEntity.getBody());
    }
}
