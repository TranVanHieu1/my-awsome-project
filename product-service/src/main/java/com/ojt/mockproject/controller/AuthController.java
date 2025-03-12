package com.ojt.mockproject.controller;

import com.ojt.mockproject.config.SecuredRestController;
import com.ojt.mockproject.dto.Account.Requests.UploadAvatarRequest;
import com.ojt.mockproject.dto.Account.Responses.GetAvatarByAccountIdResponse;
import com.ojt.mockproject.dto.Account.Responses.GetProfileResponse;
import com.ojt.mockproject.dto.Auth.Register.GoogleAccountDTO;
import com.ojt.mockproject.dto.Auth.Register.Response;
import com.ojt.mockproject.dto.Auth.Register.Token;
import com.ojt.mockproject.dto.Auth.Requests.ChangePasswordRequest;
import com.ojt.mockproject.dto.Auth.Requests.ForgotPasswordRequest;
import com.ojt.mockproject.dto.Auth.Requests.ResetPasswordRequest;
import com.ojt.mockproject.dto.Auth.Responses.*;
import com.ojt.mockproject.dto.Auth.Login.LoginRequestDTO;
import com.ojt.mockproject.dto.Auth.Login.LoginResponseDTO;
import com.ojt.mockproject.dto.Auth.Register.RegisterRequestDTO;
import com.ojt.mockproject.email.EmailService;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Enum.AccountRoleEnum;
import com.ojt.mockproject.exceptionhandler.BadRequest;
import com.ojt.mockproject.repository.AccountRepository;
import com.ojt.mockproject.service.AccountService;
import com.ojt.mockproject.service.SocialTokenVerify.VerifyTokenGoogle;
import jakarta.validation.Valid;
import com.ojt.mockproject.utils.AccountUtils;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.BindingResult;
import java.net.URI;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController implements SecuredRestController {

    @Autowired
    private VerifyTokenGoogle verifyTokenGoogle;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EmailService emailService;
    @Autowired
    private AccountUtils accountUtils;

    @GetMapping("")
    public String home() {
        return "Home XD";
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome XD";
    }

    @GetMapping("/secure")
    public String secure() {
        return "Secured hehehehe";
    }

    //Login
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return accountService.checkLogin(loginRequestDTO);
    }


    //Nguoi dung dang ky tai khoan
    //Kiem tra tai khoan co ton tai hay chua, co xac thuc tai khoan hay chua
    //Gui mail xac thuc tai khoan neu dang ky thanh cong
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerAccount(@RequestBody @Valid RegisterRequestDTO registerRequestDTO) {
        return accountService.registerAccount(registerRequestDTO);
    }

    @GetMapping("/login/google")
    public ResponseEntity loginGoogle(@Valid @RequestBody Token token) {
        // input: token

        // process: check coi có account trong hệ thống chưa
        GoogleAccountDTO googleAccountDTO = verifyTokenGoogle.verifyToken(token.getAccessToken());

        // check tồn tại chưa?
        Account acc = accountService.getAccountByEmail(googleAccountDTO.getEmail());
        if (acc == null) {
            // account da ton tai
            throw new BadRequest("Account does not exist!");
        }
        Response<Account> response = new Response<Account>(200, "Login success", acc);
        // response: kết qủa
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register-gg")
    public ResponseEntity registerGoogleAccount(@Valid @RequestBody Token token) {
        // input: token

        // lấy thông tin từ trong token
        GoogleAccountDTO googleAccountDTO = verifyTokenGoogle.verifyToken(token.getAccessToken());

        // check tồn tại chưa?
        Account acc = accountService.getAccountByEmail(googleAccountDTO.getEmail());
        Account newAccount;
        if (acc == null) {
            // chua co account
            acc = new Account();
            acc.setEmail(googleAccountDTO.getEmail());
            acc.setAvatar(googleAccountDTO.getPicture());
            acc.setName(googleAccountDTO.getFullName());
            acc.setRole(AccountRoleEnum.STUDENT);
            newAccount = accountRepository.save(acc);
        } else {
            // account da ton tai
            throw new BadRequest("Account already exist!");
        }

        Response<Account> response = new Response<Account>(200, "Register success", newAccount);
        // response: kết qủa
        return ResponseEntity.ok(response);
    }

    //Nguoi dung bam link verify trong email
    //Check status = AccountStatusEnum.ACTIVATED
    //Redirect to welcome page
    @GetMapping("/verify/{token}")
    public ResponseEntity<Void> activateAccount(@PathVariable String token) throws Exception {
        if (accountService.verifyAccount(token)) {
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("http://localhost:5173/login")).build();
        }
        return null;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        return accountService.forgotPassword(forgotPasswordRequest);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponse> resetPassword(@RequestParam("token") String token,
                                                               @RequestBody ResetPasswordRequest resetPasswordRequest) {
        return accountService.resetPassword(resetPasswordRequest, token);

    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT')")
    @PostMapping("/change-password")
    public ResponseEntity<ChangePasswordResponse> changePassword(@RequestHeader("Authorization") String token,
                                                                 @RequestBody ChangePasswordRequest changePasswordRequest) {
        return accountService.changePassword(token, changePasswordRequest);
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT', 'ADMIN')")
    @GetMapping("/profile")
    public ResponseEntity<GetProfileResponse> getProfile() {
        return accountService.getProfile();
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT', 'ADMIN')")
    @PostMapping(value = "/upload-avatar", consumes = "multipart/form-data")
    public ResponseEntity<UploadAvatarResponse> uploadAvatar( @ModelAttribute UploadAvatarRequest uploadAvatarRequest, ServletRequest request) {
        Account account = accountUtils.getCurrentAccount();
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String url = httpRequest.getRequestURI();
        return accountService.uploadAvatar(account, url, uploadAvatarRequest.getFile());
    }
}
