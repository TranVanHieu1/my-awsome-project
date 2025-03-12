package com.ojt.mockproject.service;

import com.ojt.mockproject.dto.Course.CourseResponseDTO;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.exceptionhandler.course.CourseAppException;
import com.ojt.mockproject.exceptionhandler.ErrorCode;
import com.ojt.mockproject.exceptionhandler.course.CourseException;
import com.ojt.mockproject.repository.CourseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CourseRepository courseRepository;


    public Set<String> getAllUniqueCategories() {
        try {
            List<Course> courses = courseRepository.findAll();
            Set<String> uniqueCategories = new HashSet<>();

            for (Course course : courses) {
                if (course.getCategory() != null && !course.getCategory().isEmpty()) {
                    String[] categories = course.getCategory().split(";");
                    for (String category : categories) {
                        uniqueCategories.add(category.trim());
                    }
                }
            }
            return uniqueCategories;
        } catch (Exception e) {
            throw new CourseException("Failed to retrieve unique categories", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    //Get a list of categories for a course
    public List<String> getCategoriesByCourseId(Integer courseId) {
        try {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new CourseAppException(ErrorCode.COURSE_NOT_FOUND));

            if (course.getCategory() != null && !course.getCategory().isEmpty()) {
                return Arrays.stream(course.getCategory().split(";"))
                        .map(String::trim)
                        .distinct()
                        .collect(Collectors.toList());
            } else {
                throw new CourseAppException(ErrorCode.NO_CATEGORY_FOUND);
            }
        } catch (CourseAppException e) {
            throw e;
        }catch (Exception e) {
            throw new CourseAppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    @Transactional
    public void updateCourseCategory(Integer courseId, List<String> categories) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseAppException(ErrorCode.COURSE_NOT_FOUND));

        String categoryString = String.join(";", categories);
        course.setCategory(categoryString);

        courseRepository.save(course);
    }

    // Find courses by category\
    public List<CourseResponseDTO> findCoursesByCategory(String category) {
        // Check if the provided category exists in the system
        Set<String> allCourseCategories = getAllUniqueCategories();
        boolean categoryExists = allCourseCategories.stream()
                .anyMatch(cat -> cat.equalsIgnoreCase(category));

        if (!categoryExists) {
            throw new CourseException("Category not found: " + category, ErrorCode.NO_CATEGORY_FOUND);
        }

        List<CourseResponseDTO> coursesByCategory = new ArrayList<>();
        try {
            List<Course> allCourses = courseRepository.findAll();

            for (Course course : allCourses) {
                List<String> categories = getCategoriesByCourseId(course.getId());
                boolean containsCategory = categories.stream()
                        .anyMatch(cat -> cat.equalsIgnoreCase(category));
                if (containsCategory) {
                    CourseResponseDTO dto = convertToDto(course);
                    coursesByCategory.add(dto);
                }
            }

        } catch (Exception e) {
            throw new CourseException("Failed to find courses by category: " + category, ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return coursesByCategory;
    }
    public CourseResponseDTO convertToDto(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course must not be null");
        }
        CourseResponseDTO dto = new CourseResponseDTO();
        dto.setId(course.getId());
        dto.setCategory(course.getCategory());
        dto.setCreateAt(course.getCreateAt());
        dto.setIsDeleted(course.getIsDeleted());
        dto.setName(course.getName());
        dto.setStatus(course.getStatus());
        dto.setUpdateBy(course.getUpdateAt());
        dto.setVersion(course.getVersion());

        if (course.getAccount() == null) {
            throw new CourseException("Course's account is null", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        dto.setAccountId(course.getAccount().getId());

        return dto;
    }



}
