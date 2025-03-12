package com.ojt.mockproject.service;

import com.ojt.mockproject.dto.Account.Responses.ViewAccountResponseDTO;
import com.ojt.mockproject.dto.ApiRequestLog.Responses.ApiRequestLogResponse;
import com.ojt.mockproject.dto.Course.CourseResponseDTO;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.ApiRequestLog;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.Enum.AccountRoleEnum;
import com.ojt.mockproject.entity.Enum.AccountStatusEnum;
import com.ojt.mockproject.entity.Enum.CourseStatusEnum;
import com.ojt.mockproject.exceptionhandler.ErrorCode;
import com.ojt.mockproject.exceptionhandler.account.AccountException;
import com.ojt.mockproject.exceptionhandler.course.CourseException;
import com.ojt.mockproject.repository.AccountRepository;
import com.ojt.mockproject.repository.ApiRequestLogRepository;
import com.ojt.mockproject.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private ApiRequestLogRepository apiRequestLogRepository;

    @InjectMocks
    private AdminService adminService;
    private Account testAccount1;
    private Account testAccount2;
    private Course testCourse1;
    private Course testCourse2;

    private ApiRequestLog log1;
    private ApiRequestLog log2;

    @BeforeEach
    void setUp() {
        // Setup test data
        // Setup test data
        testAccount1 = Account.builder()
                .name("Test Account 1")
                .email("test1@example.com")
                .role(AccountRoleEnum.INSTRUCTOR)
                .status(AccountStatusEnum.VERIFIED)
                .isInstructorVerify(false)
                .isDeleted(false)
                .createAt(LocalDateTime.now())
                .build();
        testAccount1.setId(1);

        testAccount2 = Account.builder()
                .name("Test Account 2")
                .email("test2@example.com")
                .role(AccountRoleEnum.STUDENT)
                .status(AccountStatusEnum.UNVERIFIED)
                .isInstructorVerify(false)
                .isDeleted(false)
                .createAt(LocalDateTime.now())
                .build();
        testAccount2.setId(2);

        log1 = new ApiRequestLog();
        log1.setId(1);
        log1.setGuest(true);
        log1.setIpAddress("192.168.1.1");
        log1.setApiEndpoint("/api/test1");
        log1.setRequestCount(10);
        log1.setCreateAt(LocalDateTime.now());
        log1.setUpdateAt(LocalDateTime.now());
        log1.setAccount(testAccount1);

        log2 = new ApiRequestLog();
        log2.setId(2);
        log2.setGuest(false);
        log2.setIpAddress("192.168.1.2");
        log2.setApiEndpoint("/api/test2");
        log2.setRequestCount(20);
        log2.setCreateAt(LocalDateTime.now());
        log2.setUpdateAt(LocalDateTime.now());
        log2.setAccount(testAccount2);
    }


    @BeforeEach
    void setUpCourse() {
        // Setup test data
        testCourse1 = new Course();
        testCourse1.setId(1);
        testCourse1.setName("Test Course 1");
        testCourse1.setStatus(CourseStatusEnum.PENDING);

        testCourse2 = new Course();
        testCourse2.setId(2);
        testCourse2.setName("Test Course 2");
        testCourse2.setStatus(CourseStatusEnum.APPROVED);
    }

    @Test
    public void testGetAccountsByRole() {
        List<Account> mockAccounts = new ArrayList<>();
        mockAccounts.add(testAccount1);
        mockAccounts.add(testAccount2);
        when(accountRepository.findByRoleAndIsDeleted(AccountRoleEnum.INSTRUCTOR,false)).thenReturn(mockAccounts);
        List<ViewAccountResponseDTO> result = adminService.getAccountsByRole(AccountRoleEnum.INSTRUCTOR);

        verify(accountRepository, times(1)).findByRoleAndIsDeleted(AccountRoleEnum.INSTRUCTOR,false);

        assertEquals(mockAccounts.size(), result.size());
    }
    @Test
    public void testGetAccountsByRole_Exception() {
        // Given
        when(accountRepository.findByRoleAndIsDeleted(any(AccountRoleEnum.class), eq(false)))
                .thenThrow(new RuntimeException("Database error"));

        // When
        AccountException exception = assertThrows(AccountException.class, () -> {
            adminService.getAccountsByRole(AccountRoleEnum.INSTRUCTOR);
        });

        // Then
        assertEquals("Failed to retrieve accounts by role", exception.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
        verify(accountRepository, times(1)).findByRoleAndIsDeleted(any(AccountRoleEnum.class), eq(false));
    }

    @Test
    public void testGetAccountsByStatus() {
        List<Account> mockAccounts = new ArrayList<>();
        mockAccounts.add(testAccount1);
        when(accountRepository.findByStatusAndIsDeleted(AccountStatusEnum.VERIFIED,false)).thenReturn(mockAccounts);
        List<ViewAccountResponseDTO> result = adminService.getAccountsByStatus(AccountStatusEnum.VERIFIED);

        verify(accountRepository, times(1)).findByStatusAndIsDeleted(AccountStatusEnum.VERIFIED,false);

        assertEquals(mockAccounts.size(), result.size());
    }
    @Test
    public void testGetAccountsByStatus_Exception() {
        when(accountRepository.findByStatusAndIsDeleted(any(AccountStatusEnum.class), eq(false)))
                .thenThrow(new RuntimeException("Database error"));

        AccountException exception = assertThrows(AccountException.class, () -> {
            adminService.getAccountsByStatus(AccountStatusEnum.VERIFIED);
        });

        assertEquals("Failed to retrieve accounts by role", exception.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
        verify(accountRepository, times(1)).findByStatusAndIsDeleted(AccountStatusEnum.VERIFIED, false);
    }

    @Test
    public void testGetAccountResponseDTOS_Success() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(testAccount1);
        accounts.add(testAccount2);

        List<ViewAccountResponseDTO> result = AdminService.getAccountResponseDTOS(accounts);

        assertEquals(2, result.size());
        assertEquals("Test Account 1", result.get(0).getName());
        assertEquals("Test Account 2", result.get(1).getName());
    }
    @Test
    public void testGetAccountResponseDTOS_Exception() {
        List<Account> mockAccounts = new ArrayList<>();
        Account mockAccount = mock(Account.class);
        when(mockAccount.getId()).thenThrow(new RuntimeException("Mock exception"));
        mockAccounts.add(mockAccount);

        AccountException exception = assertThrows(AccountException.class, () -> {
            AdminService.getAccountResponseDTOS(mockAccounts);
        });

        assertEquals("Can't redirect AccountResponseDTO", exception.getMessage());
    }
    @Test
    public void testGetAccountsByRoleAndStatus() {
        List<Account> mockAccounts = new ArrayList<>();
        mockAccounts.add(testAccount1);
        when(accountRepository.findByRoleAndStatusAndIsDeleted(AccountRoleEnum.INSTRUCTOR, AccountStatusEnum.VERIFIED, false)).thenReturn(mockAccounts);

        List<ViewAccountResponseDTO> result = adminService.getAccountsByRoleAndStatus(AccountRoleEnum.INSTRUCTOR, AccountStatusEnum.VERIFIED);
        verify(accountRepository, times(1)).findByRoleAndStatusAndIsDeleted(AccountRoleEnum.INSTRUCTOR, AccountStatusEnum.VERIFIED, false);

        assertEquals(mockAccounts.size(), result.size());
    }

    @Test
    public void testGetAccountsByRoleAndStatus_Exception() {
        when(accountRepository.findByRoleAndStatusAndIsDeleted(any(AccountRoleEnum.class), any(AccountStatusEnum.class), eq(false)))
                .thenThrow(new RuntimeException("Database error"));

        AccountException exception = assertThrows(AccountException.class, () -> {
            adminService.getAccountsByRoleAndStatus(AccountRoleEnum.INSTRUCTOR, AccountStatusEnum.VERIFIED);
        });

        assertEquals("Failed to retrieve accounts by role and status", exception.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
        verify(accountRepository, times(1)).findByRoleAndStatusAndIsDeleted(AccountRoleEnum.INSTRUCTOR, AccountStatusEnum.VERIFIED, false);
    }


    @Test
    public void testGetCourseByAccountId_Success() {
        // Given
        Integer accountId = 1;
        List<Course> mockCourses = new ArrayList<>();
        mockCourses.add(testCourse1);
        mockCourses.add(testCourse2);
        when(courseRepository.findByAccountIdAndIsDeleted(accountId, false)).thenReturn(mockCourses);

        // When
        List<CourseResponseDTO> result = adminService.getCourseByAccountId(accountId);

        // Then
        verify(courseRepository, times(1)).findByAccountIdAndIsDeleted(accountId, false);
        assertEquals(mockCourses.size(), result.size());
        assertEquals(testCourse1.getName(), result.get(0).getName());
        assertEquals(testCourse2.getName(), result.get(1).getName());
    }

    @Test
    public void testGetCourseByAccountId_AccountNotFound() {
        // Given
        Integer accountId = 1;
        when(courseRepository.findByAccountIdAndIsDeleted(accountId, false)).thenReturn(new ArrayList<>());

        // When
        CourseException exception = assertThrows(CourseException.class, () -> adminService.getCourseByAccountId(accountId));

        // Then
        assertEquals("Failed to retrieve course with account id 1", exception.getMessage());
        verify(courseRepository, times(1)).findByAccountIdAndIsDeleted(accountId, false);
    }



    @Test
    public void testGetCourseByAccountId_Exception() {
        // Given
        Integer accountId = 1;
        when(courseRepository.findByAccountIdAndIsDeleted(accountId, false)).thenThrow(new RuntimeException("Database error"));

        // When
        CourseException exception = assertThrows(CourseException.class, () -> adminService.getCourseByAccountId(accountId));

        // Then
        assertEquals("Failed to retrieve course with account id " + accountId, exception.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
        verify(courseRepository, times(1)).findByAccountIdAndIsDeleted(accountId, false);
    }

    @Test
    public void testGetCourseByStatus_Success() {
// Given
        List<Course> mockCourses = new ArrayList<>();
        mockCourses.add(testCourse1);
        when(courseRepository.findByStatusAndIsDeleted(CourseStatusEnum.PENDING, false)).thenReturn(mockCourses);

        // When
        List<CourseResponseDTO> result = adminService.getCourseByStatus(CourseStatusEnum.PENDING);

        // Then
        verify(courseRepository, times(1)).findByStatusAndIsDeleted(CourseStatusEnum.PENDING, false);
        assertEquals(mockCourses.size(), result.size());
        assertEquals(testCourse1.getName(), result.get(0).getName());
    }


    @Test
    public void testGetCourseByStatus_Exception() {
        // Given
        when(courseRepository.findByStatusAndIsDeleted(CourseStatusEnum.BLOCKED, false)).thenThrow(new RuntimeException("Database error"));

        // When
        CourseException exception = assertThrows(CourseException.class, () -> adminService.getCourseByStatus(CourseStatusEnum.BLOCKED));

        // Then
        assertEquals("Failed to retrieve course with status BLOCKED", exception.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
        verify(courseRepository, times(1)).findByStatusAndIsDeleted(CourseStatusEnum.BLOCKED, false);
    }

    @Test
    public void testGetAllCoursesByAdmin_Success() {
        List<Course> mockCourses = new ArrayList<>();
        mockCourses.add(testCourse1);
        mockCourses.add(testCourse2);

        when(courseRepository.findAll()).thenReturn(mockCourses);

        List<CourseResponseDTO> result = adminService.getAllCoursesByAdmin();

        verify(courseRepository, times(1)).findAll();
        assertEquals(mockCourses.size(), result.size());
    }

    @Test
    public void testGetAllCoursesByAdmin_Exception() {
        // Given
        when(courseRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When
        CourseException exception = assertThrows(CourseException.class, () -> adminService.getAllCoursesByAdmin());

        // Then
        assertEquals("Failed to retrieve course", exception.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    public void testGetCourseById_Success() {
        // Given
        Course testCourse1 = new Course();
        testCourse1.setId(1);
        testCourse1.setName("Test Course");
        testCourse1.setIsDeleted(false); // Đảm bảo course không bị xóa

        when(courseRepository.findById(1)).thenReturn(Optional.of(testCourse1));

        // When
        CourseResponseDTO result = adminService.getCourseById(1);

        // Then
        verify(courseRepository, times(1)).findById(1);
        assertEquals(testCourse1.getName(), result.getName());
    }
    @Test
    public void testGetCourseById_Failure_CourseIsDeleted() {
        // Given
        Course testCourse1 = new Course();
        testCourse1.setId(1);
        testCourse1.setName("Test Course");
        testCourse1.setIsDeleted(true); // Course bị xóa

        when(courseRepository.findById(1)).thenReturn(Optional.of(testCourse1));

        // When & Then
        CourseException thrown = assertThrows(CourseException.class, () -> {
            adminService.getCourseById(1);
        });
        assertEquals("This course has been deleted", thrown.getMessage());
    }


    @Test
    public void testGetCourseById_CourseNotFound() {
        when(courseRepository.findById(1)).thenReturn(Optional.empty());

        CourseException exception = assertThrows(CourseException.class, () -> adminService.getCourseById(1));

        assertEquals("Course not found with id: 1", exception.getMessage());
    }

    @Test
    public void testGetCourseById_Exception() {
        // Given
        when(courseRepository.findById(1)).thenThrow(new RuntimeException("Database error"));

        // When
        CourseException exception = assertThrows(CourseException.class, () -> adminService.getCourseById(1));

        // Then
        assertEquals("Failed to retrieve course with id 1", exception.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
        verify(courseRepository, times(1)).findById(1);
    }

    @Test
    public void testApproveCourse_Success() {
        // Given
        testCourse1.setIsDeleted(false);  // Đảm bảo rằng khóa học không bị xóa
        when(courseRepository.findById(1)).thenReturn(Optional.of(testCourse1));

        adminService.approveCourse(1);

        // Then
        verify(courseRepository, times(1)).findById(1);
        verify(courseRepository, times(1)).save(testCourse1);
        assertEquals(CourseStatusEnum.APPROVED, testCourse1.getStatus());
    }

    @Test
    public void testApproveCourse_CourseNotFound() {
        when(courseRepository.findById(1)).thenReturn(Optional.empty());

        CourseException exception = assertThrows(CourseException.class, () -> adminService.approveCourse(1));

        assertEquals("Course not found with id: 1", exception.getMessage());
    }

    @Test
    public void testApproveCourse_Exception() {
        // Given
        testCourse1.setIsDeleted(false);  // Đảm bảo rằng khóa học không bị xóa
        when(courseRepository.findById(testCourse1.getId())).thenReturn(Optional.of(testCourse1));
        doThrow(new RuntimeException("Database error")).when(courseRepository).save(any(Course.class));

        // When
        CourseException exception = assertThrows(CourseException.class, () -> adminService.approveCourse(testCourse1.getId()));

        // Then
        assertEquals("Failed to approve course", exception.getMessage());
        verify(courseRepository).findById(testCourse1.getId());
        verify(courseRepository).save(any(Course.class));
    }


    @Test
    public void testRejectCourse_Success() {
        // Given
        testCourse1.setIsDeleted(false);  // Đảm bảo rằng khóa học không bị xóa
        when(courseRepository.findById(1)).thenReturn(Optional.of(testCourse1));

        adminService.rejectCourse(1);

        // Then
        verify(courseRepository, times(1)).findById(1);
        verify(courseRepository, times(1)).save(testCourse1);
        assertEquals(CourseStatusEnum.REJECTED, testCourse1.getStatus());
    }
    @Test
    public void testRejectCourse_CourseNotFound() {
        when(courseRepository.findById(1)).thenReturn(Optional.empty());

        CourseException exception = assertThrows(CourseException.class, () -> adminService.rejectCourse(1));

        assertEquals("Course not found with Id: 1", exception.getMessage());
    }

    @Test
    public void testRejectCourse_Exception() {
        // Given
        testCourse1.setIsDeleted(false);  // Đảm bảo rằng khóa học không bị xóa
        when(courseRepository.findById(testCourse1.getId())).thenReturn(Optional.of(testCourse1));
        doThrow(new RuntimeException("Database error")).when(courseRepository).save(any(Course.class));

        // When
        CourseException exception = assertThrows(CourseException.class, () -> adminService.rejectCourse(testCourse1.getId()));

        // Then
        assertEquals("Failed to reject course", exception.getMessage());
        verify(courseRepository).findById(testCourse1.getId());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    public void testBlockCourse() {
        // Given
        testCourse1.setIsDeleted(false);  // Đảm bảo rằng khóa học không bị xóa
        when(courseRepository.findById(1)).thenReturn(Optional.of(testCourse1));

        // When
        adminService.blockCourse(1);

        // Then
        verify(courseRepository, times(1)).findById(1);
        verify(courseRepository, times(1)).save(testCourse1);
        assertEquals(CourseStatusEnum.BLOCKED, testCourse1.getStatus());
    }

    @Test
    public void testBlockCourse_CourseNotFound() {
        when(courseRepository.findById(1)).thenReturn(Optional.empty());

        CourseException exception = assertThrows(CourseException.class, () -> adminService.blockCourse(1));

        assertEquals("Course not found with id: 1", exception.getMessage());
    }

    @Test
    public void testBlockCourse_Exception() {
        // Given
        testCourse1.setIsDeleted(false);  // Đảm bảo rằng khóa học không bị xóa
        when(courseRepository.findById(testCourse1.getId())).thenReturn(Optional.of(testCourse1));
        doThrow(new RuntimeException("Database error")).when(courseRepository).save(any(Course.class));

        // When
        CourseException exception = assertThrows(CourseException.class, () -> adminService.blockCourse(testCourse1.getId()));

        // Then
        assertEquals("Failed to block course", exception.getMessage());
        verify(courseRepository).findById(testCourse1.getId());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    public void testUnblockCourse() {
        // Given
        testCourse1.setIsDeleted(false);  // Đảm bảo rằng khóa học không bị xóa
        when(courseRepository.findById(1)).thenReturn(Optional.of(testCourse1));

        // When
        adminService.unblockCourse(1);

        // Then
        verify(courseRepository, times(1)).findById(1);
        verify(courseRepository, times(1)).save(testCourse1);
        assertEquals(CourseStatusEnum.UNBLOCKED, testCourse1.getStatus());
    }

    @Test
    public void testUnblockCourseNotFound() {
        when(courseRepository.findById(1)).thenReturn(Optional.empty());

        CourseException exception = assertThrows(CourseException.class, () -> adminService.unblockCourse(1));

        assertEquals("Course not found with id: 1", exception.getMessage());
    }

    @Test
    public void testBlockInstructor_Success() {
        when(accountRepository.findById(1)).thenReturn(Optional.of(testAccount1));

        adminService.blockInstructor(1);

        verify(accountRepository, times(1)).findById(1);
        verify(accountRepository, times(1)).save(testAccount1);
        assertEquals(AccountStatusEnum.BLOCKED, testAccount1.getStatus());
    }

    @Test
    public void testUnblockCourse_Exception() {
        // Given
        testCourse1.setIsDeleted(false);  // Đảm bảo rằng khóa học không bị xóa
        when(courseRepository.findById(testCourse1.getId())).thenReturn(Optional.of(testCourse1));
        doThrow(new RuntimeException("Database error")).when(courseRepository).save(any(Course.class));

        // When
        CourseException exception = assertThrows(CourseException.class, () -> adminService.unblockCourse(testCourse1.getId()));

        // Then
        assertEquals("Failed to unblock course", exception.getMessage());
        verify(courseRepository).findById(testCourse1.getId());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    public void testBlockInstructor_AccountNotFound() {
        when(accountRepository.findById(1)).thenReturn(Optional.empty());

        AccountException exception = assertThrows(AccountException.class, () -> adminService.blockInstructor(1));

        assertEquals("Account not found with Id: 1", exception.getMessage());
    }

    @Test
    public void testBlockInstructor_NotInstructor() {
        // Given
        when(accountRepository.findById(testAccount2.getId())).thenReturn(Optional.of(testAccount2));

        // When
        AccountException exception = assertThrows(AccountException.class, () -> adminService.blockInstructor(testAccount2.getId()));

        // Then
        assertEquals("Account is not an Instructor: " + testAccount2.getId(), exception.getMessage());
        assertEquals(ErrorCode.ACCOUNT_NOT_INSTRUCTOR, exception.getErrorCode());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void testBlockInstructor_InternalServerError() {
        // Given
        when(accountRepository.findById(testAccount1.getId())).thenReturn(Optional.of(testAccount1));
        doThrow(new RuntimeException("Database error")).when(accountRepository).save(any(Account.class));

        // When
        AccountException exception = assertThrows(AccountException.class, () -> adminService.blockInstructor(testAccount1.getId()));

        // Then
        assertEquals("Failed to block instructor registration", exception.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
        verify(accountRepository).findById(testAccount1.getId());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    public void testUnblockInstructor_Success() {
        when(accountRepository.findById(1)).thenReturn(Optional.of(testAccount1));

        adminService.unblockInstructor(1);

        verify(accountRepository, times(1)).findById(1);
        verify(accountRepository, times(1)).save(testAccount1);
        assertEquals(AccountStatusEnum.UNBLOCKED, testAccount1.getStatus());
    }

    @Test
    public void testUnblockInstructor_AccountNotFound() {
        when(accountRepository.findById(1)).thenReturn(Optional.empty());

        AccountException exception = assertThrows(AccountException.class, () -> adminService.unblockInstructor(1));

        assertEquals("Account not found with Id: 1", exception.getMessage());
    }

    @Test
    public void testUnblockInstructor_AccountNotInstructor() {
        // Given
        when(accountRepository.findById(testAccount2.getId())).thenReturn(Optional.of(testAccount2));

        // When
        AccountException exception = assertThrows(AccountException.class, () -> adminService.unblockInstructor(testAccount2.getId()));

        // Then
        assertEquals("Account is not an Instructor: " + testAccount2.getId(), exception.getMessage());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void testUnblockInstructor_Exception() {
        // Given
        when(accountRepository.findById(testAccount1.getId())).thenReturn(Optional.of(testAccount1));
        doThrow(new RuntimeException("Database error")).when(accountRepository).save(any(Account.class));

        // When
        AccountException exception = assertThrows(AccountException.class, () -> adminService.unblockInstructor(testAccount1.getId()));

        // Then
        assertEquals("Failed to unblock instructor registration", exception.getMessage());
        verify(accountRepository).findById(testAccount1.getId());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    public void testBlockStudent_Success() {
        when(accountRepository.findById(2)).thenReturn(Optional.of(testAccount2));

        adminService.blockStudent(2);

        verify(accountRepository, times(1)).findById(2);
        verify(accountRepository, times(1)).save(testAccount2);
        assertEquals(AccountStatusEnum.BLOCKED, testAccount2.getStatus());
    }

    @Test
    public void testBlockStudent_AccountNotFound() {
        when(accountRepository.findById(2)).thenReturn(Optional.empty());

        AccountException exception = assertThrows(AccountException.class, () -> adminService.blockStudent(2));

        assertEquals("Account not found with Id: 2", exception.getMessage());
    }

    @Test
    public void testBlockStudent_NotStudent() {
        // Given
        when(accountRepository.findById(testAccount1.getId())).thenReturn(Optional.of(testAccount1));

        // When
        AccountException exception = assertThrows(AccountException.class, () -> adminService.blockStudent(testAccount1.getId()));

        // Then
        assertEquals("Account is not a Student: " + testAccount1.getId(), exception.getMessage());
        assertEquals(ErrorCode.ACCOUNT_NOT_STUDENT, exception.getErrorCode());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void testBlockStudent_Exception() {
        // Given
        when(accountRepository.findById(testAccount2.getId())).thenReturn(Optional.of(testAccount2));
        doThrow(new RuntimeException("Database error")).when(accountRepository).save(any(Account.class));

        // When
        AccountException exception = assertThrows(AccountException.class, () -> adminService.blockStudent(testAccount2.getId()));

        // Then
        assertEquals("Failed to block student registration", exception.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
        verify(accountRepository).findById(testAccount2.getId());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    public void testUnblockStudent_Success() {
        when(accountRepository.findById(2)).thenReturn(Optional.of(testAccount2));

        adminService.unblockStudent(2);

        verify(accountRepository, times(1)).findById(2);
        verify(accountRepository, times(1)).save(testAccount2);
        assertEquals(AccountStatusEnum.UNBLOCKED, testAccount2.getStatus());
    }

    @Test
    public void testUnblockStudent_AccountNotFound() {
        when(accountRepository.findById(1)).thenReturn(Optional.empty());

        AccountException exception = assertThrows(AccountException.class, () -> adminService.unblockStudent(1));

        assertEquals("Account not found with Id: 1", exception.getMessage());
    }

    @Test
    public void testUnblockStudent_AccountNotStudent() {
        // Given
        when(accountRepository.findById(testAccount1.getId())).thenReturn(Optional.of(testAccount1));

        // When
        AccountException exception = assertThrows(AccountException.class, () -> adminService.unblockStudent(testAccount1.getId()));

        // Then
        assertEquals("Account is not a Student: " + testAccount1.getId(), exception.getMessage());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void testUnblockStudent_Exception() {
        // Given
        when(accountRepository.findById(testAccount2.getId())).thenReturn(Optional.of(testAccount2));
        doThrow(new RuntimeException("Database error")).when(accountRepository).save(any(Account.class));

        // When
        AccountException exception = assertThrows(AccountException.class, () -> adminService.unblockStudent(testAccount2.getId()));

        // Then
        assertEquals("Failed to unblock student registration", exception.getMessage());
        verify(accountRepository).findById(testAccount2.getId());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    public void testApproveInstructor_Success() {
        // Given
        when(accountRepository.findById(testAccount2.getId())).thenReturn(Optional.of(testAccount2));

        // When
        adminService.approveInstructor(testAccount2.getId());

        // Then
        verify(accountRepository, times(1)).findById(testAccount2.getId());
        verify(accountRepository, times(1)).save(testAccount2);
        assertEquals(AccountRoleEnum.INSTRUCTOR, testAccount2.getRole());
    }

    @Test
    public void testApproveInstructor_AccountNotFound() {
        // Given
        when(accountRepository.findById(testAccount1.getId())).thenReturn(Optional.empty());

        // When
        AccountException exception = assertThrows(AccountException.class, () -> adminService.approveInstructor(testAccount1.getId()));

        // Then
        assertEquals("Account not found with id: " + testAccount1.getId(), exception.getMessage());
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void testApproveInstructor_AccountNotStudent() {
        // Given
        when(accountRepository.findById(testAccount1.getId())).thenReturn(Optional.of(testAccount1));

        // When
        AccountException exception = assertThrows(AccountException.class, () -> adminService.approveInstructor(testAccount1.getId()));

        // Then
        assertEquals("Account is not a Student: " + testAccount1.getId(), exception.getMessage());
        assertEquals(ErrorCode.ACCOUNT_NOT_STUDENT, exception.getErrorCode());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void testApproveInstructor_Exception() {
        // Given
        when(accountRepository.findById(testAccount2.getId())).thenReturn(Optional.of(testAccount2));
        doThrow(new RuntimeException("Database error")).when(accountRepository).save(any(Account.class));

        // When
        CourseException exception = assertThrows(CourseException.class, () -> adminService.approveInstructor(testAccount2.getId()));

        // Then
        assertEquals("Failed to approve account to instructor", exception.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
        verify(accountRepository, times(1)).findById(testAccount2.getId());
        verify(accountRepository, times(1)).save(testAccount2);
    }

    @Test
    public void testApproveInstructorRegistration() {
        when(accountRepository.findById(testAccount1.getId())).thenReturn(Optional.of(testAccount1));
        when(accountRepository.findByIdAndRole(testAccount1.getId(), AccountRoleEnum.INSTRUCTOR)).thenReturn(testAccount1);

        adminService.approveInstructorRegistration(testAccount1.getId());

        verify(accountRepository, times(1)).findById(testAccount1.getId());
        verify(accountRepository, times(1)).findByIdAndRole(testAccount1.getId(), AccountRoleEnum.INSTRUCTOR);
        verify(accountRepository, times(1)).save(testAccount1);

        assertEquals(AccountStatusEnum.APPROVED, testAccount1.getStatus());
    }

    @Test
    public void testRejectInstructorRegistration() {
        when(accountRepository.findById(testAccount1.getId())).thenReturn(Optional.of(testAccount1));
        when(accountRepository.findByIdAndRole(testAccount1.getId(), AccountRoleEnum.INSTRUCTOR)).thenReturn(testAccount1);

        adminService.rejectInstructorRegistration(testAccount1.getId());

        verify(accountRepository, times(1)).findById(testAccount1.getId());
        verify(accountRepository, times(1)).findByIdAndRole(testAccount1.getId(), AccountRoleEnum.INSTRUCTOR);
        verify(accountRepository, times(1)).save(testAccount1);

        assertEquals(AccountStatusEnum.REJECTED, testAccount1.getStatus());
    }


    @Test
    public void testApproveInstructorRegistration_AccountNotFound() {
        when(accountRepository.findById(anyInt())).thenReturn(Optional.empty());

        AccountException exception = assertThrows(AccountException.class, () -> {
            adminService.approveInstructorRegistration(1);
        });

        assertEquals("Account not found with id: 1", exception.getMessage());
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(accountRepository, times(1)).findById(1);
    }

    @Test
    public void testApproveInstructorRegistration_NotAnInstructor() {
        when(accountRepository.findById(anyInt())).thenReturn(Optional.of(testAccount2));
        when(accountRepository.findByIdAndRole(anyInt(), any(AccountRoleEnum.class))).thenReturn(null);

        AccountException exception = assertThrows(AccountException.class, () -> {
            adminService.approveInstructorRegistration(2);
        });

        assertEquals("Account not found or not an Instructor: 2", exception.getMessage());
        assertEquals(ErrorCode.COURSE_NOT_FOUND, exception.getErrorCode());
        verify(accountRepository, times(1)).findById(2);
        verify(accountRepository, times(1)).findByIdAndRole(2, AccountRoleEnum.INSTRUCTOR);
    }

    @Test
    public void testApproveInstructorRegistration_Exception() {
        when(accountRepository.findById(anyInt())).thenReturn(Optional.of(testAccount1));
        when(accountRepository.findByIdAndRole(anyInt(), any(AccountRoleEnum.class))).thenThrow(new RuntimeException("Database error"));

        AccountException exception = assertThrows(AccountException.class, () -> {
            adminService.approveInstructorRegistration(1);
        });

        assertEquals("Failed to approve instructor registration", exception.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
        verify(accountRepository, times(1)).findById(1);
        verify(accountRepository, times(1)).findByIdAndRole(1, AccountRoleEnum.INSTRUCTOR);
    }

    @Test
    public void testRejectInstructorRegistration_AccountNotFound() {
        when(accountRepository.findById(anyInt())).thenReturn(Optional.empty());

        AccountException exception = assertThrows(AccountException.class, () -> {
            adminService.rejectInstructorRegistration(1);
        });

        assertEquals("Account not found with id: 1", exception.getMessage());
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(accountRepository, times(1)).findById(1);
    }

    @Test
    public void testRejectInstructorRegistration_NotAnInstructor() {
        when(accountRepository.findById(anyInt())).thenReturn(Optional.of(testAccount2));
        when(accountRepository.findByIdAndRole(anyInt(), any(AccountRoleEnum.class))).thenReturn(null);

        AccountException exception = assertThrows(AccountException.class, () -> {
            adminService.rejectInstructorRegistration(2);
        });

        assertEquals("Account not found or not an Instructor: 2", exception.getMessage());
        assertEquals(ErrorCode.COURSE_NOT_FOUND, exception.getErrorCode());
        verify(accountRepository, times(1)).findById(2);
        verify(accountRepository, times(1)).findByIdAndRole(2, AccountRoleEnum.INSTRUCTOR);
    }

    @Test
    public void testRejectInstructorRegistration_Exception() {
        when(accountRepository.findById(anyInt())).thenReturn(Optional.of(testAccount1));
        when(accountRepository.findByIdAndRole(anyInt(), any(AccountRoleEnum.class))).thenThrow(new RuntimeException("Database error"));

        AccountException exception = assertThrows(AccountException.class, () -> {
            adminService.rejectInstructorRegistration(1);
        });

        assertEquals("Failed to reject instructor registration", exception.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
        verify(accountRepository, times(1)).findById(1);
        verify(accountRepository, times(1)).findByIdAndRole(1, AccountRoleEnum.INSTRUCTOR);
    }

    @Test
    public void testGetAllApiRequestLogs() {
        when(apiRequestLogRepository.findAll()).thenReturn(Arrays.asList(log1, log2));

        List<ApiRequestLogResponse> result = adminService.getAllApiRequestLogs();

        assertEquals(2, result.size());
        assertEquals(log1.getId(), result.get(0).getId());
        assertEquals(log1.isGuest(), result.get(0).isGuest());
        assertEquals(log1.getIpAddress(), result.get(0).getIpAddress());
        assertEquals(log1.getApiEndpoint(), result.get(0).getApiEndpoint());
        assertEquals(log1.getRequestCount(), result.get(0).getRequestCount());
        assertEquals(log1.getCreateAt(), result.get(0).getCreateAt());
        assertEquals(log1.getUpdateAt(), result.get(0).getUpdateAt());
        assertEquals(log2.getId(), result.get(1).getId());
        assertEquals(log2.isGuest(), result.get(1).isGuest());
        assertEquals(log2.getIpAddress(), result.get(1).getIpAddress());
        assertEquals(log2.getApiEndpoint(), result.get(1).getApiEndpoint());
        assertEquals(log2.getRequestCount(), result.get(1).getRequestCount());
        assertEquals(log2.getCreateAt(), result.get(1).getCreateAt());
        assertEquals(log2.getUpdateAt(), result.get(1).getUpdateAt());

    }


    @Test
    public void testGetApiRequestLogByIpAddress() {
        when(apiRequestLogRepository.findByIpAddress("192.168.1.1")).thenReturn(Arrays.asList(log1));

        List<ApiRequestLog> result = adminService.getApiRequestLogByIpAddress("192.168.1.1");

        assertEquals(1, result.size());
        assertEquals(log1.getId(), result.get(0).getId());
        assertEquals(log1.isGuest(), result.get(0).isGuest());
        assertEquals(log1.getIpAddress(), result.get(0).getIpAddress());
        assertEquals(log1.getApiEndpoint(), result.get(0).getApiEndpoint());
        assertEquals(log1.getRequestCount(), result.get(0).getRequestCount());
        assertEquals(log1.getCreateAt(), result.get(0).getCreateAt());
        assertEquals(log1.getUpdateAt(), result.get(0).getUpdateAt());
    }



}
