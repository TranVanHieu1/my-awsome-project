package com.ojt.mockproject.controller;

import com.ojt.mockproject.dto.Account.Requests.AddSubscribeRequestDTO;
import com.ojt.mockproject.dto.Account.Requests.AddWishListRequest;
import com.ojt.mockproject.dto.Account.Requests.RemoveWishListRequest;
import com.ojt.mockproject.dto.Account.Requests.UpdateRequestDTO;
import com.ojt.mockproject.dto.Account.Responses.*;
import com.ojt.mockproject.dto.Course.UpcomingCourseResponseDTO;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Enum.AccountRoleEnum;
import com.ojt.mockproject.service.AccountService;
import com.ojt.mockproject.utils.AccountUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account")
@CrossOrigin("*")
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountUtils accountUtils;

    @GetMapping("/findByName")
    public Account welcome(@RequestBody String name) {
        return accountService.getAccountByName(name);
    }

    @PostMapping("/add-wishlist")
    public ResponseEntity<AddWishListResponse> addWishListResponse(@RequestHeader("Authorization") String token,
                                                   @RequestBody AddWishListRequest addWishListRequest) {
            return accountService.addWishList(token, addWishListRequest);
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT')")
    @GetMapping("/wishlist/courses")
    public ResponseEntity<WishListResponse> getCoursesFromWishlist(@RequestHeader("Authorization") String token) {
        return accountService.getCoursesFromWishlist(token);
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT')")
    @PostMapping("/remove-wishlist")
    public ResponseEntity<RemoveWishListResponse> removeWishListResponse(
            @RequestHeader("Authorization") String token,
            @RequestBody RemoveWishListRequest removeWishListRequest) {
        return accountService.removeWishList(token, removeWishListRequest);
    }


    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccountById(@PathVariable Integer accountId) {
        Account account = accountService.findById(accountId);
        return ResponseEntity.ok(account);
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT', 'ADMIN')")
    @PutMapping("/update-profile")
    public UpdateResponseDTO updateProfile(@RequestBody UpdateRequestDTO updateRequestDTO) throws Exception {
        return accountService.update(updateRequestDTO);
    }

    @GetMapping("/search-instructor")
    public List<InstructorResponseDTO> searchInstructor(){
        return accountService.getAccountsByRole(AccountRoleEnum.INSTRUCTOR);
    }

    @GetMapping("/get-ip")
    public String handleRequest(HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        System.out.println(ipAddress);
        return ipAddress;
    }

    @GetMapping("/purchased-course")
    public ResponseEntity<PurchasedCoursesResponse> purchasedCourse() {
        Account account = accountUtils.getCurrentAccount();
        return accountService.getPrchasedCoursesByAccount(account);
    }

    @GetMapping("/instructor/upcoming")
    public ResponseEntity<List<UpcomingCourseResponseDTO>> instructorViewPendingCourse() {
        return accountService.instructorViewPendingCourse();
    }

    @GetMapping("/get-avatar/{accountId}")
    public ResponseEntity<GetAvatarByAccountIdResponse> getAvatarByAccountId(@PathVariable Integer accountId) {
        return accountService.getAvatarByAccountId(accountId);
    }

    @PostMapping("/follow-instructor")
    public AddSubscribeResponseDTO followInstructor(@RequestBody AddSubscribeRequestDTO requestDTO) {
        return accountService.followInstructor(requestDTO);
    }

    @PutMapping("/unfollow-instructor")
    public AddSubscribeResponseDTO unfollowInstructor(@RequestBody AddSubscribeRequestDTO requestDTO) {
        return accountService.unfollowInstructor(requestDTO);
    }

    @GetMapping("/get-subscribe/{accountId}")
    public List<AddSubscribeResponseDTO> getAllSubscribe(@PathVariable Integer accountId) {
        return accountService.getAllSubscribe(accountId);
    }

    @GetMapping("/{instructorId}/followers")
    public List<AddSubscribeResponseDTO> getAllFollowers(@PathVariable Integer instructorId) {
        return accountService.getAllFollowers(instructorId);
    }

    @GetMapping("/get-instructor-by-ID/{instructorId}")
    public InstructorResponseDTO getInstructor(@PathVariable Integer instructorId){
        return  accountService.getInstructorsById(instructorId);
    }

    //Change Status Isdelete for account
    @PostMapping("is-delete-true/{accountId}")
    public ResponseEntity<String> isDeleteTrueAccount(@PathVariable Integer accountId) {
        accountService.changeIsDeleteAccount(accountId,true);
        return ResponseEntity.ok("Account with ID " + accountId + " has been deleted.");
    }
    @PostMapping("is-delete-false/{accountId}")
    public ResponseEntity<String> isDeleteFalseAccount(@PathVariable Integer accountId) {
        accountService.changeIsDeleteAccount(accountId,false);
        return ResponseEntity.ok("Account with ID " + accountId + " has been restored.");
    }
}
