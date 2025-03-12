package com.ojt.mockproject.controller;

import com.ojt.mockproject.dto.Report.ReportCreateRequestDTO;
import com.ojt.mockproject.dto.Report.ReportResponseDTO;
import com.ojt.mockproject.dto.Report.ReportUpdateRequestDTO;
import com.ojt.mockproject.entity.Report;
import com.ojt.mockproject.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/report")
@CrossOrigin("*")
@Validated
public class ReportController {
    @Autowired
    private ReportService reportService;

    //CREATE
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT', 'ADMIN')")
    @PostMapping("/create/{courseId}")
    public ReportResponseDTO createReport(@PathVariable Integer courseId, @RequestBody ReportCreateRequestDTO reportCreateRequestDTO) {
        return reportService.createReport(courseId, reportCreateRequestDTO);
    }

    //READ (get all report)
    @GetMapping("/read")
    public List<ReportResponseDTO> readReport() {
        return reportService.readReport();
    }

    //DELETE
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT', 'ADMIN')")
    @DeleteMapping("/delete/{reportId}")
    public ResponseEntity<String> deleteReport(@PathVariable int reportId) {
        reportService.deleteReport(reportId);
        return ResponseEntity.ok("Deleted Successfully");
    }

    //UPDATE
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT', 'ADMIN')")
    @PutMapping("/update/{reportId}")
    public ResponseEntity<String> updateReport(@PathVariable int reportId, @RequestBody ReportUpdateRequestDTO reportUpdateRequestDTO) {
        reportService.updateReport(reportId, reportUpdateRequestDTO);
        return ResponseEntity.ok("Updated Successfully");
    }

}
