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
import com.ojt.mockproject.exceptionhandler.account.AccountException;
import com.ojt.mockproject.exceptionhandler.course.CourseException;
import com.ojt.mockproject.exceptionhandler.ErrorCode;
import com.ojt.mockproject.repository.AccountRepository;
import com.ojt.mockproject.repository.ApiRequestLogRepository;
import com.ojt.mockproject.repository.CourseRepository;
import com.ojt.mockproject.utils.ValidationUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ApiRequestLogRepository apiRequestLogRepository;

    // ---- <UC 29: View list of students/instructors> ---
    //Tuan Dat
    public List<ViewAccountResponseDTO> getAccountsByRole(AccountRoleEnum role) {
        try {
            List<Account> list = accountRepository.findByRoleAndIsDeleted(role,false);
            return getAccountResponseDTOS(list);
        } catch (Exception e) {
            throw new AccountException("Failed to retrieve accounts by role", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public List<ViewAccountResponseDTO> getAccountsByStatus(AccountStatusEnum status) {
        try {
            List<Account> list = accountRepository.findByStatusAndIsDeleted(status,false);
            return getAccountResponseDTOS(list);
        } catch (Exception e) {
            throw new AccountException("Failed to retrieve accounts by role", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    //Tuan Dat
    public List<ViewAccountResponseDTO> getAccountsByRoleAndStatus(AccountRoleEnum role, AccountStatusEnum status) {
        try {
            List<Account> list = accountRepository.findByRoleAndStatusAndIsDeleted(role, status,false);
            return getAccountResponseDTOS(list);
        } catch (Exception e) {
            throw new AccountException("Failed to retrieve accounts by role and status", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }/////

    //Tuan Dat
    public static List<ViewAccountResponseDTO> getAccountResponseDTOS(List<Account> list) {
        try {
            List<ViewAccountResponseDTO> returnList = new ArrayList<>();
            for (Account account : list) {
                returnList.add(
                        new ViewAccountResponseDTO(account.getId(), account.getName(), account.getEmail(), account.getPhone(), account.getGender(), account.getAvatar(), account.getRole(), account.getStatus())
                );
            }
            return returnList;
        } catch (Exception e) {
            throw new AccountException("Can't redirect AccountResponseDTO");
        }
    }
    //---- </UC29> ----

    //------- <UC 30: Approve or Reject Instructor registration> ------
    //Tuan Dat
    @Transactional
    public void approveInstructorRegistration(Integer accountId) {
        try {
            Account account1 = accountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountException("Account not found with id: " + accountId, ErrorCode.USER_NOT_FOUND));

            Account account = accountRepository.findByIdAndRole(accountId, AccountRoleEnum.INSTRUCTOR);
            if (account == null) {
                throw new AccountException("Account not found or not an Instructor: " + accountId, ErrorCode.COURSE_NOT_FOUND);
            }
            ValidationUtils.validateAccountIsDeleted(account);
            account.setStatus(AccountStatusEnum.APPROVED);
            accountRepository.save(account);
        } catch (AccountException e) {
            throw e;
        } catch (Exception e) {
            throw new AccountException("Failed to approve instructor registration", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    //Tuan Dat
    @Transactional
    public void rejectInstructorRegistration(Integer accountId) {
        try {
            accountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountException("Account not found with id: " + accountId, ErrorCode.USER_NOT_FOUND));

            Account account = accountRepository.findByIdAndRole(accountId, AccountRoleEnum.INSTRUCTOR);
            if (account == null) {
                throw new AccountException("Account not found or not an Instructor: " + accountId, ErrorCode.COURSE_NOT_FOUND);
            }
            ValidationUtils.validateAccountIsDeleted(account);
            account.setStatus(AccountStatusEnum.REJECTED);
            accountRepository.save(account);
        } catch (AccountException e) {
            throw e;
        } catch (Exception e) {
            throw new AccountException("Failed to reject instructor registration", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    //--- view course by Account Id
    public List<CourseResponseDTO> getCourseByAccountId(Integer accountId) {
        try {
            List<Course> courses = courseRepository.findByAccountIdAndIsDeleted(accountId,false);
            if (courses.isEmpty()) {
                throw new CourseException("CourseId not found", ErrorCode.COURSE_NOT_FOUND);
            }
            return courses.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CourseException("Failed to retrieve course with account id " + accountId, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    //----- view status course: PENDING, APPROVED, REJECTED, BLOCKED, UNBLOCKED
    public List<CourseResponseDTO> getCourseByStatus(CourseStatusEnum status) {
        try {
            List<Course> courses = courseRepository.findByStatusAndIsDeleted(status,false);
            return courses.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CourseException("Failed to retrieve course with status " + status, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private CourseResponseDTO convertToDTO(Course course) {
        CourseResponseDTO dto = new CourseResponseDTO();
        dto.setId(course.getId());
        dto.setName(course.getName());
        dto.setCategory(course.getCategory());
        dto.setCreateAt(course.getCreateAt());
        dto.setUpdateBy(course.getUpdateAt());
        dto.setVersion(course.getVersion());
        dto.setStatus(course.getStatus());
        dto.setIsDeleted(course.getIsDeleted());
        if (course.getAccount() != null) {
            dto.setAccountId(course.getAccount().getId());
        } else {
            dto.setAccountId(null); // Hoặc giá trị mặc định
        }
        return dto;
    }

    // nay chi co admin moi coi duoc nen khong can bat
    public List<CourseResponseDTO> getAllCoursesByAdmin() {
        try {
            List<Course> courses = courseRepository.findAll();
            return courses.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CourseException("Failed to retrieve course", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public CourseResponseDTO getCourseById(Integer id) {
        try {
            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new CourseException("Course not found with id: " + id, ErrorCode.COURSE_NOT_FOUND));
            ValidationUtils.validateCourseIsDeleted(course);
            return convertToDTO(course);
        } catch (CourseException e) {
            throw e;
        } catch (Exception e) {
            throw new CourseException("Failed to retrieve course with id " + id, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    public void approveCourse(Integer id) {
        try {
            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new CourseException("Course not found with id: " + id, ErrorCode.COURSE_NOT_FOUND));
            ValidationUtils.validateCourseIsDeleted(course);
            course.setStatus(CourseStatusEnum.APPROVED);
            courseRepository.save(course);
        } catch (CourseException e) {
            throw e;
        } catch (Exception e) {
            throw new CourseException("Failed to approve course", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void rejectCourse(Integer id) {
        try {
            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new CourseException("Course not found with Id: " + id, ErrorCode.COURSE_NOT_FOUND));
            ValidationUtils.validateCourseIsDeleted(course);
            course.setStatus(CourseStatusEnum.REJECTED);
            courseRepository.save(course);
        } catch (CourseException e) {
            throw e;
        } catch (Exception e) {
            throw new CourseException("Failed to reject course", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void blockCourse(Integer id) {
        try {
            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new CourseException("Course not found with id: " + id, ErrorCode.COURSE_NOT_FOUND));
            ValidationUtils.validateCourseIsDeleted(course);
            course.setStatus(CourseStatusEnum.BLOCKED);
            courseRepository.save(course);
        } catch (CourseException e) {
            throw e;
        } catch (Exception e) {
            throw new CourseException("Failed to block course", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void unblockCourse(Integer id) {
        try {
            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new CourseException("Course not found with id: " + id, ErrorCode.COURSE_NOT_FOUND));
            ValidationUtils.validateCourseIsDeleted(course);
            course.setStatus(CourseStatusEnum.UNBLOCKED);
            courseRepository.save(course);
        } catch (CourseException e) {
            throw e;
        } catch (Exception e) {
            throw new CourseException("Failed to unblock course", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    //-------UC35: block/unblock student, instructor.
    @Transactional
    public void blockInstructor(Integer accountId) {
        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountException("Account not found with Id: " + accountId, ErrorCode.ACCOUNT_NOT_FOUND));

            if (account.getRole() != AccountRoleEnum.INSTRUCTOR) {
                throw new AccountException("Account is not an Instructor: " + accountId, ErrorCode.ACCOUNT_NOT_INSTRUCTOR);
            }
            ValidationUtils.validateAccountIsDeleted(account);
            account.setStatus(AccountStatusEnum.BLOCKED);
            accountRepository.save(account);
        } catch (AccountException e){
            throw e;
        } catch (Exception e) {
            throw new AccountException("Failed to block instructor registration", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void unblockInstructor(Integer accountId) {
        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountException("Account not found with Id: " + accountId, ErrorCode.ACCOUNT_NOT_FOUND));

            if (account.getRole() != AccountRoleEnum.INSTRUCTOR) {
                throw new AccountException("Account is not an Instructor: " + account.getId(), ErrorCode.ACCOUNT_NOT_INSTRUCTOR);
            }
            ValidationUtils.validateAccountIsDeleted(account);
            account.setStatus(AccountStatusEnum.UNBLOCKED);
            accountRepository.save(account);
        } catch (AccountException ae) {
            throw ae;
        } catch (Exception e) {
            throw new AccountException("Failed to unblock instructor registration", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void blockStudent(Integer accountId) {
        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountException("Account not found with Id: " + accountId, ErrorCode.ACCOUNT_NOT_FOUND));

            if (account.getRole() != AccountRoleEnum.STUDENT) {
                throw new AccountException("Account is not a Student: " + accountId, ErrorCode.ACCOUNT_NOT_STUDENT);
            }
            ValidationUtils.validateAccountIsDeleted(account);
            account.setStatus(AccountStatusEnum.BLOCKED);
            accountRepository.save(account);
        } catch (AccountException ae) {
            throw ae;
        } catch (Exception e) {
            throw new AccountException("Failed to block student registration", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void unblockStudent(Integer accountId) {
        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountException("Account not found with Id: " + accountId, ErrorCode.ACCOUNT_NOT_FOUND));

            if (account.getRole() != AccountRoleEnum.STUDENT) {
                throw new AccountException("Account is not a Student: " + account.getId(), ErrorCode.ACCOUNT_NOT_STUDENT);
            }
            ValidationUtils.validateAccountIsDeleted(account);
            account.setStatus(AccountStatusEnum.UNBLOCKED);
            accountRepository.save(account);
        } catch (AccountException ae) {
            throw ae;
        } catch (Exception e) {
            throw new AccountException("Failed to unblock student registration", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void approveInstructor(Integer accountId) {
        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountException("Account not found with id: " + accountId, ErrorCode.ACCOUNT_NOT_FOUND));
            if (account.getRole() != AccountRoleEnum.STUDENT) {
                throw new AccountException("Account is not a Student: " + account.getId(), ErrorCode.ACCOUNT_NOT_STUDENT);
            }
            ValidationUtils.validateAccountIsDeleted(account);
            account.setRole(AccountRoleEnum.INSTRUCTOR);
            accountRepository.save(account);
        } catch (AccountException e) {
            throw e;
        } catch (Exception e) {
            throw new CourseException("Failed to approve account to instructor", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public List<ApiRequestLogResponse> getAllApiRequestLogs() {
        List<ApiRequestLog> apiRequestLogs = apiRequestLogRepository.findAll();

        List<ApiRequestLogResponse> apiRequestLogResponse = apiRequestLogs.stream().map(log -> {
            ApiRequestLogResponse response = new ApiRequestLogResponse();
            response.setId(log.getId());
            response.setGuest(log.isGuest());
            response.setIpAddress(log.getIpAddress());
            response.setApiEndpoint(log.getApiEndpoint());
            response.setRequestCount(log.getRequestCount());
            response.setCreateAt(log.getCreateAt());
            response.setUpdateAt(log.getUpdateAt());
            if (log.getAccount() != null) {
                response.setAccountId(log.getAccount().getId());
            } else {
                response.setAccountId(null);
            }

            return response;
        }).collect(Collectors.toList());
        return apiRequestLogResponse;
    }

    public List<ApiRequestLog> getApiRequestLogByIpAddress(String ipAddress) {
        return apiRequestLogRepository.findByIpAddress(ipAddress);
    }




}

