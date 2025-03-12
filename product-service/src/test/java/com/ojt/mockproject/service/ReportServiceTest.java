package com.ojt.mockproject.service;

import com.ojt.mockproject.dto.Report.ReportCreateRequestDTO;
import com.ojt.mockproject.dto.Report.ReportResponseDTO;
import com.ojt.mockproject.dto.Report.ReportUpdateRequestDTO;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.Enum.*;
import com.ojt.mockproject.entity.Report;
import com.ojt.mockproject.exceptionhandler.ErrorCode;
import com.ojt.mockproject.exceptionhandler.ReportExceptionHandler;
import com.ojt.mockproject.exceptionhandler.course.CourseException;
import com.ojt.mockproject.repository.CourseRepository;
import com.ojt.mockproject.repository.ReportRepository;
import com.ojt.mockproject.utils.AccountUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {
    @Mock
    private ReportRepository reportRepository;

    @Mock
    private AccountUtils accountUtils;

    @Mock
    private CourseRepository courseRepository;


    @InjectMocks
    private ReportService reportService;


    private final int courseId = 1;
    private Report report;
    private ReportCreateRequestDTO reportCreateRequestDTO;
    private ReportUpdateRequestDTO reportUpdateRequestDTO;
    private Account account;

    @BeforeEach
    void setUp() throws Exception {
        //TẠO 1 REPORT CÓ SẴN TRONG DB
        report = new Report();
        report.setId(2);
        report.setDescription("Old Description");
        report.setReportCategory(ReportCateEnum.NOTHING);
        report.setUpdateAt(LocalDateTime.now());
        report.setIsDeleted(false);
        report.setAccount(new Account());
        report.setCourse(new Course());

        account = Account.builder()
                .id(1)
                .name("John Doe")
                .email("johndoe@example.com")
                .password("password")
                .phone("1234567890")
                .gender(AccountGenderEnum.MALE)
                .avatar("avatar.png")
                .role(AccountRoleEnum.INSTRUCTOR)
                .wishlist("wishlist")
                .purchasedCourse("1,2,3")
                .provider(AccountProviderEnum.LOCAL)
                .status(AccountStatusEnum.APPROVED)
                .createAt(LocalDateTime.now())
                .isDeleted(false)
                .isInstructorVerify(false)
                .build();

        reportCreateRequestDTO = ReportCreateRequestDTO.builder()
                .description("Test description")
                .build();

        reportUpdateRequestDTO = ReportUpdateRequestDTO.builder()
                .description("Test description")
                .reportCategory("SCAM")
                .isDeleted(Boolean.TRUE)
                .build();

    }

    @Test
    void createReportTest() {
        when(accountUtils.getCurrentAccount()).thenReturn(account);
        Course mockCourse = new Course();
        when(courseRepository.findById(any(Integer.class))).thenReturn(Optional.of(mockCourse));

        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
            Report reportToSave = invocation.getArgument(0);
            reportToSave.setId(1);
            return reportToSave;
        });

        ReportResponseDTO actualResponse = reportService.createReport(1, reportCreateRequestDTO);

        assertNotNull(actualResponse);
        assertEquals(reportCreateRequestDTO.getDescription(), actualResponse.getDescription());

        verify(accountUtils, times(1)).getCurrentAccount();
        verify(courseRepository, times(1)).findById(any(Integer.class));
        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    void createReport_CourseNotFound() {
        when(accountUtils.getCurrentAccount()).thenReturn(account);
        when(courseRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        assertThrows(ReportExceptionHandler.class, () -> reportService.createReport(1, reportCreateRequestDTO));
        verify(reportRepository, never()).save(any());
    }

    @Test
    void createReport_NullDescription() {
        ReportCreateRequestDTO requestDTO = new ReportCreateRequestDTO();
        requestDTO.setDescription("");
        when(accountUtils.getCurrentAccount()).thenReturn(account);
        when(courseRepository.findById(any(Integer.class))).thenReturn(Optional.of(new Course()));

        ReportExceptionHandler exception = assertThrows(ReportExceptionHandler.class,
                () -> reportService.createReport(1, requestDTO));
        assertEquals("Description can't NULL", exception.getMessage());
        verify(reportRepository, never()).save(any(Report.class));
    }

    @Test
    void createReport_NullCurrentAccount() {
        when(accountUtils.getCurrentAccount()).thenThrow(ClassCastException.class);

        ReportExceptionHandler exception = assertThrows(ReportExceptionHandler.class, () -> {
            reportService.createReport(1, reportCreateRequestDTO);
        });
        assertEquals("Login to Report", exception.getMessage());
        assertEquals(ErrorCode.NOT_LOGIN, exception.getErrorCode());
        verify(reportRepository, never()).save(any(Report.class));
    }

    @Test
    void updateReportTest() {
        when(reportRepository.findById(2)).thenReturn(Optional.of(report));
        reportService.updateReport(2, reportUpdateRequestDTO);

        assertEquals(reportUpdateRequestDTO.getDescription(), report.getDescription());
        assertEquals(reportUpdateRequestDTO.getReportCategory(), report.getReportCategory().toString());
        assertEquals(reportUpdateRequestDTO.getIsDeleted(), report.getIsDeleted());

        verify(reportRepository, times(2)).findById(2);
        verify(reportRepository, times(1)).save(any(Report.class));
    }
    @Test
    void updateReport_NotFound() {
        when(reportRepository.findById(2)).thenReturn(Optional.empty());
        assertThrows(ReportExceptionHandler.class, () -> reportService.updateReport(2, reportUpdateRequestDTO));
        verify(reportRepository, never()).save(any());
    }

    @Test
    void updateReport_NullDescription() {
        ReportUpdateRequestDTO requestDTO = new ReportUpdateRequestDTO();
        requestDTO.setDescription("");
        when(reportRepository.findById(2)).thenReturn(Optional.of(report));
        ReportExceptionHandler exception = assertThrows(ReportExceptionHandler.class, () -> {
            reportService.updateReport(2, requestDTO);
        });
        assertEquals("Description can't NULL", exception.getMessage());
        verify(reportRepository, never()).save(any());
    }

    @Test
        void updateReport_InvalidEnum(){
        ReportUpdateRequestDTO requestDTO = new ReportUpdateRequestDTO();
        requestDTO.setDescription("Test Description");
        requestDTO.setReportCategory("");
        when(reportRepository.findById(2)).thenReturn(Optional.of(report));
        ReportExceptionHandler exception = assertThrows(ReportExceptionHandler.class,
                () -> reportService.updateReport(2, requestDTO));
        assertEquals("Invalid Enum Format, Category -> SCAM | NOTHING", exception.getMessage());
        verify(reportRepository, never()).save(any());
    }
    @Test
    void readReportsTest() {
        List<Report> reportList = new ArrayList<>();
        reportList.add(report);
        when(reportRepository.findAll()).thenReturn(reportList);

        List<ReportResponseDTO> result = reportService.readReport();

        assertEquals(reportList.size(), result.size());
        verify(reportRepository, times(1)).findAll();
    }

    @Test
    void deleteReportTest() {
        when(reportRepository.findById(2)).thenReturn(Optional.of(report));
        reportService.deleteReport(2);
        verify(reportRepository, times(1)).deleteById(2);
    }

    @Test
    void deleteReport_NotFound() {
        when(reportRepository.findById(2)).thenReturn(Optional.empty());
        assertThrows(ReportExceptionHandler.class, () -> reportService.deleteReport(2));
        verify(reportRepository, never()).deleteById(2);
    }

    @Test
    void getReportByCourseIdTest() {
        List<Report> reports = new ArrayList<>();
        reports.add(report);

        when(reportRepository.findByCourseId(courseId)).thenReturn(reports);

        List<ReportResponseDTO> result = reportService.getReportByCourseId(courseId);

        assertEquals(reports.size(), result.size());
        verify(reportRepository, times(1)).findByCourseId(courseId);
    }


    @Test
    void getReportByCourseId_InternalServerError() {
        when(reportRepository.findByCourseId(courseId)).thenThrow(RuntimeException.class);

        assertThrows(CourseException.class, () -> reportService.getReportByCourseId(courseId));
        verify(reportRepository, times(1)).findByCourseId(courseId);
    }

    @Test
    void convertToReportResponseDTO_ValidReport() {
        ReportResponseDTO dto = reportService.convertToReportResponseDTO(report);

        assertNotNull(dto);
        assertEquals(report.getId(), dto.getId());
        assertEquals(report.getDescription(), dto.getDescription());
        assertEquals(report.getReportCategory(), dto.getReportCategory());
        assertEquals(report.getUpdateAt(), dto.getUpdateAt());
        assertEquals(report.getCreateAt(), dto.getCreateAt());
        assertEquals(report.getAccount().getId(), dto.getAccountId());
        assertEquals(report.getCourse().getId(), dto.getCourseId());
        assertEquals(report.getIsDeleted(), dto.getIsDeleted());
    }

    @Test
    void convertToReportResponseDTO_ListOfReports() {
        List<Report> reports = new ArrayList<>();
        reports.add(report);

        List<ReportResponseDTO> dtos = reportService.convertToReportResponseDTO(reports);

        assertNotNull(dtos);
        assertEquals(1, dtos.size());

        ReportResponseDTO dto = dtos.get(0);
        assertEquals(report.getId(), dto.getId());
        assertEquals(report.getDescription(), dto.getDescription());
        assertEquals(report.getReportCategory(), dto.getReportCategory());
        assertEquals(report.getUpdateAt(), dto.getUpdateAt());
        assertEquals(report.getCreateAt(), dto.getCreateAt());
        assertEquals(report.getAccount().getId(), dto.getAccountId());
        assertEquals(report.getCourse().getId(), dto.getCourseId());
        assertEquals(report.getIsDeleted(), dto.getIsDeleted());
    }
}
