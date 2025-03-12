package com.ojt.mockproject.controller;
import com.ojt.mockproject.dto.Wallet.CreateWalletRequest;
import com.ojt.mockproject.dto.Wallet.Requests.AddPriceToWalletRequest;
import com.ojt.mockproject.dto.Wallet.Requests.BuyCourseRequest;
import com.ojt.mockproject.dto.Wallet.Requests.CashOutRequest;
import com.ojt.mockproject.dto.Wallet.Responses.AddPriceWalletResponse;
import com.ojt.mockproject.dto.Wallet.Responses.BuyCourseResponse;
import com.ojt.mockproject.dto.Wallet.Responses.CashOutResponse;
import com.ojt.mockproject.dto.Wallet.Responses.GetWalletResponseDTO;
import com.ojt.mockproject.dto.Wallet.UpdateWalletRequest;
import com.ojt.mockproject.dto.Wallet.WalletResponseDTO;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.exceptionhandler.NotFoundException;
import com.ojt.mockproject.exceptionhandler.Wallet.WalletException;
import com.ojt.mockproject.service.WalletService;
import com.ojt.mockproject.utils.AccountUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/wallets")
@CrossOrigin("*")
public class WalletController {

    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private WalletService walletService;

    @GetMapping("/current-account")
    public ResponseEntity<WalletResponseDTO> getWalletByCurrentAccount(@RequestHeader("Authorization") String token) {
        try {
            WalletResponseDTO walletResponse = walletService.getWalletByCurrentAccount();
            return new ResponseEntity<>(walletResponse, HttpStatus.OK);
        } catch (WalletException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    //get all wallet by id (have returns the total money coming in over the past 30 days/
    // the total money going out over the past 30 days)
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<WalletResponseDTO>> getAllWallets() {
        List<WalletResponseDTO> wallets = walletService.getAllWallets();
        return new ResponseEntity<>(wallets, HttpStatus.OK);
    }

    //get all wallet (have returns the total money coming in over the past 30 days/
    // the total money going out over the past 30 days)

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<WalletResponseDTO> getWalletById(@PathVariable Integer id) {
        WalletResponseDTO wallet = walletService.getWalletById(id);
        return new ResponseEntity<>(wallet, HttpStatus.OK);
    }

    //Create wallet by current account
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT', 'ADMIN')")
    @PostMapping ("/create-wallet")
    public ResponseEntity<GetWalletResponseDTO> createWallet(@Validated @RequestBody CreateWalletRequest createWalletRequest) {
        GetWalletResponseDTO response = walletService.createWallet(createWalletRequest);
        return ResponseEntity.ok(response);
    }
    //Get wallet by account id
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT', 'ADMIN')")
    @GetMapping("/account/{accountId}")
    public ResponseEntity<GetWalletResponseDTO> getWalletByAccountId(@PathVariable Integer accountId) {
        GetWalletResponseDTO responseDTO = walletService.getWalletByAccountIdResponse(accountId);
        return ResponseEntity.ok(responseDTO);
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT', 'ADMIN')")
    @PutMapping("/update-wallet/{walletId}")
    public ResponseEntity<GetWalletResponseDTO> updateWallet(@PathVariable Integer walletId, @RequestBody @Valid UpdateWalletRequest updateWalletRequest) {
        GetWalletResponseDTO responseDTO = walletService.updateWallet(walletId, updateWalletRequest);
        return ResponseEntity.ok(responseDTO);
    }

    // Update bang current account
    @PutMapping("/update-wallet-current")
    public ResponseEntity<GetWalletResponseDTO> updateWallet(
            @RequestBody @Valid UpdateWalletRequest updateWalletRequest) {
        try {
            GetWalletResponseDTO response = walletService.updateWalletCurrenAccount(updateWalletRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete wallet bang ID
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWallet(@PathVariable Integer id) {
        String response = walletService.deleteWallet(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-balance")
    public ResponseEntity<AddPriceWalletResponse> addPriceToWallet(@RequestBody @Valid AddPriceToWalletRequest request){
        Account account = accountUtils.getCurrentAccount();
        return walletService.addPriceToWallet(account.getId(), request.getInitialBalance());
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT', 'ADMIN')")
    @PostMapping("/cash-out")
    public ResponseEntity<CashOutResponse> cashOut(@RequestBody @Valid CashOutRequest request) {
        Account account = accountUtils.getCurrentAccount();
        return walletService.cashOut(account.getId(), request.getCashoutBalance());
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT')")
    @PostMapping("/buy-course-by-wallet")
    public ResponseEntity<BuyCourseResponse> buyCourseByWallet(@RequestBody @Valid BuyCourseRequest request) {
        Account account = accountUtils.getCurrentAccount();
        return walletService.buyCourseByWallet(account.getId(), request.getCourseId(), request.getPrice());
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT')")
    @GetMapping("/return")
    public ResponseEntity<AddPriceWalletResponse> returnAddBalance(
            @RequestParam String email,
            @RequestParam BigDecimal totalPrice,
            @RequestParam String code,
            @RequestParam boolean cancel,
            @RequestParam String status,
            @RequestParam String orderCode ) {
        Account account = accountUtils.getCurrentAccount();
        return walletService.handleReturnAddBalance(account, email, totalPrice, code, cancel, status, orderCode);
    }
}