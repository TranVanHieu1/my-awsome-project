package com.ojt.mockproject.controller;

import com.ojt.mockproject.dto.Transaction.Responses.GetCashoutTransactionResponse;
import com.ojt.mockproject.dto.Transaction.TransactionDetailsDTO;
import com.ojt.mockproject.dto.Transaction.TransactionRequestDTO;
import com.ojt.mockproject.dto.Transaction.TransactionResponseDTO;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Enum.TransactionTypeEnum;
import com.ojt.mockproject.service.TransactionService;
import com.ojt.mockproject.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
@Validated
@CrossOrigin("*")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountUtils accountUtils;

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT', 'ADMIN')")
    @PostMapping("/create")
     public TransactionResponseDTO createTransaction (@RequestBody TransactionRequestDTO transactionRequestDTO) {
        return transactionService.createTransaction(transactionRequestDTO);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<TransactionResponseDTO> getAllTransactions(@PathVariable Integer orderId) {
        Account acccount = accountUtils.getCurrentAccount();
        return transactionService.getTransactionByOrderID(acccount, orderId);
    }

    @GetMapping("/wallet-log/{walletLogId}")
    public ResponseEntity<TransactionDetailsDTO> getTransactionByWalletLogId(@PathVariable Integer walletLogId) {
            TransactionDetailsDTO transactionDetails = transactionService.getTransactionByWalletLogId(walletLogId);
            return ResponseEntity.ok(transactionDetails);

    }

    @GetMapping("/cash-out")
    public ResponseEntity<List<GetCashoutTransactionResponse>> cashOut() {
        TransactionTypeEnum type = TransactionTypeEnum.CASH_OUT;
        Account account = accountUtils.getCurrentAccount();
        List<GetCashoutTransactionResponse> getCashoutTransactionResponse = transactionService.getCashoutTransactions(account, type);
        return ResponseEntity.ok(getCashoutTransactionResponse);
    }
}
