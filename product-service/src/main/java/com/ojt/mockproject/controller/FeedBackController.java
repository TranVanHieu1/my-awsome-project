package com.ojt.mockproject.controller;
import com.ojt.mockproject.dto.Feedback.DeleteResponseDTO;
import com.ojt.mockproject.dto.Feedback.FeedBackRequestDTO;
import com.ojt.mockproject.dto.Feedback.FeedBackResponseDTO;
import com.ojt.mockproject.entity.Feedback;
import com.ojt.mockproject.service.FeedBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/feedback")
public class FeedBackController {

    @Autowired
    private FeedBackService feedBackService;

    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping("/create-feedback")
    public FeedBackResponseDTO createFeedBack(@RequestBody FeedBackRequestDTO feedBackRequestDTO) throws Exception {
            return feedBackService.addFeedBack(feedBackRequestDTO);
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @DeleteMapping("/delete-feedback/{id}")
    public DeleteResponseDTO removeFeedBack(@PathVariable Integer id) throws Exception {
            return feedBackService.deleteFeedBack(id);
    }

    @GetMapping("/read-feedback")
    public List<FeedBackResponseDTO> readFeedBack() throws Exception {
           return feedBackService.getAllFeedBack();
    }

    @GetMapping("/read-feedback-by-course/{id}")
    public List<FeedBackResponseDTO> readFeedbackByCourse(@PathVariable Integer id) throws Exception {
        return feedBackService.getFeebackByCourse(id);
    }
}
