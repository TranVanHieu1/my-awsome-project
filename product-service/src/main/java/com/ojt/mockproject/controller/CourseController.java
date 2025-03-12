package com.ojt.mockproject.controller;

import com.ojt.mockproject.dto.Course.*;
import com.ojt.mockproject.dto.Quiz.QuizRequestDTO;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.Enum.CourseStatusEnum;
import com.ojt.mockproject.service.CourseService;
import com.ojt.mockproject.utils.UploadFileUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/course")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private UploadFileUtils uploadFile;

    @GetMapping("/view-course")
    public List<CourseDTO> getAllCourses() {
        return courseService.getAllCourses();

    }

    @GetMapping("/search/{name}")
    public List<CourseDTO> searchCourses(@PathVariable String name) {
        return courseService.getCourseByName(name);

    }

    @GetMapping("/search-course/{Id}")
    public CourseDTO searchCourseById(@PathVariable Integer Id) throws IOException {
        return courseService.viewCourseById(Id);
    }

    @PostMapping("/create-course" )
    public ResponseEntity<CreateCourseResponseDTO> createCourse(@RequestBody CourseRequestDTO course) {
        CreateCourseResponseDTO newCourse = courseService.createCourse(course);
        return ResponseEntity.ok(newCourse);
    }

    @PutMapping("update-course/{id}")
    public ResponseEntity updateCourse(@RequestBody UpdateCourseDTO courseDTO, @PathVariable Integer id) {
        return ResponseEntity.ok(courseService.updateCourse(courseDTO, id));
    }

    @DeleteMapping("delete-course/{id}")
    public ResponseEntity deleteCourse(@PathVariable Integer id) {
        return ResponseEntity.ok(courseService.deleteCourse(id));
    }

    @PostMapping("/view-course-by-category")
    public ResponseEntity<List<Course>> getCoursesByCategory(@RequestBody @Valid CourseCategoryRequestDTO request) {
        List<Course> courses = courseService.getCoursesByCategory(request.getCategory());
        return ResponseEntity.ok(courses);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @GetMapping("/view-my-course")
    public List<FindCourseResponseDTO> viewMyCourse(@RequestParam(value = "status", required = false) CourseStatusEnum status){
        return courseService.getCourseByAccount(status);
    }

    @PostMapping(value ="/files/upload", consumes = "multipart/form-data")
    public String handleFileUpload(@ModelAttribute @RequestParam("file") MultipartFile file) {
        try {
            // Start the file upload process
            int maxWidthSizeImage = 1000;
            uploadFile.uploadFile("filename", file, maxWidthSizeImage);
            return "File uploaded successfully!";
        } catch (IOException e) {
            e.printStackTrace(); // Handle exception as needed
            return "File upload failed: " + e.getMessage();
        }
    }

    @GetMapping("/view-course-by-account/{accountId}")
    public List<CourseDTO> getCoursesByAccountId(@PathVariable Integer accountId) {
        return courseService.getCoursesByAccount(accountId);
    }
}
