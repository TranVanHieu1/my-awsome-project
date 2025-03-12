package com.ojt.mockproject.controller;

import com.ojt.mockproject.dto.ApiRequestLog.Responses.ApiRequestLogResponse;
import com.ojt.mockproject.dto.Course.CourseResponseDTO;
import com.ojt.mockproject.dto.Report.ReportResponseDTO;
import com.ojt.mockproject.entity.ApiRequestLog;
import com.ojt.mockproject.entity.Enum.CourseStatusEnum;
import com.ojt.mockproject.repository.ApiRequestLogRepository;
import com.ojt.mockproject.service.AdminService;
import com.ojt.mockproject.dto.Account.Responses.ViewAccountResponseDTO;
import com.ojt.mockproject.entity.Enum.AccountRoleEnum;
import com.ojt.mockproject.entity.Enum.AccountStatusEnum;
import com.ojt.mockproject.service.RedisService;
import com.ojt.mockproject.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@CrossOrigin("*")
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private AdminService adminService;
    @Autowired
    private ApiRequestLogRepository apiRequestLogRepository;
    @Autowired
    private RedisService redisService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all-courses")
    public List<CourseResponseDTO> getAllCourses() {
        return adminService.getAllCoursesByAdmin();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Integer courseId) {
            CourseResponseDTO course = adminService.getCourseById(courseId);
            return ResponseEntity.ok(course);
    }

    //---view course by account id
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/course/by-account/{accountId}")
    public List<CourseResponseDTO> getCourseByAccountId(@PathVariable Integer accountId) {
        return adminService.getCourseByAccountId(accountId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/course/by-status/{status}")
    public List<CourseResponseDTO> getCourseByStatus(@PathVariable CourseStatusEnum status) {
        return adminService.getCourseByStatus(status);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/account/approve/course/{courseId}")
    public ResponseEntity<String> approveCourse(@PathVariable Integer courseId) {
        adminService.approveCourse(courseId);
        return ResponseEntity.ok("Course with ID " + courseId + " has been approved.");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/account/reject/course/{courseId}")
    public ResponseEntity<String> rejectCourse(@PathVariable Integer courseId) {
        adminService.rejectCourse(courseId);
        return ResponseEntity.ok("Course with ID " + courseId + " has been rejected.");
    }

    //----- <UC 29: View list of students/instructors> ------
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("account/role/{role}")
    public ResponseEntity<List<ViewAccountResponseDTO>> getAccountsByRole(@PathVariable AccountRoleEnum role) {
        List<ViewAccountResponseDTO> accounts = adminService.getAccountsByRole(role);
        return ResponseEntity.ok(accounts);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("account/status/{status}")
    public ResponseEntity<List<ViewAccountResponseDTO>> getAccountsByStatus(@PathVariable AccountStatusEnum status) {
        List<ViewAccountResponseDTO> accounts = adminService.getAccountsByStatus(status);
        return ResponseEntity.ok(accounts);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("account/role/{role}/status/{status}")
    public ResponseEntity<List<ViewAccountResponseDTO>> getAccountsByRoleAndStatus(@PathVariable AccountRoleEnum role, @PathVariable AccountStatusEnum status) {
        List<ViewAccountResponseDTO> accounts = adminService.getAccountsByRoleAndStatus(role, status);
        return ResponseEntity.ok(accounts);
    }
    //------- </UC29> ------

    //------- <UC 30: Approve or Reject Instructor registration> ------
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("account/approve/instructor/{accountId}")
    public ResponseEntity<String> approveInstructorRegistration(@PathVariable Integer accountId) {
        adminService.approveInstructorRegistration(accountId);
        return ResponseEntity.ok("Account with ID " + accountId + " has been approved.");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("account/reject/instructor/{accountId}")
    public ResponseEntity<String> rejectInstructorRegistration(@PathVariable Integer accountId) {
        adminService.rejectInstructorRegistration(accountId);
        return ResponseEntity.ok("Account with ID " + accountId + " has been rejected.");
    }
    //------- </UC30> --------------

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("block/course/{courseId}")
    public ResponseEntity<String> blockCourse(@PathVariable Integer courseId) {
        adminService.blockCourse(courseId);
        return ResponseEntity.ok("Course with ID " + courseId + " has been blocked.");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("unblock/course/{courseId}")
    public ResponseEntity<String> unblockCourse(@PathVariable Integer courseId) {
        adminService.unblockCourse(courseId);
        return ResponseEntity.ok("Course with ID " + courseId + " has been unblocked.");
    }

    //------UC35: block/unblock student, instructor
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("account/block-instructor/{accountId}")
    public ResponseEntity<String> blockInstructorRegistration(@PathVariable Integer accountId) {
        adminService.blockInstructor(accountId);
        return ResponseEntity.ok("Instructor with ID " + accountId + " has been blocked.");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("account/unblock-instructor/{accountId}")
    public ResponseEntity<String> unblockInstructorRegistration(@PathVariable Integer accountId) {
        adminService.unblockInstructor(accountId);
        return ResponseEntity.ok("Instructor with ID " + accountId + " has been unblocked.");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("account/block-student/{accountId}")
    public ResponseEntity<String> blockStudentRegistration(@PathVariable Integer accountId) {
        adminService.blockStudent(accountId);
        return ResponseEntity.ok("Student with ID " + accountId + " has been blocked.");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("account/unblock-student/{accountId}")
    public ResponseEntity<String> unblockStudentRegistration(@PathVariable Integer accountId) {
        adminService.unblockStudent(accountId);
        return ResponseEntity.ok("Student with ID " + accountId + " has been unblocked.");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/report/by-course/{courseId}")
    public List<ReportResponseDTO> getReportByCourseId(@PathVariable Integer courseId) {
        return reportService.getReportByCourseId(courseId);
    }

    //change role student to instructor
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("account/change-instructor/{accountId}")
     public ResponseEntity<String> approveInstructor(@PathVariable Integer accountId) {
        adminService.approveInstructor(accountId);
        return ResponseEntity.ok("Converted to instructor");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/api-request-logs/views-all")
    public ResponseEntity<List<ApiRequestLogResponse>> getAllApiRequestLogs() {
        List<ApiRequestLogResponse> apiRequestLogs = adminService.getAllApiRequestLogs();
        return ResponseEntity.ok(apiRequestLogs);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/api-request-logs/{ip}")
    public ResponseEntity<List<ApiRequestLog>> getApiRequestLogById(@PathVariable String ip) {
        List<ApiRequestLog> apiRequestLogOptional = adminService.getApiRequestLogByIpAddress(ip);
        return ResponseEntity.ok(apiRequestLogOptional);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/unban-ip-address")
    public ResponseEntity<String> deleteKey(@RequestBody String key) {
        redisService.deleteKey(key);
        return ResponseEntity.ok("Key deleted successfully");
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/banned-ip-address/all")
    public ResponseEntity<List<String>> getAllBlacklistedIps() {
        List<String> blacklistedIps = redisService.getAllBlacklistedIps();
        return ResponseEntity.ok(blacklistedIps);
    }



}
