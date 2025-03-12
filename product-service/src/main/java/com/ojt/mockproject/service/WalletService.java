package com.ojt.mockproject.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ojt.mockproject.dto.Wallet.CreateWalletRequest;
import com.ojt.mockproject.dto.Wallet.Responses.*;
import com.ojt.mockproject.dto.Wallet.UpdateWalletRequest;
import com.ojt.mockproject.dto.Wallet.WalletResponseDTO;
import com.ojt.mockproject.dto.Wallet.WalletValidator;
import com.ojt.mockproject.dto.WalletLog.Requests.WalletLogRequestDTO;
import com.ojt.mockproject.email.EmailService;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Enum.*;
import com.ojt.mockproject.entity.Transaction;
import com.ojt.mockproject.entity.Wallet;
import com.ojt.mockproject.entity.WalletLog;
import com.ojt.mockproject.exceptionhandler.ErrorCode;
import com.ojt.mockproject.exceptionhandler.NotFoundException;
import com.ojt.mockproject.exceptionhandler.Wallet.WalletAppException;
import com.ojt.mockproject.exceptionhandler.Wallet.WalletException;
import com.ojt.mockproject.exceptionhandler.account.AccountException;
import com.ojt.mockproject.repository.TransactionRepository;
import com.ojt.mockproject.repository.WalletLogRepository;
import com.ojt.mockproject.repository.WalletRepository;
import com.ojt.mockproject.repository.AccountRepository;
import com.ojt.mockproject.utils.AccountUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletLogService walletLogService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountUtils accountUtils;

    private final KafkaTemplate<String, CashOutResponse.DataResponse> kafkaTemplate;
    private final KafkaTemplate<String, CashoutAdminResponse> cashoutAdminKafkaTemplate;
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    public WalletService(KafkaTemplate<String, CashOutResponse.DataResponse> kafkaTemplate,
                         KafkaTemplate<String, CashoutAdminResponse> cashoutAdminKafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.cashoutAdminKafkaTemplate = cashoutAdminKafkaTemplate;
    }
    @Autowired
    private EmailService emailService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JWTService jwtService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private WalletLogRepository walletLogRepository;

    @Autowired
    private TransactionService transactionService;
    //Create Wallet
    @Autowired
    private WalletValidator walletValidator;
    // create wallet
//    @Transactional
//    public GetWalletResponseDTO createWallet(CreateWalletRequest createWalletRequest) {
//        Errors errors = new BeanPropertyBindingResult(createWalletRequest, "createWalletRequest");
//         ValidationUtils.invokeValidator(walletValidator, createWalletRequest, errors);
//
//         if (errors.hasErrors()) {
//             throw new WalletException("Validation failed for create wallet request", ErrorCode.INTERNAL_SERVER_ERROR);
//         }
//
//         Account account = accountRepository.findById(createWalletRequest.getAccountId())
//            .orElseThrow(() -> new NotFoundException("Account not found with id: " + createWalletRequest.getAccountId()));
//
//         Wallet wallet = new Wallet(account, createWalletRequest.getBankName(), createWalletRequest.getBankAccountNumber());
//         walletRepository.save(wallet);
//
//         return new GetWalletResponseDTO(
//            wallet.getId(),
//            wallet.getAccount().getName(),
//            wallet.getAccount().getId(),
//            wallet.getBalance(),
//            wallet.getBankName(),
//            wallet.getBankAccountNumber()
//         );
//    }

    @Transactional
    public GetWalletResponseDTO createWallet(CreateWalletRequest createWalletRequest) {
        // Validate the createWalletRequest
        Errors errors = new BeanPropertyBindingResult(createWalletRequest, "createWalletRequest");
        ValidationUtils.invokeValidator(walletValidator, createWalletRequest, errors);

        if (errors.hasErrors()) {
            throw new WalletException("Validation failed for create wallet request", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        Account account = accountUtils.getCurrentAccount();
        Wallet wallet = new Wallet(account, createWalletRequest.getBankName(), createWalletRequest.getBankAccountNumber());
        walletRepository.save(wallet);

        return new GetWalletResponseDTO(
                wallet.getId(),
                wallet.getAccount().getName(),
                wallet.getAccount().getId(),
                wallet.getBalance(),
                wallet.getBankName(),
                wallet.getBankAccountNumber()
        );
    }


    //get wallet by account id

    @Transactional
    public GetWalletResponseDTO getWalletByAccountIdResponse(Integer accountId) {
        Wallet wallet = getWalletByAccountId(accountId);
        return new GetWalletResponseDTO(
                wallet.getId(),
                wallet.getAccount().getName(),
                wallet.getAccount().getId(),
                wallet.getBalance(),
                wallet.getBankName(),
                wallet.getBankAccountNumber()
        );
    }

    public Wallet getWalletByAccountId(Integer accountId) {
        return walletRepository.findByAccountId(accountId)
                .orElseThrow(() -> new NotFoundException("Wallet not found for account id: " + accountId));
    }
// update wallet (update banknumber and bank name) by walletID
    @Transactional
    public GetWalletResponseDTO updateWallet(Integer walletId, @Valid UpdateWalletRequest updateWalletRequest) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new NotFoundException("Wallet not found with id: " + walletId));

        wallet.setBankName(updateWalletRequest.getBankName());
        wallet.setBankAccountNumber(updateWalletRequest.getBankAccountNumber());

        wallet = walletRepository.save(wallet);
        return new GetWalletResponseDTO(
                wallet.getId(),
                wallet.getAccount().getName(),
                wallet.getAccount().getId(),
                wallet.getBalance(),
                wallet.getBankName(),
                wallet.getBankAccountNumber()
        );
    }


    //Update wallet by current account
    @Transactional
    public GetWalletResponseDTO updateWalletCurrenAccount(@Valid UpdateWalletRequest updateWalletRequest) {
        // Retrieve the currently logged-in account
        Account account = accountUtils.getCurrentAccount();
        Integer accountId = account.getId(); // Get the account ID

        // Retrieve the wallet associated with the current account
        Wallet wallet = walletRepository.findByAccountId(accountId)
                .orElseThrow(() -> new NotFoundException("Wallet not found for account id: " + accountId));

        // Update the wallet details
        wallet.setBankName(updateWalletRequest.getBankName());
        wallet.setBankAccountNumber(updateWalletRequest.getBankAccountNumber());

        // Save the updated wallet
        wallet = walletRepository.save(wallet);

        // Return the updated wallet information
        return new GetWalletResponseDTO(
                wallet.getId(),
                wallet.getAccount().getName(),
                wallet.getAccount().getId(),
                wallet.getBalance(),
                wallet.getBankName(),
                wallet.getBankAccountNumber()
        );
    }

    //Delete Wallet by ID
    @Transactional
    public String deleteWallet(Integer id) {
        try {
            Wallet wallet = walletRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Wallet not found with id: " + id));
            wallet.setIsDeleted(true);
            walletRepository.save(wallet);
            return "Wallet with id: " + id + " has been successfully deleted.";
        } catch (Exception e) {
            throw new WalletException("Error deleting wallet with id: " + id, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<AddPriceWalletResponse> addPriceToWallet(Integer accountId, BigDecimal price) {
        try {
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new WalletAppException(ErrorCode.PRICE_INVALID);
            }
            Wallet wallet = getWalletByAccountId(accountId);
            BigDecimal newBalance = wallet.getBalance().add(price);
            wallet.setBalance(newBalance);

            // Save the updated wallet
            Wallet updatedWallet = walletRepository.save(wallet);

            // Prepare the response
            String messageResponse = "Add price to wallet successfully!";
            AddPriceWalletResponse.DataResponse walletResponse = new AddPriceWalletResponse.DataResponse(
                    updatedWallet.getId(),
                    updatedWallet.getBalance(),
                    price,
                    null,
                    null,
                    null
            );

            AddPriceWalletResponse addPriceWalletResponse = new AddPriceWalletResponse(messageResponse, null, 200, walletResponse);

            return ResponseEntity.ok(addPriceWalletResponse);
        } catch (NotFoundException e) {
            throw new WalletException("Error adding price to wallet", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<CashOutResponse> cashOut(Integer accountId, BigDecimal cashoutBalance) {
        try {
            if (cashoutBalance.compareTo(BigDecimal.ZERO) <= 0) {
                throw new WalletAppException(ErrorCode.PRICE_INVALID);
            }
            // get account id
            Wallet walletId = getWalletByAccountId(accountId);
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountException("Account not found with id: " + accountId, ErrorCode.USER_NOT_FOUND));


            // get current balance
            BigDecimal balance = walletId.getBalance();

            if (balance.compareTo(cashoutBalance) < 0) {
                throw new WalletAppException(ErrorCode.BALANCE_INVALID);
            }
            //Create Transaction
            Transaction transaction = new Transaction();
            transaction.setAccount(account);
            transaction.setTotalPrice(cashoutBalance);
            transaction.setPaymentMethod(PaymentMethodEnum.BANKING);
            transaction.setStatus(TransactionStatusEnum.SUCCESS);
            transaction.setType(TransactionTypeEnum.CASH_OUT);
            transaction.setOrderr(null);
            transaction.setCreateAt(LocalDateTime.now());
            transaction.setIsDeleted(false);
            Transaction transactionResponse = transactionRepository.save(transaction);

            // Create wallet log
            WalletLogRequestDTO walletLog = createWalletLog(walletId, cashoutBalance,transactionResponse.getId(), WalletLogTypeEnum.SUBTRACT );

            // handle cashout || subtract
            walletId.setBalance(walletId.getBalance().subtract(cashoutBalance));

            // save wallet
            walletRepository.save(walletId);

            // Prepare response data
            CashOutResponse.DataResponse dataResponse = createDataResponse(walletId, cashoutBalance, balance, walletLog);

            // Send Kafka messages asynchronously
            sendKafkaMessages(dataResponse);

            String messageResponse = "Cashout" + " -" + cashoutBalance + " successfully!" + " Please wait for admin to process your request.";
            CashOutResponse cashOutResponse = new CashOutResponse(messageResponse, null, 200, dataResponse);
            return ResponseEntity.ok(cashOutResponse);
        }
        catch (WalletAppException e) {
            ErrorCode errorCode = e.getErrorCode();
            CashOutResponse cashOutResponse = new CashOutResponse("Cashout failed", e.getMessage(), errorCode.getCode(), null);
            return new ResponseEntity<>(cashOutResponse, errorCode.getHttpStatus());
        }
    }
    @Transactional
    public ResponseEntity<BuyCourseResponse> buyCourseByWallet(Integer accountId, String courseId, BigDecimal price) {
        try {
            Wallet wallet = getWalletByAccountId(accountId);
            if (wallet.getId() == null) {
                throw new WalletAppException(ErrorCode.WALLET_NOT_FOUND);
            }
            BigDecimal balance = wallet.getBalance();

            if(balance.compareTo(price) < 0) {
                throw new WalletAppException(ErrorCode.BALANCE_NOT_ENOUGH);
            }
            wallet.setBalance(balance.subtract(price));

            UpdateWalletRequest updateWalletRequest = new UpdateWalletRequest(wallet.getBalance(), wallet.getBankName(), wallet.getBankAccountNumber());
            updateWallet(wallet.getId(), updateWalletRequest);
            walletRepository.save(wallet);

            System.out.println(getWalletById(wallet.getId()).getBalance());

            BuyCourseResponse buyCourseResponse = new BuyCourseResponse("Course purchased successfully!", null,courseId, price, wallet.getBalance());
            return ResponseEntity.ok(buyCourseResponse);
        } catch (WalletAppException e) {
            ErrorCode errorCode = e.getErrorCode();
            BuyCourseResponse buyCourseResponse = new BuyCourseResponse("Course purchase failed", errorCode.getMessage(), courseId, price, null);
            return new ResponseEntity<>(buyCourseResponse, errorCode.getHttpStatus().BAD_REQUEST);
        }
    }
    @Transactional
    public ResponseEntity<AddPriceWalletResponse> handleReturnAddBalance(Account account, String email, BigDecimal totalPrice, String code, boolean cancel, String status, String orderCode) {
        try {
            if (account.getEmail() == null || account.getEmail().isEmpty() || !account.getEmail().equals(email)) {
                throw new WalletAppException(ErrorCode.ACCOUNT_NOT_MATCH);
            }
            // Convert to enum
            ResponseEntity<AddPriceWalletResponse> addPriceWalletResponse = null;
            if ("PAID".equals(status)) {
                addPriceWalletResponse = addPriceToWallet(account.getId(), totalPrice);
                return addPriceWalletResponse;
            } else if("CANCELLED".equals(status)) {
                throw new WalletAppException(ErrorCode.CANCELLED);
            }
            AddPriceWalletResponse response = new AddPriceWalletResponse("Add balance to wallet successfully!", null, 200, addPriceWalletResponse.getBody().getData());
            return ResponseEntity.ok(response);
        } catch (WalletAppException e) {
            ErrorCode errorCode = e.getErrorCode();
            AddPriceWalletResponse response = new AddPriceWalletResponse("Cashout failed", e.getMessage(), errorCode.getCode(), null);
            return new ResponseEntity<>(response, errorCode.getHttpStatus());
        }
    }
    public WalletLogRequestDTO createWalletLog(Wallet wallet, BigDecimal price,Integer transactionId, WalletLogTypeEnum walletLogTypeEnum) {
        WalletLogRequestDTO walletLog = new WalletLogRequestDTO();
        walletLog.setWalletId(wallet.getId());
        walletLog.setTransactionId(transactionId);
        walletLog.setType(walletLogTypeEnum);
        walletLog.setAmount(price);
        walletLog.setIsDeleted(false);
        walletLogService.save(walletLog);
        return walletLog;
    }
    private CashOutResponse.DataResponse createDataResponse(Wallet wallet, BigDecimal cashoutBalance, BigDecimal initialBalance, WalletLogRequestDTO walletLog) {
        return new CashOutResponse.DataResponse(
                wallet.getId(), wallet.getBalance(), cashoutBalance,
                initialBalance, wallet.getBankName(), wallet.getBankAccountNumber(),
                walletLog, wallet.getAccount().getName(), wallet.getAccount().getEmail()
        );
    }
    private List<String> getEmailsOfAllAdminAccounts() {
        List<Account> adminAccounts = accountRepository.findByRole(AccountRoleEnum.ADMIN);
        return adminAccounts.stream()
                .map(Account::getEmail)
                .toList();
    }
    private void sendKafkaMessages(CashOutResponse.DataResponse dataResponse) {
        List<String> emailAdmin = getEmailsOfAllAdminAccounts();
        CashoutAdminResponse cashoutAdminResponse = new CashoutAdminResponse(dataResponse, emailAdmin.toString());
        String topicCashoutEmail = "cashout-message-topic-email";
        String topicCashoutAdminEmail = "cashout-message-admin-topic-email";
        kafkaTemplate.send( topicCashoutEmail, dataResponse);
        cashoutAdminKafkaTemplate.send(topicCashoutAdminEmail, cashoutAdminResponse);

        System.out.println("Kafka send topic 1: " + topicCashoutEmail);
        System.out.println("Kafka send topic 2: " + topicCashoutAdminEmail);
    }
    private BigDecimal calculateTotalMoneyInLast30Days(Integer walletId) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return walletLogRepository.findByWalletIdAndTypeAndCreateAtAfter(walletId, WalletLogTypeEnum.ADD, thirtyDaysAgo)
                .stream()
                .map(WalletLog::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    private BigDecimal calculateTotalMoneyOutLast30Days(Integer walletId) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return walletLogRepository.findByWalletIdAndTypeAndCreateAtAfter(walletId, WalletLogTypeEnum.SUBTRACT, thirtyDaysAgo)
                .stream()
                .map(WalletLog::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public WalletResponseDTO getWalletById(Integer id) {
        try {
            Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Wallet not found with id: " + id));

            if (wallet.getIsDeleted()) {
                throw new NotFoundException("Wallet not found with id: " + id);
            }

            BigDecimal totalMoneyInLast30Days = calculateTotalMoneyInLast30Days(wallet.getId());
            BigDecimal totalMoneyOutLast30Days = calculateTotalMoneyOutLast30Days(wallet.getId());

            return new WalletResponseDTO(wallet.getId(), wallet.getAccount().getName(), wallet.getAccount().getId(),
                    wallet.getBalance(), wallet.getBankName(), wallet.getBankAccountNumber(),
                    totalMoneyInLast30Days, totalMoneyOutLast30Days);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new WalletException("Error retrieving wallet with id: " + id, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public WalletResponseDTO getWalletByCurrentAccount() {
        try {
            Account account = accountUtils.getCurrentAccount();
            Wallet wallet = walletRepository.findByAccountId(account.getId())
                    .orElseThrow(() -> new NotFoundException("Wallet not found for account id: " + account.getId()));

            if (wallet.getIsDeleted()) {
                throw new NotFoundException("Wallet not found for account id: " + account.getId());
            }

            BigDecimal totalMoneyInLast30Days = calculateTotalMoneyInLast30Days(wallet.getId());
            BigDecimal totalMoneyOutLast30Days = calculateTotalMoneyOutLast30Days(wallet.getId());

            return new WalletResponseDTO(wallet.getId(), wallet.getAccount().getName(), wallet.getAccount().getId(),
                    wallet.getBalance(), wallet.getBankName(), wallet.getBankAccountNumber(),
                    totalMoneyInLast30Days, totalMoneyOutLast30Days);
        }  catch (Exception e) {
            throw new WalletException("Error retrieving wallets!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public List<WalletResponseDTO> getAllWallets() {
        try {
            return walletRepository.findAll().stream()
                    .filter(wallet -> !wallet.getIsDeleted())
                    .map(wallet -> {
                        BigDecimal totalMoneyInLast30Days = calculateTotalMoneyInLast30Days(wallet.getId());
                        BigDecimal totalMoneyOutLast30Days = calculateTotalMoneyOutLast30Days(wallet.getId());

                        return new WalletResponseDTO(wallet.getId(), wallet.getAccount().getName(),
                                wallet.getAccount().getId(), wallet.getBalance(), wallet.getBankName(),
                                wallet.getBankAccountNumber(), totalMoneyInLast30Days, totalMoneyOutLast30Days);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new WalletException("Error retrieving all wallets!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}