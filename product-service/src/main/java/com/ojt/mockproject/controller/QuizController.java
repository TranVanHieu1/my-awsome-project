package com.ojt.mockproject.controller;

import com.ojt.mockproject.dto.Quiz.QuizRequestDTO;
import com.ojt.mockproject.dto.Quiz.QuizResponseDTO;
import com.ojt.mockproject.dto.Quiz.SubmitQuizRequestDTO;
import com.ojt.mockproject.dto.Quiz.SubmitQuizResponseDTO;
import com.ojt.mockproject.service.AccountService;
import com.ojt.mockproject.service.QuestionChoiceService;
import com.ojt.mockproject.service.QuestionService;
import com.ojt.mockproject.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@CrossOrigin("*")
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionChoiceService questionChoiceService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private AccountService accountService;

    //Create a list of question for the certificate
    //This endpoint will get Course's Quiz Information(Title, Description), list of Question, each Question will have 4 choices
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR')")
    @PostMapping("/create-quiz/{courseId}")
    public QuizRequestDTO createQuiz(@PathVariable Integer courseId, @RequestBody QuizRequestDTO quizRequestDTO) {
        return quizService.createQuizForCourse(quizRequestDTO, courseId);
    }

    //Student take quiz based on courseId to get certificate
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT')")
    @GetMapping("take-quiz/{courseId}")
    public QuizResponseDTO getQuizByCourse(@PathVariable Integer courseId) throws IOException {
        return quizService.takeQuizByCourseId(courseId);
    }

    //Take quiz's result from student and calculate, and response.
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT')")
    @PostMapping("/submit")
    public SubmitQuizResponseDTO submitQuizTest(@RequestBody SubmitQuizRequestDTO submitQuizRequestDTO){
        return quizService.submitsubmitQuizTest(submitQuizRequestDTO);
    }

    //Update the question (question only)
}
