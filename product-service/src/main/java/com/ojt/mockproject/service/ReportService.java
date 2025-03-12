package com.ojt.mockproject.service;

import com.ojt.mockproject.dto.Report.ReportCreateRequestDTO;
import com.ojt.mockproject.dto.Report.ReportResponseDTO;
import com.ojt.mockproject.dto.Report.ReportUpdateRequestDTO;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.Enum.ReportCateEnum;
import com.ojt.mockproject.entity.Report;
import com.ojt.mockproject.exceptionhandler.ErrorCode;
import com.ojt.mockproject.exceptionhandler.ReportExceptionHandler;
import com.ojt.mockproject.exceptionhandler.course.CourseException;
import com.ojt.mockproject.repository.CourseRepository;
import com.ojt.mockproject.repository.ReportRepository;
import com.ojt.mockproject.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {
    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AccountUtils accountUtils;

    public void copyReportDetails(Report oldReport, Report newReport, Course newCourse) {
        newReport.setAccount(oldReport.getAccount());
        newReport.setCourse(newCourse);
        newReport.setDescription(oldReport.getDescription());
        newReport.setReportCategory(oldReport.getReportCategory());
        newReport.setCreateAt(LocalDateTime.now());
        newReport.setIsDeleted(false);
    }

    //CREATE 1 report mới
    public ReportResponseDTO createReport(Integer courseId, ReportCreateRequestDTO reportRequest) {
        try {
            Account curAcc = accountUtils.getCurrentAccount();
            Course course = courseRepository.findById(courseId).orElseThrow(() ->
                      new ReportExceptionHandler("No Course founded", ErrorCode.COURSE_NOT_FOUND));
            if (reportRequest.getDescription().isEmpty()) {
                throw new ReportExceptionHandler("Description can't NULL", ErrorCode.INVALID_INPUT);
            }

            Report report = new Report();
            report.setAccount(curAcc);
            report.setCourse(course);
            report.setReportCategory(ReportCateEnum.NOTHING);
            report.setDescription(reportRequest.getDescription());
            report.setCreateAt(LocalDateTime.now());
            report.setUpdateAt(LocalDateTime.now());
            report.setIsDeleted(Boolean.FALSE);
            reportRepository.save(report);

            return convertToReportResponseDTO(report);
        } catch (ReportExceptionHandler e) {
            throw e;
        } catch (ClassCastException e) {
            throw new ReportExceptionHandler("Login to Report", ErrorCode.NOT_LOGIN);
        } catch (Exception e) {
            throw new ReportExceptionHandler("Error creating report", ErrorCode.INTERNAL_SERVER_ERROR);

        }
    }


    //READ (Get All Report có trong DB)
    public List<ReportResponseDTO> readReport() {
        List<Report> report = reportRepository.findAll();
        return convertToReportResponseDTO(report);
    }



    //DELETE report
    public void deleteReport(int reportId) {
        if (reportRepository.findById(reportId).isEmpty()) {
            throw new ReportExceptionHandler("No Report founded", ErrorCode.COURSE_NOT_FOUND);
        }
        reportRepository.deleteById(reportId);
    }


    //UPDATE report có sẵn
    public void updateReport(int id, ReportUpdateRequestDTO reportRequest) {
        try {
            if (reportRepository.findById(id).isEmpty()) {
                throw new ReportExceptionHandler("No Report Founded", ErrorCode.COURSE_NOT_FOUND);
            }
            Report report = reportRepository.findById(id).orElseThrow(() -> new ReportExceptionHandler("Error updating report", ErrorCode.INTERNAL_SERVER_ERROR));

            if (reportRequest.getDescription().isEmpty()) {
                throw new ReportExceptionHandler("Description can't NULL", ErrorCode.INVALID_INPUT);
            }

            if (!reportRequest.getReportCategory().equals("SCAM") && !reportRequest.getReportCategory().equals("NOTHING")){
                throw new ReportExceptionHandler("Invalid Enum Format, Category -> SCAM | NOTHING", ErrorCode.INVALID_ENUM);
            }

            report.setDescription(reportRequest.getDescription());
            report.setReportCategory(ReportCateEnum.valueOf(reportRequest.getReportCategory()));
            report.setUpdateAt(LocalDateTime.now());
            report.setIsDeleted(reportRequest.getIsDeleted());
            reportRepository.save(report);
        } catch (ReportExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            throw new ReportExceptionHandler("Error updating report", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public ReportResponseDTO convertToReportResponseDTO(Report report) {
        return new ReportResponseDTO(
                report.getId(),
                report.getDescription(),
                report.getReportCategory(),
                report.getUpdateAt(),
                report.getCreateAt(),
                report.getAccount().getId(),
                report.getCourse().getId(),
                report.getIsDeleted()
        );
    }

    //--- View Report By CourseId
    public List<ReportResponseDTO> getReportByCourseId(Integer courseId) {
        try {
            List<Report> reports = reportRepository.findByCourseId(courseId);
            return reports.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CourseException("Failed to retrieve course with account id " + courseId, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private ReportResponseDTO convertToDTO(Report report) {
        ReportResponseDTO dto = new ReportResponseDTO();
        dto.setId(report.getId());
        dto.setDescription(report.getDescription());
        dto.setReportCategory(report.getReportCategory());
        dto.setCreateAt(report.getCreateAt());
        dto.setUpdateAt(report.getUpdateAt());
        dto.setIsDeleted(report.getIsDeleted());
        dto.setAccountId(report.getAccount().getId());
        dto.setCourseId(report.getCourse().getId());
        return dto;
    }

    public List<ReportResponseDTO> convertToReportResponseDTO(List<Report> report) {
        List<ReportResponseDTO> reportResponseDTOList = new ArrayList<>();
        for (Report reportList : report) {
            reportResponseDTOList.add(new ReportResponseDTO(
                    reportList.getId(),
                    reportList.getDescription(),
                    reportList.getReportCategory(),
                    reportList.getUpdateAt(),
                    reportList.getCreateAt(),
                    reportList.getAccount().getId(),
                    reportList.getCourse().getId(),
                    reportList.getIsDeleted()
            ));
        }
        return reportResponseDTOList;
    }
}
