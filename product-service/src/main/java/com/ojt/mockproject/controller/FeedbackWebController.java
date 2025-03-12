package com.ojt.mockproject.controller;

import com.ojt.mockproject.dto.FeedbackWeb.FeedbackWebRequest;
import com.ojt.mockproject.dto.FeedbackWeb.FeedbackWebResponse;
import com.ojt.mockproject.service.FeedbackWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/feedback-webpage")
public class FeedbackWebController {

    @Autowired
    private FeedbackWebService feedbackWedService;

    @PreAuthorize("hasAuthority('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    @PostMapping("/create-feedback")
    public FeedbackWebResponse createFeedBack(@RequestBody FeedbackWebRequest feedbackWebRequest) throws Exception {
        return feedbackWedService.addFeedbackWeb(feedbackWebRequest);
    }

    @GetMapping("/view-feedback-webpage")
    public  FeedbackWebResponse viewFeedback(){
        return  feedbackWedService.getFeedbackWeb();
    }
}
