package com.ojt.mockproject.controller;

import com.ojt.mockproject.dto.Course.CourseCategoryRequestDTO;
import com.ojt.mockproject.dto.Course.CourseCategoryResponseDTO;
import com.ojt.mockproject.dto.Course.CourseResponseDTO;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.exceptionhandler.course.CourseAppException;
import com.ojt.mockproject.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin("*")
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Endpoint to get all unique categories
     *
     * @return ResponseEntity containing the list of unique categories
     */
    @GetMapping()
    public ResponseEntity<Set<String>> getAllUniqueCategories() {
        Set<String> uniqueCategories = categoryService.getAllUniqueCategories();
        return ResponseEntity.ok(uniqueCategories);
    }

    /**
     * Get all categories for a specific course
     *
     * @param courseId the ID of the course
     * @return ResponseEntity with CourseCategoryRequest containing the list of categories
     */
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseCategoryResponseDTO> getCourseCategories(@PathVariable Integer courseId) {
            List<String> categories = categoryService.getCategoriesByCourseId(courseId);
            CourseCategoryResponseDTO response = new CourseCategoryResponseDTO(categories);
            return ResponseEntity.ok(response);
    }

    //-------- <UC 33: Edit a category>--------
    /**
     * Endpoint to update the categories of a course identified by its ID.
     * Receives a list of categories in the request body and updates the course's category string accordingly.
     *
     * @param courseId               The ID of the course to update.
     * @param courseCategoryRequestDTO The DTO containing the list of new categories for the course.
     * @return ResponseEntity<Object> Indicating success or failure of the update operation.
     */
    @PutMapping("/{courseId}")
    public ResponseEntity<Object> updateCourseCategory(@PathVariable Integer courseId, @RequestBody CourseCategoryRequestDTO courseCategoryRequestDTO) {
        try {
            categoryService.updateCourseCategory(courseId, courseCategoryRequestDTO.getCategories());
            return ResponseEntity.status(HttpStatus.OK).body("Category updated successfully for course with ID: " + courseId);
        } catch (CourseAppException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode().getMessage());
        }
    }


    // ----- </UC33> --------

    /**
     * Endpoint to find courses without a specific category
     * @param category Category to search for
     * @return ResponseEntity containing the list of courses without the specified category
     */
    @GetMapping("/findCoursesByCategory")
    public ResponseEntity<List<CourseResponseDTO>> findCoursesByCategory(@RequestParam String category) {
        List<CourseResponseDTO> coursesWithoutCategory = categoryService.findCoursesByCategory(category);
        return ResponseEntity.ok(coursesWithoutCategory);
    }

}
