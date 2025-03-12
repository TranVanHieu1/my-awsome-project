package com.ojt.mockproject.controller;

import com.ojt.mockproject.dto.Report.ReportCreateRequestDTO;
import com.ojt.mockproject.dto.Report.ReportResponseDTO;
import com.ojt.mockproject.dto.Report.ReportUpdateRequestDTO;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.Enum.*;
import com.ojt.mockproject.entity.Report;
import com.ojt.mockproject.exceptionhandler.ErrorCode;
import com.ojt.mockproject.exceptionhandler.ReportExceptionHandler;
import com.ojt.mockproject.service.ReportService;
import com.ojt.mockproject.utils.AccountUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportControllerTest {
    @Mock
    private ReportService reportService;

    @Mock
    private AccountUtils accountUtils;

    @InjectMocks
    private ReportController reportController;

    private ReportCreateRequestDTO createRequest;
    private ReportUpdateRequestDTO updateRequest;
    private ReportResponseDTO response;
    private List<ReportResponseDTO> mockReports = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        createRequest = ReportCreateRequestDTO.builder()
                .description("test Description")
                .build();

        updateRequest = ReportUpdateRequestDTO.builder()
                .description("Test Update Description")
                .reportCategory("NOTHING")
                .isDeleted(Boolean.TRUE)
                .build();

        response = ReportResponseDTO.builder()
                .id(1)
                .description("Test Create Description")
                .reportCategory(ReportCateEnum.NOTHING)
                .updateAt(LocalDateTime.now())
                .createAt(LocalDateTime.now())
                .accountId(1)
                .courseId(1)
                .isDeleted(Boolean.FALSE)
                .build();
        mockReports.add(response);
    }

    @Test
    public void createReportTest() throws Exception {
        when(reportService.createReport(any(Integer.class), any(ReportCreateRequestDTO.class))).thenReturn(response);
        ReportResponseDTO result = reportController.createReport(1, createRequest);
        assertEquals(response, result);
        assertEquals(response.getDescription(), result.getDescription());
        verify(reportService, times(1)).createReport(any(Integer.class), any(ReportCreateRequestDTO.class));
    }

    @Test
    public void createReport_DescriptionEmpty() {
        ReportCreateRequestDTO requestDTO = new ReportCreateRequestDTO();
        requestDTO.setDescription("");
        doThrow(new ReportExceptionHandler("Description can't NULL", ErrorCode.INVALID_INPUT))
                .when(reportService).createReport(any(Integer.class), eq(requestDTO));
        ReportExceptionHandler exception = assertThrows(ReportExceptionHandler.class,
                () -> reportController.createReport(1, requestDTO));

        assertEquals("Description can't NULL", exception.getMessage());
        assertEquals(ErrorCode.INVALID_INPUT, exception.getErrorCode());
        verify(reportService, times(1)).createReport(any(Integer.class), eq(requestDTO));
    }

    @Test
    public void CreateReport_NoCurrentAccount() {
        try {
            reportController.createReport(1, createRequest);
        } catch (ReportExceptionHandler e) {
            assertEquals("Login to Report", e.getMessage());
            assertEquals(ErrorCode.NOT_LOGIN, e.getErrorCode());
        }
    }

    @Test
    public void readReport_Success() {
        when(reportService.readReport()).thenReturn(mockReports);
        List<ReportResponseDTO> result = reportController.readReport();

        assertEquals(mockReports.size(), result.size());
        assertEquals(mockReports.get(0), result.get(0));
        verify(reportService, times(1)).readReport();
    }

    @Test
    public void deleteReport_Success() {
        doNothing().when(reportService).deleteReport(1);
        ResponseEntity<String> responseEntity = reportController.deleteReport(1);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Deleted Successfully", responseEntity.getBody());
        verify(reportService, times(1)).deleteReport(1);
    }

    @Test
    public void deleteReport_NotFound() {
        doThrow(new ReportExceptionHandler("No Report founded", ErrorCode.COURSE_NOT_FOUND))
                .when(reportService).deleteReport(1);
        ReportExceptionHandler exception = assertThrows(ReportExceptionHandler.class,
                () -> reportController.deleteReport(1));

        assertEquals("No Report founded", exception.getMessage());
        assertEquals(ErrorCode.COURSE_NOT_FOUND, exception.getErrorCode());

        verify(reportService, times(1)).deleteReport(1);
    }

    @Test
    public void updateReport_Success() {
        doNothing().when(reportService).updateReport(1, updateRequest);
        ResponseEntity<String> responseEntity = reportController.updateReport(1, updateRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Updated Successfully", responseEntity.getBody());
        verify(reportService, times(1)).updateReport(1, updateRequest);
    }

    @Test
    public void updateReport_DescriptionEmpty() {
        ReportUpdateRequestDTO requestDTO = new ReportUpdateRequestDTO();
        requestDTO.setDescription("");

        doThrow(new ReportExceptionHandler("Description can't NULL", ErrorCode.INVALID_INPUT))
                .when(reportService).updateReport(1, requestDTO);
        ReportExceptionHandler exception = assertThrows(ReportExceptionHandler.class,
                () -> reportController.updateReport(1, requestDTO));

        assertEquals("Description can't NULL", exception.getMessage());
        assertEquals(ErrorCode.INVALID_INPUT, exception.getErrorCode());
        verify(reportService, times(1)).updateReport(1, requestDTO);
    }
}
