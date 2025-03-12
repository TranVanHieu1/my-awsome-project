package com.ojt.mockproject.service;

import com.ojt.mockproject.dto.Account.Requests.RemoveWishListRequest;
import com.ojt.mockproject.dto.Account.Requests.UpdateRequestDTO;
import com.ojt.mockproject.dto.Account.Responses.InstructorResponseDTO;
import com.ojt.mockproject.dto.Account.Responses.RemoveWishListResponse;
import com.ojt.mockproject.dto.Account.Responses.UpdateResponseDTO;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.Enum.AccountGenderEnum;
import com.ojt.mockproject.entity.Enum.AccountRoleEnum;
import com.ojt.mockproject.exceptionhandler.ValidationException;
import com.ojt.mockproject.exceptionhandler.account.AccountException;
import com.ojt.mockproject.exceptionhandler.account.NotLoginException;
import com.ojt.mockproject.repository.AccountRepository;
import com.ojt.mockproject.repository.CourseRepository;
import com.ojt.mockproject.utils.AccountUtils;
import com.ojt.mockproject.utils.UploadFileUtils;
import com.ojt.mockproject.utils.ValidationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountUtils accountUtils;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ValidationUtils validationUtils;

    @Mock
    private RemoveWishListRequest removeWishListRequest;
    @Mock
    private RemoveWishListResponse removeWishListResponse;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Mock
    private JWTService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UploadFileUtils uploadFileUtils;

    private UpdateRequestDTO updateRequestDTO;
    private Account account;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        updateRequestDTO = new UpdateRequestDTO();
        updateRequestDTO.setName("John Doe");
        updateRequestDTO.setHeadline("This is headline");
        updateRequestDTO.setAboutMe("This is about me");
        updateRequestDTO.setFacebookLink("This is Facebook Link");
        updateRequestDTO.setLinkedinLink("This is Linkedin Link");
        updateRequestDTO.setTwitterLink("This is Twitter Link");
        updateRequestDTO.setPersonalSiteLink("This is Personal Site Link");
        updateRequestDTO.setYoutubeLink("This is Youtube Link");

        // ** student account * //
        account = new Account();
        account.setId(1);
        account.setName("Old Name");
        account.setPhone("0112345678");
        account.setAvatar("old-avatar-url");
        account.setGender(AccountGenderEnum.FEMALE);
        account.setRole(AccountRoleEnum.STUDENT);
        account.setIsDeleted(false);
        account.setWishlist("1,2,3");

        removeWishListRequest = new RemoveWishListRequest();

    }


//    @Test
//    public void testUpdateAccount_Success() throws Exception {
//        // Setup mock behavior
//        Account mockAccount = new Account();
//        when(accountUtils.getCurrentAccount()).thenReturn(mockAccount);
//
//        UpdateResponseDTO responseDTO = accountService.update(updateRequestDTO);
//
//        // Assertions
//        assertNotNull(responseDTO);
//        assertEquals("John Doe", responseDTO.getName());
//        assertEquals("This is headline", responseDTO.getHeadline());
//        assertEquals("JThis is about me", responseDTO.getAboutMe());
//        assertEquals("This is Facebook Link", responseDTO.getFacebookLink());
//        assertEquals("This is Linkedin Link", responseDTO.getLinkedinLink());
//        assertEquals("This is Twitter Link", responseDTO.getTwitterLink());
//        assertEquals("This is Personal Site Link", responseDTO.getPersonalSiteLink());
//        assertEquals("This is Youtube Link", responseDTO.getYoutubeLink());
//    }


    @Test
    public void testUpdateAccount_NotLoggedIn() throws NotLoginException {
        // Mock behavior of getCurrentAccount()
        when(accountUtils.getCurrentAccount()).thenThrow(new NotLoginException("Not Login"));

        // Call the method under test and expect NotLoginException
        NotLoginException exception = assertThrows(NotLoginException.class, () -> accountService.update(updateRequestDTO));

        // Verify exception message
        assertEquals("Not Login", exception.getMessage());

        // Verify interactions
        verify(accountUtils, times(1)).getCurrentAccount();
        verify(accountRepository, never()).save(any());
    }

//    @Test
//    public void testUpdateAccount_ValidationFails() throws NotLoginException {
//        // Mock behavior of getCurrentAccount()
//        when(accountUtils.getCurrentAccount()).thenReturn(account);
//        updateRequestDTO.setPhone("sdfsd");
//        // Call the method under test and expect IllegalArgumentException
//        ValidationException exception = assertThrows(ValidationException.class, () -> accountService.update(updateRequestDTO));
//
//        // Verify exception message
//        assertEquals("Invalid phone number: " + updateRequestDTO.getPhone(), exception.getMessage());
//    }

    @Test
    public void testUpdateAccount_SaveFails() throws Exception {
        when(accountUtils.getCurrentAccount()).thenReturn(account);

        when(accountRepository.save(any(Account.class))).thenThrow(new RuntimeException("Save failed"));

        Exception exception = assertThrows(Exception.class, () -> accountService.update(updateRequestDTO));
        assertEquals("Can not update", exception.getMessage());
    }

    @Test
    public void testGetAccountsByRole() throws IOException {
        // Arrange
        AccountRoleEnum role = AccountRoleEnum.INSTRUCTOR;

        Account account = new Account();
        account.setName("Instructor Name");
        account.setRole(role);
        account.setIsDeleted(false);
        account.setAvatar("avatarUrl");

        Course course1 = new Course();
        course1.setId(1);
        course1.setName("Course 1");
        course1.setAccount(account);
        course1.setIsDeleted(false);

        Course course2 = new Course();
        course2.setId(2);
        course2.setName("Course 2");
        course2.setAccount(account);
        course2.setIsDeleted(false);

        Course course3 = new Course();
        course3.setId(3);
        course3.setName("Course 3");
        course3.setAccount(account);
        course3.setIsDeleted(false);

        List<Account> accounts = Arrays.asList(account);
        List<Course> courses = Arrays.asList(course1, course2, course3);

        when(accountRepository.findByRole(role)).thenReturn(accounts);
        when(courseRepository.findCoursesByAccount(account)).thenReturn(courses);
        when(uploadFileUtils.getSignedAvatarUrl("avatarUrl")).thenReturn("signedAvatarUrl");

        // Act
        List<InstructorResponseDTO> result = accountService.getAccountsByRole(role);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        InstructorResponseDTO instructorResponseDTO = result.get(0);
        assertEquals("Instructor Name", instructorResponseDTO.getName());
        assertEquals(role, instructorResponseDTO.getRole());
        assertEquals(3, instructorResponseDTO.getNumberCourse());
        assertEquals("signedAvatarUrl", instructorResponseDTO.getAvatar());

        verify(accountRepository, times(1)).findByRole(role);
        verify(courseRepository, times(1)).findCoursesByAccount(account);
        verify(uploadFileUtils, times(1)).getSignedAvatarUrl("avatarUrl");
    }


//    @Test
//    public void testGetAccountsByRole_NoAccounts() {
//        // Arrange
//        AccountRoleEnum role = AccountRoleEnum.INSTRUCTOR;
//
//        when(accountRepository.findByRole(role)).thenReturn(Collections.emptyList());
//
//        // Act & Assert
//        AccountException thrown = assertThrows(AccountException.class, () -> {
//            accountService.getAccountsByRole(role);
//        });
//
//        assertEquals("Role instructor is required!", thrown.getMessage());
//    }

}
