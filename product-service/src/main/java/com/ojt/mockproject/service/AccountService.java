package com.ojt.mockproject.service;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.ojt.mockproject.dto.Account.Requests.AddSubscribeRequestDTO;
import com.ojt.mockproject.dto.Account.Requests.AddWishListRequest;
import com.ojt.mockproject.dto.Account.Requests.RemoveWishListRequest;
import com.ojt.mockproject.dto.Account.Requests.UpdateRequestDTO;
import com.ojt.mockproject.dto.Account.Responses.*;
import com.ojt.mockproject.dto.Auth.Requests.ChangePasswordRequest;
import com.ojt.mockproject.dto.Auth.Requests.ForgotPasswordRequest;
import com.ojt.mockproject.dto.Auth.Requests.ResetPasswordRequest;
import com.ojt.mockproject.dto.Auth.Responses.*;
import com.ojt.mockproject.dto.Course.CourseDTO;
import com.ojt.mockproject.dto.Auth.Login.LoginRequestDTO;
import com.ojt.mockproject.dto.Auth.Login.LoginResponseDTO;
import com.ojt.mockproject.dto.Auth.Register.RegisterRequestDTO;
import com.ojt.mockproject.dto.Course.UpcomingCourseResponseDTO;
import com.ojt.mockproject.entity.Enum.*;
import com.ojt.mockproject.exceptionhandler.course.CourseException;
import com.ojt.mockproject.utils.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.exceptionhandler.*;
import com.ojt.mockproject.exceptionhandler.account.AccountAppException;
import com.ojt.mockproject.exceptionhandler.account.AccountException;
import com.ojt.mockproject.exceptionhandler.account.NotLoginException;
import com.ojt.mockproject.exceptionhandler.account.UnableToSaveAccountException;
import com.ojt.mockproject.repository.AccountRepository;
import com.ojt.mockproject.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AccountService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private KafkaTemplate<String, Account> kafkaTemplate;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountUtils accountUtils;
    @Autowired
    private UploadFileUtils uploadFileUtils;

    private static final String AVATAR_CACHE_PREFIX = "avatar::";

    private static final String defaultAvatar = "logo.png";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public Account findById(Integer accountId) {
        return accountRepository.findById(accountId).orElse(null);
    }

    public Account getAccountByEmail(String email) {
        Optional<Account> account = accountRepository.findByEmail(email);
        return account.orElse(null);
    }

    //Please do not touch
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = accountRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userDetails;
    }

    public Account getAccountByName(String name) {
        Optional<Account> account = accountRepository.findAccountByName(name);
        return account.orElse(null);
    }

    public ResponseEntity<LoginResponseDTO> checkLogin(LoginRequestDTO loginRequestDTO) {
        try {
            Account account = getAccountByEmail(loginRequestDTO.getEmail());

            if (account == null) {
                throw new AuthAppException(ErrorCode.EMAIL_NOT_FOUND);
            }
            if (account.getStatus().equals(AccountStatusEnum.UNVERIFIED)) {
                throw new AuthAppException(ErrorCode.ACCOUNT_NOT_VERIFY);
            }
            Authentication authentication = null;
            try {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequestDTO.getEmail(),
                                loginRequestDTO.getPassword()
                        )
                );
            } catch (Exception e) {
                throw new AuthAppException(ErrorCode.USERNAME_PASSWORD_NOT_CORRECT);
            }

            System.out.println(authentication);

            if (authentication != null && authentication.isAuthenticated()) {
                Account returnAccount = (Account) authentication.getPrincipal();
                account.setTokens(jwtService.generateToken(account.getEmail()));
                account.setRefreshToken(jwtService.generateRefreshToken(account.getEmail()));

                String responseString = "Login successful";
                LoginResponseDTO loginResponseDTO = new LoginResponseDTO(
                        responseString,
                        null,
                        returnAccount.getTokens(),
                        returnAccount.getRefreshToken()
                );
                return new ResponseEntity<>(loginResponseDTO, HttpStatus.OK);
            } else {
                throw new UsernameNotFoundException("Invalid user request");
            }

        } catch (AuthAppException e) {
            ErrorCode errorCode = e.getErrorCode();
            String errorResponse = "Login failed";
            LoginResponseDTO loginResponseDTO = new LoginResponseDTO(
                    e.getMessage(),
                    errorResponse,
                    null,
                    null
            );
            return new ResponseEntity<>(loginResponseDTO, errorCode.getHttpStatus());
        }
    }

    private Account getAccountFromToken(String token) {
        String email = extractEmailFromToken(token);

        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new AuthAppException(ErrorCode.TOKEN_INVALID));
    }

    private String extractEmailFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String email = jwtService.extractEmail(token);

        if (email == null || email.isEmpty()) {
            throw new AuthAppException(ErrorCode.TOKEN_INVALID);
        }

        return email;
    }

    //Sau nay phai doi lai 1 chut
    private List<Integer> parseListToInterger(String wishlist) {
        return Arrays.stream(wishlist.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    //Dat lai ten
    private String getString(String token) {
        String email = jwtService.extractEmail(token);
        if (email == null || email.isEmpty()) {
            throw new AuthAppException(ErrorCode.TOKEN_INVALID);
        }
        return email;
    }


    public ResponseEntity<RegisterResponse> registerAccount(RegisterRequestDTO registerRequestDTO) {
        try {
            Account tempAccount = getAccountByEmail(registerRequestDTO.getEmail());
            if (tempAccount != null) {
                if (tempAccount.getStatus().equals(AccountStatusEnum.VERIFIED)) {
                    throw new AuthAppException(ErrorCode.EMAIL_EXISTED);
                } else if (tempAccount.getStatus().equals(AccountStatusEnum.UNVERIFIED)) {
                    throw new AuthAppException(ErrorCode.EMAIL_WAIT_VERIFY);
                }
            }
            Account account = new Account(registerRequestDTO.getName(), registerRequestDTO.getEmail(), registerRequestDTO.getAccountRoleEnum(), AccountStatusEnum.UNVERIFIED, false);
            account.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
            account.setProvider(AccountProviderEnum.LOCAL);
            account.setGender(AccountGenderEnum.MALE);
            account.setAvatar(defaultAvatar);
            accountRepository.save(account);

            String token = jwtService.generateToken(account.getEmail());
            account.setTokens(token);

            kafkaTemplate.send("email_register_account_topic", account);
            /* Send email right here if kafka is broken
            Email Service send Verify Account Mail Template */
            String responseMessage = "Successful registration, please check your email for verification";
            RegisterResponse response = new RegisterResponse(responseMessage, null, 201, registerRequestDTO.getEmail());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (AuthAppException e) {
            ErrorCode errorCode = e.getErrorCode();
            String errorMessage = "Register failed";
            RegisterResponse response = new RegisterResponse(errorMessage, errorCode.getMessage(), errorCode.getCode(), null);
            return new ResponseEntity<>(response, errorCode.getHttpStatus());
        }
    }

    public boolean verifyAccount(String token) throws Exception {
        try {
            String email = jwtService.extractEmail(token);

            Account accountEntity = getAccountByEmail(email);
            accountEntity.setStatus(AccountStatusEnum.VERIFIED);
            saveAccountChanges(accountEntity);
            return true;
        } catch (Exception e) {
            throw new TokenExpiredException("Invalid or expired token!", Instant.now());
        }
    }

    public void saveAccountChanges(Account accountEntity) throws Exception {
        try {
            accountRepository.save(accountEntity);
        } catch (Exception e) {
            throw new UnableToSaveAccountException("Unable to save the account to database, please re-check it. /n At saveAccountChanges At Account Service");
        }
    }

    public ResponseEntity<ForgotPasswordResponse> forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        try {
            Optional<Account> tempAccount = accountRepository.findByEmail(forgotPasswordRequest.getEmail());

            Account checkAccount = tempAccount.orElseThrow(() -> new AuthAppException(ErrorCode.EMAIL_NOT_FOUND));

            if (checkAccount.getEmail() == null || checkAccount.getEmail().isEmpty() || checkAccount.getStatus().equals(AccountStatusEnum.UNVERIFIED)) {
                throw new AuthAppException(ErrorCode.EMAIL_NOT_FOUND);
            }

            String token = jwtService.generateToken(forgotPasswordRequest.getEmail());
            Account account = tempAccount.orElseThrow(() -> new UsernameNotFoundException("User not found"));
            account.setTokens(token);

            kafkaTemplate.send("email_forgot_password_topic", account);

            accountRepository.save(account);
            ForgotPasswordResponse forgotPasswordResponse = new ForgotPasswordResponse("Password reset token generated successfully.", null, 200);
            return new ResponseEntity<>(forgotPasswordResponse, HttpStatus.OK);
        } catch (AuthAppException e) {
            ErrorCode errorCode = e.getErrorCode();
            ForgotPasswordResponse forgotPasswordResponse = new ForgotPasswordResponse("Password reset failed", e.getMessage(), errorCode.getCode());
            return new ResponseEntity<>(forgotPasswordResponse, errorCode.getHttpStatus());
        }
    }

    public ResponseEntity<ResetPasswordResponse> resetPassword(ResetPasswordRequest resetPasswordRequest, String token) {
        try {

            if (!resetPasswordRequest.getNew_password().equals(resetPasswordRequest.getRepeat_password())) {
                throw new AuthAppException(ErrorCode.PASSWORD_REPEAT_INCORRECT);
            }
            // CALL FUNC
            String email = getString(token);

            Optional<Account> accountOptional = accountRepository.findByEmail(email);
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                account.setPassword(passwordEncoder.encode(resetPasswordRequest.getNew_password()));
                accountRepository.save(account);
            }

            ResetPasswordResponse resetPasswordResponse = new ResetPasswordResponse("Password reset token generated successfully.", null, 200);
            return new ResponseEntity<>(resetPasswordResponse, HttpStatus.CREATED);
        } catch (AuthAppException e) {
            ErrorCode errorCode = e.getErrorCode();
            ResetPasswordResponse resetPasswordResponse = new ResetPasswordResponse("Password reset failed", e.getMessage(), errorCode.getCode());
            return new ResponseEntity<>(resetPasswordResponse, errorCode.getHttpStatus());
        }

    }

    public ResponseEntity<ChangePasswordResponse> changePassword(String token, ChangePasswordRequest
            changePasswordRequest) {
        try {
            Account account = getAccountFromToken(token);

            if (!passwordEncoder.matches(changePasswordRequest.getOld_password(), account.getPassword())) {
                throw new AuthAppException(ErrorCode.OLD_PASSWORD_INCORRECT);
            }

            if (!changePasswordRequest.getNew_password().equals(changePasswordRequest.getRepeat_password())) {
                throw new AuthAppException(ErrorCode.PASSWORD_REPEAT_INCORRECT);
            }

            account.setPassword(passwordEncoder.encode(changePasswordRequest.getNew_password()));
            accountRepository.save(account);

            ChangePasswordResponse changePasswordResponse = new ChangePasswordResponse("Password changed successfully", null, 200);
            return new ResponseEntity<>(changePasswordResponse, HttpStatus.OK);
        } catch (AuthAppException e) {
            ErrorCode errorCode = e.getErrorCode();
            ChangePasswordResponse changePasswordResponse = new ChangePasswordResponse("Password change failed", e.getMessage(), errorCode.getCode());
            return new ResponseEntity<>(changePasswordResponse, errorCode.getHttpStatus());
        }

    }

    public ResponseEntity<AddWishListResponse> addWishList(String token, AddWishListRequest addWishListRequest) {
        try {
            // PARSE STRING COURSE ID TO INT
            int courseId = Integer.parseInt(addWishListRequest.getCourseId());
            courseRepository.findById(courseId)
                    .orElseThrow(() -> new AccountAppException(ErrorCode.COURSE_NOT_FOUND));
            // GET ACCOUNT BY TOKEN ( TOKEN IN HEADER )
            Account account = getAccountFromToken(token);

            // HANDLE LOGIC ADD WISHLIST
                // USE TERNARY OPERATOR
                // STEP 1 CHECK IF FIELD WISHLIST == NULL THEN ADD SINGLE COURSE
                // ELSE ADD COURSES AFTER ","
            String updatedWishlist = account.getWishlist() == null ? addWishListRequest.getCourseId()
                    : account.getWishlist() + "," + addWishListRequest.getCourseId();
            account.setWishlist(updatedWishlist);
            accountRepository.save(account);

            AddWishListResponse.DataResponse dataResponse = new AddWishListResponse.DataResponse(updatedWishlist);
            AddWishListResponse addWishListResponse = new AddWishListResponse("Course added to wishlist successfully", null, 201, dataResponse);
            return new ResponseEntity<>(addWishListResponse, HttpStatus.CREATED);
        } catch (AccountAppException e) {
            ErrorCode errorCode = e.getErrorCode();
            AddWishListResponse addWishListResponse = new AddWishListResponse("Add wishlist failed", e.getMessage(), errorCode.getCode(), null);
            return new ResponseEntity<>(addWishListResponse, errorCode.getHttpStatus());
        }
    }

    public ResponseEntity<WishListResponse> getCoursesFromWishlist(String token) {
        try {
            // GET ACCOUNT BY TOKEN ( TOKEN IN HEADER )
            Account account = getAccountFromToken(token);

            if (account == null || account.getWishlist() == null || account.getWishlist().isEmpty()) {
                throw new AccountAppException(ErrorCode.NO_COURSE_IN_WISHLIST);
            }

            List<Integer> courseIds = parseListToInterger(account.getWishlist());
            List<Course> courses = courseRepository.findByIdIn(courseIds);

            List<CourseDTO> courseDTOS = courses.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            WishListResponse wishListResponse = new WishListResponse("Get wishlist success", null, 200, courseDTOS);
            return new ResponseEntity<>(wishListResponse, HttpStatus.OK);
        } catch (AccountAppException e) {
            ErrorCode errorCode = e.getErrorCode();
            WishListResponse wishListResponse = new WishListResponse("Get wishlist failed", e.getMessage(), errorCode.getCode(), null);
            return new ResponseEntity<>(wishListResponse, errorCode.getHttpStatus());
        }
    }

    public ResponseEntity<RemoveWishListResponse> removeWishList(String token, RemoveWishListRequest removeWishListRequest) {
        try {
            int courseId = Integer.parseInt(removeWishListRequest.getCourseId());

            Account account = getAccountFromToken(token);
            if (account.getWishlist() == null || account.getWishlist().isEmpty()) {
                throw new AccountAppException(ErrorCode.NO_COURSE_IN_WISHLIST);
            }

            List<String> wishlist = new ArrayList<>(Arrays.asList(account.getWishlist().split(",")));
            if (!wishlist.contains(String.valueOf(courseId))) {
                throw new AccountAppException(ErrorCode.COURSE_NOT_FOUND_IN_WISHLIST);
            }

            wishlist.remove(String.valueOf(courseId));
            String updatedWishlist = String.join(",", wishlist);
            account.setWishlist(updatedWishlist);
            accountRepository.save(account);

            List<CourseDTO> updatedWishlistDetails = wishlist.stream()
                    .map(id -> courseRepository.findById(Integer.parseInt(id)).orElse(null))
                    .filter(course -> course != null)
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            RemoveWishListResponse.DataResponse dataResponse = new RemoveWishListResponse.DataResponse(updatedWishlistDetails);
            RemoveWishListResponse removeWishListResponse = new RemoveWishListResponse("Course removed from wishlist successfully", null, 200, dataResponse);
            return new ResponseEntity<>(removeWishListResponse, HttpStatus.OK);
        } catch (AccountAppException e) {
            ErrorCode errorCode = e.getErrorCode();
            RemoveWishListResponse removeWishListResponse = new RemoveWishListResponse("Remove wishlist failed", e.getMessage(), errorCode.getCode(), null);
            return new ResponseEntity<>(removeWishListResponse, errorCode.getHttpStatus());
        } catch (NumberFormatException e) {
            RemoveWishListResponse removeWishListResponse = new RemoveWishListResponse("Invalid course ID format", e.getMessage(), 400, null);
            return new ResponseEntity<>(removeWishListResponse, HttpStatus.BAD_REQUEST);
        }
    }


    private CourseDTO convertToDto(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setName(course.getName());
        dto.setCategory(course.getCategory());
        dto.setPrice(course.getPrice());
        dto.setInstructorName(course.getAccount().getName());
        return dto;
    }

    //Search instructor by role
    public List<InstructorResponseDTO> getAccountsByRole(AccountRoleEnum role) {
        try {
            // Get accounts by role except accounts that were deleted
            List<Account> listAccount = accountRepository.findByRole(role).stream()
                    .filter(account -> !account.getIsDeleted())
                    .collect(Collectors.toList());

            List<InstructorResponseDTO> listInstructor = new ArrayList<>();

            for (Account account : listAccount) {
                //get number course by account
                List<Course> courseList = courseRepository.findCoursesByAccount(account);

                Integer numberCourse = 0;
                numberCourse = courseList.size();
                //get avatar of instructor
                String avatar;
                if (account.getAvatar() != null) {
                    avatar = uploadFileUtils.getSignedAvatarUrl(account.getAvatar());
                } else {
                    avatar = defaultAvatar;
                }
                listInstructor.add(new InstructorResponseDTO(account.getName(), account.getRole(), numberCourse, avatar));
            }


            if (listInstructor.isEmpty()) {
                throw new AccountException("There is no: " + role, ErrorCode.INTERNAL_SERVER_ERROR);
            }

            return listInstructor;
        } catch (Exception e) {
            throw new AccountException(e.getMessage());
        }

    }

    //Search instructor by id
    public InstructorResponseDTO getInstructorsById(Integer instructorID) {
        try {
            // Get accounts by role except accounts that were deleted
            Account account = accountRepository.findInstructorById(instructorID);

                //get number course by account
                List<Course> courseList = courseRepository.findCoursesByAccount(account);

                int numberCourse = 0;
                numberCourse = courseList.size();
                //get avatar of instructor
                String avatar;
                if (account.getAvatar() != null) {
                    avatar = uploadFileUtils.getSignedAvatarUrl(account.getAvatar());
                } else {
                    avatar = defaultAvatar;
                }
                if(!account.getRole().equals(AccountRoleEnum.INSTRUCTOR)){
                    throw new AccountException("There is no instructor!");
                }
            return new InstructorResponseDTO(account.getName(), account.getRole(), numberCourse, avatar, account.getAboutMe());


        } catch (Exception e) {
            throw new AccountException("Can not find instructor");
        }

    }


    //Update information of user
    public UpdateResponseDTO update(UpdateRequestDTO updateRequestDTO) throws Exception {
        //get current user
        Account account = null;
        try {
            account = accountUtils.getCurrentAccount();
        } catch (Exception ex) {
            throw new NotLoginException("Not Login");
        }

        account = UpdateUtils.updateAccount(updateRequestDTO, account);

        try {
            accountRepository.save(account);
            return new UpdateResponseDTO(
                    account.getName(),
                    account.getHeadline(),
                    account.getAboutMe(),
                    account.getPersonalSiteLink(),
                    account.getFacebookLink(),
                    account.getTwitterLink(),
                    account.getLinkedinLink(),
                    account.getYoutubeLink());
        } catch (Exception ex) {
            throw new Exception("Can not update");
        }
    }

    public ResponseEntity<PurchasedCoursesResponse> getPrchasedCoursesByAccount(Account account) {
        try {
            List<Integer> courseIds = parseListToInterger(account.getPurchasedCourse());
            List<Course> courses = courseRepository.findByIdIn(courseIds);

            List<CourseDTO> courseDtos = courses.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            PurchasedCoursesResponse purchasedCoursesResponse = new PurchasedCoursesResponse("Get purchased courses success", null, 200, courseDtos);
            return new ResponseEntity<>(purchasedCoursesResponse, HttpStatus.OK);
        } catch (AccountAppException e) {
            ErrorCode errorCode = e.getErrorCode();
            PurchasedCoursesResponse purchasedCoursesResponse = new PurchasedCoursesResponse("Get purchased courses failed", e.getMessage(), errorCode.getCode(), null);
            return new ResponseEntity<>(purchasedCoursesResponse, errorCode.getHttpStatus());
        }
    }

    public ResponseEntity<GetProfileResponse> getProfile() {
        Account account = accountUtils.getCurrentAccount();
        String signedImageUrl = null;

        try {
            signedImageUrl = uploadFileUtils.getSignedImageUrl(account.getAvatar());
        } catch (Exception e) {
            throw new AccountException(e.getMessage());
        }

        GetProfileResponse getProfileResponse = new GetProfileResponse(
                account.getId(),
                account.getName(),
                account.getEmail(),
                account.getPhone(),
                account.getRole(),
                signedImageUrl,
                account.getAboutMe()
        );
        return ResponseEntity.ok(getProfileResponse);
    }

    @Transactional
    public AddSubscribeResponseDTO followInstructor(AddSubscribeRequestDTO requestDTO) {
        Optional<Account> optionalAccount = accountRepository.findById(Integer.valueOf(requestDTO.getUserId()));
        Optional<Account> optionalSubscribe = accountRepository.findById(Integer.valueOf(requestDTO.getInstructorId()));

        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            if (optionalSubscribe.isPresent()) {
                Account subscribeAccount = optionalSubscribe.get();
                String subscribe = account.getSubscribe();
                String subscribers = subscribeAccount.getSubscribers();

                // Kiểm tra nếu đã theo dõi người đó rồi
                if (subscribe != null && !subscribe.isEmpty()) {
                    List<String> subscribeIds = Arrays.asList(subscribe.split(","));
                    if (subscribeIds.contains(String.valueOf(subscribeAccount.getId()))) {
                        throw new RuntimeException("You are already following this person");
                    }
                }

                // Subscribe list
                if (subscribe == null || subscribe.isEmpty()) {
                    account.setSubscribe(String.valueOf(subscribeAccount.getId()));
                } else {
                    account.setSubscribe(subscribe + "," + subscribeAccount.getId());
                }

                // Update Subscribers list
                if (subscribers == null || subscribers.isEmpty()) {
                    subscribeAccount.setSubscribers(String.valueOf(account.getId()));
                } else {
                    subscribeAccount.setSubscribers(subscribers + "," + account.getId());
                }

                accountRepository.save(account);
                accountRepository.save(subscribeAccount);

                AddSubscribeResponseDTO responseDTO = new AddSubscribeResponseDTO();
                responseDTO.setId(subscribeAccount.getId());
                responseDTO.setName(subscribeAccount.getName());
                responseDTO.setAvatar(subscribeAccount.getAvatar());

                return responseDTO;
            } else {
                throw new RuntimeException("Subscribe not found with id: " + requestDTO.getInstructorId());
            }
        } else {
            throw new RuntimeException("Account not found with id: " + requestDTO.getUserId());
        }
    }

    @Transactional
    public AddSubscribeResponseDTO unfollowInstructor(AddSubscribeRequestDTO requestDTO) {
        Optional<Account> optionalAccount = accountRepository.findById(Integer.valueOf(requestDTO.getUserId()));
        Optional<Account> optionalSubscribe = accountRepository.findById(Integer.valueOf(requestDTO.getInstructorId()));

        if (optionalAccount.isPresent() && optionalSubscribe.isPresent()) {
            Account account = optionalAccount.get();
            Account subscribeAccount = optionalSubscribe.get();

            String subscribe = account.getSubscribe();
            String subscribers = subscribeAccount.getSubscribers();

            // Kiểm tra nếu giảng viên không có trong danh sách theo dõi
            if (subscribe == null || !Arrays.asList(subscribe.split(",")).contains(String.valueOf(subscribeAccount.getId()))) {
                throw new RuntimeException("Not following the instructor with id: " + requestDTO.getInstructorId());
            }

            // Unsubscribe list
            if (subscribe != null && !subscribe.isEmpty()) {
                account.setSubscribe(Arrays.stream(subscribe.split(","))
                        .filter(id -> !id.equals(String.valueOf(subscribeAccount.getId())))
                        .collect(Collectors.joining(",")));
            }

            // Update Subscribers list
            if (subscribers != null && !subscribers.isEmpty()) {
                subscribeAccount.setSubscribers(Arrays.stream(subscribers.split(","))
                        .filter(id -> !id.equals(String.valueOf(account.getId())))
                        .collect(Collectors.joining(",")));
            }

            accountRepository.save(account);
            accountRepository.save(subscribeAccount);

            AddSubscribeResponseDTO responseDTO = new AddSubscribeResponseDTO();
            responseDTO.setId(subscribeAccount.getId());
            responseDTO.setName(subscribeAccount.getName());
            responseDTO.setAvatar(subscribeAccount.getAvatar());

            return responseDTO;
        } else {
            if (!optionalAccount.isPresent()) {
                throw new RuntimeException("Account not found with id: " + requestDTO.getUserId());
            } else {
                throw new RuntimeException("Subscribe not found with id: " + requestDTO.getInstructorId());
            }
        }
    }

    public List<AddSubscribeResponseDTO> getAllSubscribe(Integer userId) {
        Optional<Account> optionalAccount = accountRepository.findById(userId);

        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            String subscribe = account.getSubscribe();

            if (subscribe == null || subscribe.isEmpty()) {
                return Collections.emptyList();
            }

            List<Integer> subscribeIds = Arrays.stream(subscribe.split(","))
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());

            List<AddSubscribeResponseDTO> subscribedInstructors = new ArrayList<>();
            for (Integer subscribeId : subscribeIds) {
                Optional<Account> subscribedAccountOpt = accountRepository.findById(subscribeId);
                subscribedAccountOpt.ifPresent(subscribedAccount -> {
                    AddSubscribeResponseDTO responseDTO = new AddSubscribeResponseDTO();
                    responseDTO.setId(subscribedAccount.getId());
                    responseDTO.setName(subscribedAccount.getName());
                    responseDTO.setAvatar(subscribedAccount.getAvatar());
                    subscribedInstructors.add(responseDTO);
                });
            }
            return subscribedInstructors;
        } else {
            throw new RuntimeException("Account not found with id: " + userId);
        }
    }

    public List<AddSubscribeResponseDTO> getAllFollowers(Integer instructorId) {
        Optional<Account> optionalAccount = accountRepository.findById(instructorId);

        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            String subscribers = account.getSubscribers();

            if (subscribers == null || subscribers.isEmpty()) {
                return Collections.emptyList();
            }

            List<Integer> subscriberIds = Arrays.stream(subscribers.split(","))
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());

            List<AddSubscribeResponseDTO> followers = new ArrayList<>();
            for (Integer subscriberId : subscriberIds) {
                Optional<Account> followerAccountOpt = accountRepository.findById(subscriberId);
                followerAccountOpt.ifPresent(followerAccount -> {
                    AddSubscribeResponseDTO responseDTO = new AddSubscribeResponseDTO();
                    responseDTO.setId(followerAccount.getId());
                    responseDTO.setName(followerAccount.getName());
                    responseDTO.setAvatar(followerAccount.getAvatar());
                    followers.add(responseDTO);
                });
            }
            return followers;
        } else {
            throw new RuntimeException("Instructor not found with id: " + instructorId);
        }
    }


    public ResponseEntity<UploadAvatarResponse> uploadAvatar(Account account, String url, MultipartFile file) {
        try {
            int maxWidthSizeImage = 1000;
            String signedImageUrl = null;

            String folderName = account.getEmail() + url;
            String imageUrl = uploadFileUtils.uploadFile(folderName, file, maxWidthSizeImage);
            account.setAvatar(imageUrl);
            account.setUpdateAt(LocalDateTime.now());
            accountRepository.save(account);
            try {
                signedImageUrl = uploadFileUtils.getSignedImageUrl(account.getAvatar());
            } catch (IOException e) {
                log.error("Error generating signed URL: " + e.getMessage());
            }

            UploadAvatarResponse uploadAvatarResponse = new UploadAvatarResponse();
            uploadAvatarResponse.setAccountId(account.getId());
            uploadAvatarResponse.setImageUrl(signedImageUrl);
            return ResponseEntity.ok(uploadAvatarResponse);
        } catch (Exception e) {
            throw new AccountException(e.getMessage());
        }
    }


    public ResponseEntity<GetAvatarByAccountIdResponse> getAvatarByAccountId(Integer accountId) {
        String cacheKey = AVATAR_CACHE_PREFIX + accountId;
        GetAvatarByAccountIdResponse cachedResponse = (GetAvatarByAccountIdResponse) redisTemplate.opsForValue().get(cacheKey);

        if (cachedResponse != null) {
            return ResponseEntity.ok(cachedResponse);
        }

        Optional<Account> account = accountRepository.findById(accountId);
        if (!account.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        GetAvatarByAccountIdResponse getAvatarByAccountIdResponse = new GetAvatarByAccountIdResponse();
        getAvatarByAccountIdResponse.setAccountId(account.get().getId());
        String signedImageUrl = null;

        try {
            signedImageUrl = uploadFileUtils.getSignedAvatarUrl(account.get().getAvatar());
        } catch (Exception e) {
            throw new AccountException(e.getMessage());
        }
        getAvatarByAccountIdResponse.setAvatar(signedImageUrl);
        getAvatarByAccountIdResponse.setName(account.get().getName());

        redisTemplate.opsForValue().set(cacheKey, getAvatarByAccountIdResponse, 7, TimeUnit.DAYS);

        return ResponseEntity.ok(getAvatarByAccountIdResponse);
    }

    public ResponseEntity<List<UpcomingCourseResponseDTO>> instructorViewPendingCourse() {
        //get current user
        Account account = null;
        try {
            account = accountUtils.getCurrentAccount();
        } catch (Exception ex) {
            throw new NotLoginException("Not Login");
        }

        List<UpcomingCourseResponseDTO> upcomingCourseResponseDTOList = new ArrayList<>();
        List<Course> courseList = null;
        try {
            courseList = courseRepository.findCourseByAccountAndStatus(account, CourseStatusEnum.PENDING);
        } catch (Exception e) {
            throw new CourseException("No course was found!", ErrorCode.COURSE_NOT_FOUND);
        }

        for (Course course : courseList) {
            if (!course.getCrashCourseVideos().isEmpty()) {
                UpcomingCourseResponseDTO upcomingCourseResponseDTO = new UpcomingCourseResponseDTO(
                        course.getName(),
                        course.getCrashCourseVideos().get(0).getThumbnail(),
                        course.getCategory(),
                        course.getPrice(),
                        course.getCreateAt(),
                        course.getStatus());
                upcomingCourseResponseDTOList.add(upcomingCourseResponseDTO);
            } else {
                UpcomingCourseResponseDTO upcomingCourseResponseDTO = new UpcomingCourseResponseDTO(
                        course.getName(),
                        "https://th.bing.com/th/id/OIP.v-SSnSruWQfbLMEHKw5TigHaFj?rs=1&pid=ImgDetMain",
                        course.getCategory(),
                        course.getPrice(),
                        course.getCreateAt(),
                        course.getStatus());
                upcomingCourseResponseDTOList.add(upcomingCourseResponseDTO);
            }
        }

        return ResponseEntity.ok(upcomingCourseResponseDTOList);
    }

    //Tuan Dat
    @Transactional
    public void changeIsDeleteAccount(Integer accountId, boolean isDelete) {
        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountException("Order not found with id: " + accountId, ErrorCode.ORDER_NOT_FOUND));
            account.setIsDeleted(isDelete);
            accountRepository.save(account);
        } catch (AccountException e) {
            throw e;
        } catch (Exception e) {
            throw new AccountException("Failed to delete account", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
