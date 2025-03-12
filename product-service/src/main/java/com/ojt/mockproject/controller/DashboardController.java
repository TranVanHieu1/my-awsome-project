package com.ojt.mockproject.controller;

import com.ojt.mockproject.dto.Dashboard.*;
import com.ojt.mockproject.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/dashboard")
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;


    //DashBoard student
    @GetMapping("/student")
    public DashboardStudentResponse dashboardStudent(){
        return dashboardService.getDashboardStudent();
    }

    //Dashboard instructor
    @GetMapping("/instructor")
    public DashboardInstructorResponse dashboardInstructor(){
        return dashboardService.getDashboardInstructor();
    }

    //View Name, Purchased, Views for each instructor's course
    @GetMapping("/courses")
    public List<CoursesDashboardResponse> dashboardCoursesByInstructor(){return dashboardService.getCourseDashboardByInstructor();}

    //View Course is Pending by instructor account
    @GetMapping("/courses-pending")
    public List<CoursePendingResponese> coursePendingByInstructor(){return dashboardService.coursePendingByInstructor();}

    //View Total Sale in Week and sales per day within 7 days
    @GetMapping("/weakly-sales")
    public WeeklySalesResponse getWeaklySales(){
        return dashboardService.weeklySales();
    }

    //Sales by month of the year
    @GetMapping("/Sale-of-the-year")
    public SalesOfYearResponse getSalesOfTheYear(){
        return dashboardService.salesOfYear();
    }
}
