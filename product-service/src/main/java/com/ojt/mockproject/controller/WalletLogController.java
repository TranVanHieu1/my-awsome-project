package com.ojt.mockproject.controller;

import com.ojt.mockproject.dto.WalletLog.Requests.WalletLogRequestDTO;
import com.ojt.mockproject.dto.WalletLog.Response.WalletLogAccountResponseDTO;
import com.ojt.mockproject.dto.WalletLog.Response.WalletLogResponseDTO;
import com.ojt.mockproject.service.WalletLogService;
import com.ojt.mockproject.utils.AccountUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/wallet-log")
public class WalletLogController {
    @Autowired
    private WalletLogService walletLogService;
    @Autowired
    private AccountUtils accountUtils;

    //View all wallet log
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping
    public List<WalletLogResponseDTO> getAllWalletLogs() {
        return walletLogService.findAll();
    }

    //View wallet log by walletLogID
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT', 'ADMIN')")
    @GetMapping("/view-by-id/{id}")
    public ResponseEntity<WalletLogResponseDTO> getWalletLogById(@PathVariable Integer id) {
        WalletLogResponseDTO walletLogs = walletLogService.getWalletLogById(id);
        return ResponseEntity.ok(walletLogs);
    }

    //View wallet log by accountID
    @GetMapping("/by-account/{accountId}")
    public ResponseEntity<List<WalletLogAccountResponseDTO>> getAllWalletLogByAccountId(@PathVariable Integer accountId) {
        List<WalletLogAccountResponseDTO> walletLogs = walletLogService.getAllWalletLogsByAccountId(accountId);
        return ResponseEntity.ok(walletLogs);
    }

    //View wallet log by walletID
    @GetMapping("/view-by-walletId/{walletId}")
    public List<WalletLogResponseDTO> getWalletLogByWalletId(@PathVariable Integer walletId) {
        return walletLogService.getWalletLogByWalletId(walletId);
    }

    //Create wallet log
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT', 'ADMIN')")
    @PostMapping
    public ResponseEntity<WalletLogResponseDTO> createWalletLog(@Valid @RequestBody WalletLogRequestDTO walletLogRequestDTO) {
            WalletLogResponseDTO createdWalletLog = walletLogService.save(walletLogRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdWalletLog);
    }

    //Update wallet log by walletLogId
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<WalletLogResponseDTO> updateWalletLog(@PathVariable Integer id, @Valid @RequestBody WalletLogRequestDTO walletLogRequestDTO) {
            WalletLogResponseDTO updatedWalletLog = walletLogService.update(id, walletLogRequestDTO);
            return ResponseEntity.ok(updatedWalletLog);
    }

    //Delete wallet log
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWalletLog(@PathVariable Integer id) {
            walletLogService.deleteById(id);
            return ResponseEntity.ok("Wallet log with id " + id + " deleted");
    }

    //View wallet log by month
    @GetMapping("/view-wallet-log-by-month")
    public List<WalletLogResponseDTO> getAllWalletLogsByMonth(@RequestParam int month, @RequestParam int year) {
        return walletLogService.getAllWalletLogsByMonth(month, year);
    }

    //view wallet log by date
    @GetMapping("/view-wallet-log-by-date")
    public List<WalletLogResponseDTO> getAllWalletLogsByDate(@RequestParam int day, @RequestParam int month, @RequestParam int year) {
        return walletLogService.getWalletLogsByDate(day, month, year);
    }

    //View total sale, total amount last 12 days
    @GetMapping("/last-12-days")
    public List<Map<String, Object>> getWalletLogsForLast12Days() {
        return walletLogService.getWalletLogsForLast12Days();
    }


}