package com.ojt.mockproject.controller;


import com.ojt.mockproject.dto.Account.Responses.ViewAccountResponseDTO;
import com.ojt.mockproject.dto.ApiRequestLog.Responses.ApiRequestLogResponse;
import com.ojt.mockproject.dto.Course.CourseResponseDTO;
import com.ojt.mockproject.dto.Report.ReportResponseDTO;
import com.ojt.mockproject.entity.ApiRequestLog;
import com.ojt.mockproject.entity.Enum.AccountRoleEnum;
import com.ojt.mockproject.entity.Enum.AccountStatusEnum;
import com.ojt.mockproject.entity.Enum.CourseStatusEnum;
import com.ojt.mockproject.service.AdminService;
import com.ojt.mockproject.service.RedisService;
import com.ojt.mockproject.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @Mock
    private ReportService reportService;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private AdminController adminController;

    private static final Integer COURSE_ID = 1;
    private static final Integer ACCOUNT_ID = 1;
    @BeforeEach
    void setUp() {
    }

    @Test
    public void testGetAccountsByRole() {
        List<ViewAccountResponseDTO> accounts = Arrays.asList(
                new ViewAccountResponseDTO(), new ViewAccountResponseDTO());
        when(adminService.getAccountsByRole(AccountRoleEnum.INSTRUCTOR)).thenReturn(accounts);

        ResponseEntity<List<ViewAccountResponseDTO>> result = adminController.getAccountsByRole(AccountRoleEnum.INSTRUCTOR);
        verify(adminService, times(1)).getAccountsByRole(AccountRoleEnum.INSTRUCTOR);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(accounts, result.getBody());
    }

    @Test
    public void testGetAccountsByStatus() {
        List<ViewAccountResponseDTO> accounts = Arrays.asList(
                new ViewAccountResponseDTO(), new ViewAccountResponseDTO());
        when(adminService.getAccountsByStatus(AccountStatusEnum.VERIFIED)).thenReturn(accounts);

        ResponseEntity<List<ViewAccountResponseDTO>> result = adminController.getAccountsByStatus(AccountStatusEnum.VERIFIED);
        verify(adminService, times(1)).getAccountsByStatus(AccountStatusEnum.VERIFIED);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(accounts, result.getBody());
    }

    @Test
    public void testGetAccountsByRoleAndStatus() {
        List<ViewAccountResponseDTO> accounts = Arrays.asList(
                new ViewAccountResponseDTO(), new ViewAccountResponseDTO());
        when(adminService.getAccountsByRoleAndStatus(AccountRoleEnum.INSTRUCTOR, AccountStatusEnum.VERIFIED)).thenReturn(accounts);

        ResponseEntity<List<ViewAccountResponseDTO>> result = adminController.getAccountsByRoleAndStatus(AccountRoleEnum.INSTRUCTOR, AccountStatusEnum.VERIFIED);
        verify(adminService, times(1)).getAccountsByRoleAndStatus(AccountRoleEnum.INSTRUCTOR, AccountStatusEnum.VERIFIED);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(accounts, result.getBody());
    }


    @Test
    public void testApproveInstructorRegistration() {
        ResponseEntity<String> result = adminController.approveInstructorRegistration(ACCOUNT_ID);
        verify(adminService, times(1)).approveInstructorRegistration(ACCOUNT_ID);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Account with ID " + ACCOUNT_ID + " has been approved.", result.getBody());
    }

    @Test
    public void testRejectInstructorRegistration() {
        ResponseEntity<String> result = adminController.rejectInstructorRegistration(ACCOUNT_ID);
        verify(adminService, times(1)).rejectInstructorRegistration(ACCOUNT_ID);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Account with ID " + ACCOUNT_ID + " has been rejected.", result.getBody());
    }

    @Test
    public void testGetAllCourses() {
        List<CourseResponseDTO> courses = Arrays.asList(
                new CourseResponseDTO(), new CourseResponseDTO());
        when(adminService.getAllCoursesByAdmin()).thenReturn(courses);

        List<CourseResponseDTO> result = adminController.getAllCourses();

        verify(adminService, times(1)).getAllCoursesByAdmin();
        assertEquals(courses, result);
    }

    @Test
    public void testGetCourseById() {
        CourseResponseDTO course = new CourseResponseDTO();
        when(adminService.getCourseById(COURSE_ID)).thenReturn(course);

        ResponseEntity<CourseResponseDTO> result = adminController.getCourseById(COURSE_ID);

        verify(adminService, times(1)).getCourseById(COURSE_ID);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(course, result.getBody());
    }

    @Test
    public void testGetCourseByAccountId() {
        List<CourseResponseDTO> courses = Arrays.asList(
                new CourseResponseDTO(), new CourseResponseDTO());
        when(adminService.getCourseByAccountId(ACCOUNT_ID)).thenReturn(courses);

        List<CourseResponseDTO> result = adminController.getCourseByAccountId(ACCOUNT_ID);

        verify(adminService, times(1)).getCourseByAccountId(ACCOUNT_ID);
        assertEquals(courses, result);
    }

    @Test
    public void testGetCourseByStatus() {
        List<CourseResponseDTO> courses = Arrays.asList(
                new CourseResponseDTO(), new CourseResponseDTO());
        when(adminService.getCourseByStatus(CourseStatusEnum.UNBLOCKED)).thenReturn(courses);

        List<CourseResponseDTO> result = adminController.getCourseByStatus(CourseStatusEnum.UNBLOCKED);

        verify(adminService, times(1)).getCourseByStatus(CourseStatusEnum.UNBLOCKED);
        assertEquals(courses, result);
    }

    @Test
    public void testApproveCourse() {
        ResponseEntity<String> result = adminController.approveCourse(COURSE_ID);

        verify(adminService, times(1)).approveCourse(COURSE_ID);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Course with ID " + COURSE_ID + " has been approved.", result.getBody());
    }

    @Test
    public void testRejectCourse() {
        ResponseEntity<String> result = adminController.rejectCourse(COURSE_ID);

        verify(adminService, times(1)).rejectCourse(COURSE_ID);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Course with ID " + COURSE_ID + " has been rejected.", result.getBody());
    }

    @Test
    public void testBlockCourse(){
        ResponseEntity<String> result = adminController.blockCourse(COURSE_ID);

        verify(adminService, times(1)).blockCourse(COURSE_ID);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Course with ID " + COURSE_ID + " has been blocked.", result.getBody());
    }

    @Test
    public void testUnblockCourse(){
        ResponseEntity<String> result = adminController.unblockCourse(COURSE_ID);

        verify(adminService, times(1)).unblockCourse(COURSE_ID);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Course with ID " + COURSE_ID + " has been unblocked.", result.getBody());
    }

    @Test
    public void testBlockInstructor(){
        ResponseEntity<String> result = adminController.blockInstructorRegistration(ACCOUNT_ID);

        verify(adminService, times(1)).blockInstructor(ACCOUNT_ID );
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Instructor with ID " + ACCOUNT_ID + " has been blocked.", result.getBody());
    }

    @Test
    public void testUnblockInstructor(){
        ResponseEntity<String> result = adminController.unblockInstructorRegistration(ACCOUNT_ID);

        verify(adminService, times(1)).unblockInstructor(ACCOUNT_ID );
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Instructor with ID " + ACCOUNT_ID + " has been unblocked.", result.getBody());
    }

    @Test
    public void testBlockStudent(){
        ResponseEntity<String> result = adminController.blockStudentRegistration(ACCOUNT_ID);

        verify(adminService, times(1)).blockStudent(ACCOUNT_ID );
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Student with ID " + ACCOUNT_ID + " has been blocked.", result.getBody());
    }

    @Test
    public void testUnblockStudent(){
        ResponseEntity<String> result = adminController.unblockStudentRegistration(ACCOUNT_ID);

        verify(adminService, times(1)).unblockStudent(ACCOUNT_ID );
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Student with ID " + ACCOUNT_ID + " has been unblocked.", result.getBody());
    }

    @Test
    public void testGetReportByCourseId() {
        List<ReportResponseDTO> reports = Arrays.asList(
                new ReportResponseDTO(), new ReportResponseDTO());
        when(reportService.getReportByCourseId(COURSE_ID)).thenReturn(reports);

        List<ReportResponseDTO> result = adminController.getReportByCourseId(COURSE_ID);

        verify(reportService, times(1)).getReportByCourseId(COURSE_ID);
        assertEquals(reports, result);
    }

    @Test
    public void testApproveInstructor() {
        ResponseEntity<String> result = adminController.approveInstructor(ACCOUNT_ID);

        verify(adminService, times(1)).approveInstructor(ACCOUNT_ID);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Converted to instructor", result.getBody());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testGetAllApiRequestLogs() {
        // Given
        ApiRequestLogResponse log1 = new ApiRequestLogResponse();
        log1.setId(1);
        ApiRequestLogResponse log2 = new ApiRequestLogResponse();
        log2.setId(2);

        List<ApiRequestLogResponse> logList = Arrays.asList(log1, log2);

        when(adminService.getAllApiRequestLogs()).thenReturn(logList);

        // When
        ResponseEntity<List<ApiRequestLogResponse>> response = adminController.getAllApiRequestLogs();

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(logList, response.getBody());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testGetApiRequestLogById() {
        // Given
        ApiRequestLog log1 = new ApiRequestLog();
        log1.setId(1);
        ApiRequestLog log2 = new ApiRequestLog();
        log2.setId(2);

        List<ApiRequestLog> logList = Arrays.asList(log1, log2);

        when(adminService.getApiRequestLogByIpAddress("192.168.1.1")).thenReturn(logList);

        // When
        ResponseEntity<List<ApiRequestLog>> response = adminController.getApiRequestLogById("192.168.1.1");

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(logList, response.getBody());
    }
    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testDeleteKey() {
        // Given
        String key = "testKey";
        doNothing().when(redisService).deleteKey(key);

        // When
        ResponseEntity<String> response = adminController.deleteKey(key);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Key deleted successfully", response.getBody());
        verify(redisService).deleteKey(key);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testGetAllBlacklistedIps() {
        // Given
        List<String> blacklistedIps = Arrays.asList("192.168.1.1", "192.168.1.2");
        when(redisService.getAllBlacklistedIps()).thenReturn(blacklistedIps);

        // When
        ResponseEntity<List<String>> response = adminController.getAllBlacklistedIps();

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(blacklistedIps, response.getBody());
    }
}

