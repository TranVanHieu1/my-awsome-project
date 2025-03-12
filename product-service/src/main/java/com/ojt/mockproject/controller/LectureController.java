package com.ojt.mockproject.controller;

import com.ojt.mockproject.dto.SmallCourseVideo.SmallVideoRequestDTO;
import com.ojt.mockproject.dto.SmallCourseVideo.SmallVideoResponseDTO;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Lecture;
import com.ojt.mockproject.service.SmallCourseVideoService;
import com.ojt.mockproject.utils.AccountUtils;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/lecture")
public class LectureController {
    @Autowired
    private SmallCourseVideoService smallCourseVideoService;
    @Autowired
    private AccountUtils accountUtils;

    @GetMapping("/get/course-chapter/{courseChapterId}")
    public ResponseEntity getSmallCourseVideosByCourseChapter(@PathVariable Integer courseChapterId){
        List<Lecture> smallCourseVideos = smallCourseVideoService.getSmallCourseVideosByCourseChapterId(courseChapterId);
        return ResponseEntity.ok(smallCourseVideos);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity getSmallCourseVideosById(@PathVariable Integer id){
        Lecture smallCourseVideo = smallCourseVideoService.getSmallCourseVideosById(id);
        return ResponseEntity.ok(smallCourseVideo);
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR')")
    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity createSmallCourseVideo(@ModelAttribute @RequestBody SmallVideoRequestDTO smallVideoRequestDTO, ServletRequest request){
        Account account = accountUtils.getCurrentAccount();
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String url = httpRequest.getRequestURI();
        SmallVideoResponseDTO smallCourseVideo = smallCourseVideoService.createSmallCourseVideo(smallVideoRequestDTO, account, url);
        return ResponseEntity.ok(smallCourseVideo);
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR')")
    @PutMapping("/update/{id}")
    public ResponseEntity updateSmallCourseVideo(@RequestBody SmallVideoRequestDTO smallVideoRequestDTO, @PathVariable Integer id){
        return ResponseEntity.ok(smallCourseVideoService.updateSmallCourseVideo(smallVideoRequestDTO, id));
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteSmallCourseVideo(@PathVariable Integer id){
        return ResponseEntity.ok(smallCourseVideoService.deleteSmallCourseVideo(id));
    }
}


