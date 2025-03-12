package com.ojt.mockproject.controller;

import com.ojt.mockproject.dto.CrashCourseVideo.CrashCourseRequestDTO;
import com.ojt.mockproject.dto.CrashCourseVideo.CrashCourseResponseDTO;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Media;
import com.ojt.mockproject.service.CrashCourseVideoService;
import com.ojt.mockproject.utils.AccountUtils;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/media")
public class MediaController {
    @Autowired
    private CrashCourseVideoService crashCourseVideoService;
    @Autowired
    private AccountUtils accountUtils;

    @GetMapping("/get/{id}")
    public ResponseEntity getCrashCourseVideosByCourse(@PathVariable Integer id){
        List<Media> crashCourseVideos = crashCourseVideoService.getCrashCourseVideoByCourse(id);
        return ResponseEntity.ok(crashCourseVideos);
    }


    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity createCrashCourseVideo(@ModelAttribute @RequestBody CrashCourseRequestDTO crashCourseRequestDTO, ServletRequest request){
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String url = httpRequest.getRequestURI();
        Account account = accountUtils.getCurrentAccount();
        CrashCourseResponseDTO crashCourseVideo = crashCourseVideoService.createCrashCourseVideo(crashCourseRequestDTO, url, account);
        return ResponseEntity.ok(crashCourseVideo);
    }

    @PutMapping(value = "/update/{id}", consumes = "multipart/form-data")
    public ResponseEntity updateCrashCourseVideo(@ModelAttribute @RequestBody CrashCourseRequestDTO crashCourseRequestDTO, @PathVariable Integer id,  ServletRequest request){
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String url = httpRequest.getRequestURI();
        Account account = accountUtils.getCurrentAccount();
        return ResponseEntity.ok(crashCourseVideoService.updateCrashCourseVideo(crashCourseRequestDTO, id, url, account));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteCrashCourseVideo(@PathVariable Integer id){
        return ResponseEntity.ok(crashCourseVideoService.deleteCrashCourseVideo(id));
    }
}


