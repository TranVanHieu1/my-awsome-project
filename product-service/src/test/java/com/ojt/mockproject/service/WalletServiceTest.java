package com.ojt.mockproject.service;

import com.ojt.mockproject.dto.Wallet.CreateWalletRequest;
import com.ojt.mockproject.dto.Wallet.Responses.CashOutResponse;
import com.ojt.mockproject.dto.Wallet.Responses.GetWalletResponseDTO;
import com.ojt.mockproject.dto.Wallet.UpdateWalletRequest;
import com.ojt.mockproject.dto.Wallet.WalletResponseDTO;
import com.ojt.mockproject.dto.Wallet.WalletValidator;
import com.ojt.mockproject.dto.WalletLog.Requests.WalletLogRequestDTO;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Enum.WalletLogTypeEnum;
import com.ojt.mockproject.entity.Wallet;
import com.ojt.mockproject.entity.WalletLog;
import com.ojt.mockproject.exceptionhandler.ErrorCode;
import com.ojt.mockproject.exceptionhandler.NotFoundException;
import com.ojt.mockproject.exceptionhandler.Wallet.WalletAppException;
import com.ojt.mockproject.repository.AccountRepository;
import com.ojt.mockproject.repository.WalletLogRepository;
import com.ojt.mockproject.repository.WalletRepository;
import com.ojt.mockproject.utils.AccountUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ojt.mockproject.exceptionhandler.Wallet.WalletException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @InjectMocks
    private WalletService walletService;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletLogRepository walletLogRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private WalletValidator walletValidator;

    private Account account;
    private Wallet wallet;

    private List<Wallet> wallets;

    @Mock
    private AccountUtils accountUtils;

    private CreateWalletRequest createWalletRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        account = new Account();
        account.setId(1);
        account.setName("Minh Chau");

        wallet = new Wallet(account, "Bidv", "12345");
        wallet.setId(1);
        wallet.setBalance(BigDecimal.ZERO);

        createWalletRequest = new CreateWalletRequest();
        createWalletRequest.setBankName("Bidv");
        createWalletRequest.setBankAccountNumber("12345");
    }

    @Test
    public void testGetWalletByAccountIdResponse_Success() {
        when(walletRepository.findByAccountId(account.getId())).thenReturn(Optional.of(wallet));

        GetWalletResponseDTO response = walletService.getWalletByAccountIdResponse(account.getId());

        assertNotNull(response);
        assertEquals(1, response.getWalletId());
        assertEquals("Minh Chau", response.getAccountName());
        assertEquals(1, response.getAccountId());
        assertEquals(BigDecimal.ZERO, response.getBalance());
        assertEquals("Bidv", response.getBankName());
        assertEquals("12345", response.getBankAccountNumber());

        // Verify interactions with mock objects
        verify(walletRepository, times(1)).findByAccountId(account.getId());
    }
    @Test
    public void testGetWalletByAccountIdResponse_WalletNotFound() {
        when(walletRepository.findByAccountId(account.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            walletService.getWalletByAccountIdResponse(account.getId());
        });

        assertEquals("Wallet not found for account id: " + account.getId(), exception.getMessage());

        verify(walletRepository, times(1)).findByAccountId(account.getId());
    }

    @Test
    public void testGetWalletById_InternalServerError() {
        when(walletRepository.findById(wallet.getId())).thenThrow(new RuntimeException("Database connection failed"));

        WalletException exception = assertThrows(WalletException.class, () -> {
            walletService.getWalletById(wallet.getId());
        });

        assertEquals("Error retrieving wallet with id: " + wallet.getId(), exception.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());

        verify(walletRepository, times(1)).findById(wallet.getId());
    }

    @Test
    public void testGetAllWallets_InternalServerError() {
        when(walletRepository.findAll()).thenThrow(new RuntimeException("Database connection failed"));

        WalletException exception = assertThrows(WalletException.class, () -> {
            walletService.getAllWallets();
        });

        assertEquals("Error retrieving all wallets!", exception.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());

        verify(walletRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteWallet_Success() {
        // Arrange
        when(walletRepository.findById(anyInt())).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        // Act
        walletService.deleteWallet(1);

        // Assert
        assertTrue(wallet.getIsDeleted());
        verify(walletRepository).findById(1);
        verify(walletRepository).save(wallet);
    }

    @Test
    public void testDeleteWallet_InternalServerError() {
        // Mock repository behavior
        when(walletRepository.findById(wallet.getId())).thenThrow(new RuntimeException("Database connection failed"));

        WalletException exception = assertThrows(WalletException.class, () -> {
            walletService.deleteWallet(wallet.getId());
        });

        String expectedMessage = "Error deleting wallet with id: " + wallet.getId();
        String actualMessage = exception.getMessage();

        assert(actualMessage.contains(expectedMessage));
        assert(exception.getErrorCode() == ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testUpdateWallet_Success() {
        // Arrange
        int walletId = 1;
        UpdateWalletRequest updateWalletRequest = new UpdateWalletRequest();
        updateWalletRequest.setBankName("NewBank");
        updateWalletRequest.setBankAccountNumber("67890");

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(wallet)).thenReturn(wallet);

        // Act
        GetWalletResponseDTO response = walletService.updateWallet(walletId, updateWalletRequest);

        // Assert
        assertEquals(wallet.getId(),  response.getWalletId());
        assertEquals("NewBank", response.getBankName());
        assertEquals("67890", response.getBankAccountNumber());
        assertEquals(wallet.getAccount().getName(), response.getAccountName());
        assertEquals(wallet.getAccount().getId(), response.getAccountId());

        verify(walletRepository).save(wallet);
    }

    @Test
    public void testUpdateWallet_WalletNotFound() {
        // Arrange
        int walletId = 999;
        UpdateWalletRequest updateWalletRequest = new UpdateWalletRequest();
        updateWalletRequest.setBankName("NewBank");
        updateWalletRequest.setBankAccountNumber("67890");

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            walletService.updateWallet(walletId, updateWalletRequest);
        });

        assertEquals("Wallet not found with id: " + walletId, thrown.getMessage());
    }

    @Test
    void testCreateWallet_Success() {
        // Prepare test data
        CreateWalletRequest request = new CreateWalletRequest();
        request.setBankName("Bank A");
        request.setBankAccountNumber("123456789");

        Wallet wallet = new Wallet();
        wallet.setAccount(account);
        wallet.setBankName(request.getBankName());
        wallet.setBankAccountNumber(request.getBankAccountNumber());

        // Mock behavior
        when(accountUtils.getCurrentAccount()).thenReturn(account);
        when(walletValidator.supports(CreateWalletRequest.class)).thenReturn(true);

        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        // Execute the method
        GetWalletResponseDTO response = walletService.createWallet(request);

        // Validate the result
        assertNotNull(response, "Response should not be null");
        assertEquals("Bank A", response.getBankName(), "Bank Name should match");
        assertEquals("123456789", response.getBankAccountNumber(), "Bank Account Number should match");
    }


    @Test
    public void testCreateWallet_ValidationFailed() {
        // Arrange
        CreateWalletRequest createWalletRequest = new CreateWalletRequest();
        createWalletRequest.setBankName("");
        createWalletRequest.setBankAccountNumber("12345");

        Errors errors = new BeanPropertyBindingResult(createWalletRequest, "createWalletRequest");
        errors.rejectValue("bankName", "NotEmpty", "Bank name must not be empty");

        when(walletValidator.supports(CreateWalletRequest.class)).thenReturn(true);
        doAnswer(invocation -> {
            Errors argument = invocation.getArgument(1);
            argument.rejectValue("bankName", "NotEmpty", "Bank name must not be empty");
            return null;
        }).when(walletValidator).validate(any(), any());

        // Act & Assert
        WalletException thrown = assertThrows(WalletException.class, () -> {
            walletService.createWallet(createWalletRequest);
        });

        assertEquals("Validation failed for create wallet request", thrown.getMessage());
    }

    @Test
    public void testGetWalletById_WalletNotFound() {
        // Arrange
        when(walletRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            walletService.getWalletById(1);
        });

        assertEquals("Wallet not found with id: 1", thrown.getMessage());
    }

    @Test
    public void testGetWalletById_WalletDeleted() {
        // Arrange
        wallet.setIsDeleted(true);
        when(walletRepository.findById(anyInt())).thenReturn(Optional.of(wallet));

        // Act & Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            walletService.getWalletById(1);
        });

        assertEquals("Wallet not found with id: 1", thrown.getMessage());
    }

    @Test
    public void testUpdateWalletCurrenAccount_Success() {
        // Given
        UpdateWalletRequest updateWalletRequest = new UpdateWalletRequest();
        updateWalletRequest.setBankName("Vietcombank");
        updateWalletRequest.setBankAccountNumber("67890");

        GetWalletResponseDTO expectedResponse = new GetWalletResponseDTO(
                wallet.getId(),
                account.getName(),
                account.getId(),
                wallet.getBalance(),
                updateWalletRequest.getBankName(),
                updateWalletRequest.getBankAccountNumber()
        );

        // Mocking the behavior
        when(accountUtils.getCurrentAccount()).thenReturn(account);
        when(walletRepository.findByAccountId(account.getId())).thenReturn(Optional.of(wallet));
        when(walletRepository.save(wallet)).thenReturn(wallet);

        // When
        GetWalletResponseDTO response = walletService.updateWalletCurrenAccount(updateWalletRequest);

        // Then
        assertThat(response).isEqualToComparingFieldByField(expectedResponse);
    }

    @Test
    public void testUpdateWalletCurrenAccount_WalletNotFound() {
        // Given
        UpdateWalletRequest updateWalletRequest = new UpdateWalletRequest();
        updateWalletRequest.setBankName("Vietcombank");
        updateWalletRequest.setBankAccountNumber("67890");

        // Mocking the behavior
        when(accountUtils.getCurrentAccount()).thenReturn(account);
        when(walletRepository.findByAccountId(account.getId())).thenReturn(Optional.empty());

        // When & Then
        NotFoundException thrownException = assertThrows(
                NotFoundException.class,
                () -> walletService.updateWalletCurrenAccount(updateWalletRequest),
                "Expected updateWalletCurrenAccount to throw, but it didn't"
        );
        assertEquals("Wallet not found for account id: 1", thrownException.getMessage());
    }

//    @Test
//    public void testGetWalletByCurrentAccount_Success() {
//        // Given
//        BigDecimal totalMoneyInLast30Days = BigDecimal.valueOf(1000.00);
//        BigDecimal totalMoneyOutLast30Days = BigDecimal.valueOf(500.00);
//
//        // Mock current account
//        when(accountUtils.getCurrentAccount()).thenReturn(account);
//
//        // Mock wallet repository response
//        when(walletRepository.findByAccountId(account.getId())).thenReturn(Optional.of(wallet));
//
//        // Mock wallet log repository responses for ADD operations
//        when(walletLogRepository.findByWalletIdAndTypeAndCreateAtAfter(anyInt(), any(), any()))
//                .thenReturn(Arrays.asList(
//                        new WalletLog(1, wallet, null, WalletLogTypeEnum.ADD, BigDecimal.valueOf(500.00), LocalDateTime.now(), false),
//                        new WalletLog(1, wallet, null, WalletLogTypeEnum.ADD, BigDecimal.valueOf(500.00), LocalDateTime.now(), false)
//                ));
//
//        // Mock wallet log repository responses for SUBTRACT operations
//        when(walletLogRepository.findByWalletIdAndTypeAndCreateAtAfter(anyInt(), any(), any()))
//                .thenReturn(Arrays.asList(
//                        new WalletLog(1, wallet, null, WalletLogTypeEnum.SUBTRACT, BigDecimal.valueOf(250.00), LocalDateTime.now(), false),
//                        new WalletLog(1, wallet, null, WalletLogTypeEnum.SUBTRACT, BigDecimal.valueOf(250.00), LocalDateTime.now(), false)
//                ));
//
//        WalletResponseDTO expectedResponse = new WalletResponseDTO(
//                wallet.getId(),
//                wallet.getAccount().getName(),
//                wallet.getAccount().getId(),
//                wallet.getBalance(),
//                wallet.getBankName(),
//                wallet.getBankAccountNumber(),
//                totalMoneyInLast30Days,
//                totalMoneyOutLast30Days
//        );
//
//        // When
//        WalletResponseDTO response = walletService.getWalletByCurrentAccount();
//
//        // Then
//        assertEquals(expectedResponse, response); // Ensure `equals` method is overridden in WalletResponseDTO
//    }

//    @Test
//    public void testGetAllWallets_Success() {
//        // Given
//        BigDecimal totalMoneyInLast30Days = BigDecimal.valueOf(1000.00);
//        BigDecimal totalMoneyOutLast30Days = BigDecimal.valueOf(500.00);
//
//        // Mock wallet repository response
//        when(walletRepository.findAll()).thenReturn(Arrays.asList(wallet));
//
//        // Mock wallet log repository responses
//        when(walletLogRepository.findByWalletIdAndTypeAndCreateAtAfter(anyInt(), any(), any()))
//                .thenReturn(Arrays.asList(
//                        new WalletLog(1, wallet, null, WalletLogTypeEnum.ADD, BigDecimal.valueOf(500.00), LocalDateTime.now(), false),
//                        new WalletLog(1, wallet, null, WalletLogTypeEnum.ADD, BigDecimal.valueOf(500.00), LocalDateTime.now(), false)
//                ));
//        when(walletLogRepository.findByWalletIdAndTypeAndCreateAtAfter(anyInt(), any(), any()))
//                .thenReturn(Arrays.asList(
//                        new WalletLog(1, wallet, null, WalletLogTypeEnum.SUBTRACT, BigDecimal.valueOf(250.00), LocalDateTime.now(), false),
//                        new WalletLog(1, wallet, null, WalletLogTypeEnum.SUBTRACT, BigDecimal.valueOf(250.00), LocalDateTime.now(), false)
//                ));
//
//        WalletResponseDTO expectedResponse = new WalletResponseDTO(
//                wallet.getId(),
//                wallet.getAccount().getName(),
//                wallet.getAccount().getId(),
//                wallet.getBalance(),
//                wallet.getBankName(),
//                wallet.getBankAccountNumber(),
//                totalMoneyInLast30Days,
//                totalMoneyOutLast30Days
//        );
//
//        List<WalletResponseDTO> expectedResponseList = Collections.singletonList(expectedResponse);
//
//        // When
//        List<WalletResponseDTO> responseList = walletService.getAllWallets();
//
//        // Then
//        assertEquals(expectedResponseList, responseList);
//    }

    @Test
    public void testGetAllWallets_Error() {
        // Given
        when(walletRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        WalletException exception = assertThrows(WalletException.class, () -> walletService.getAllWallets());
        assertEquals("Error retrieving all wallets!", exception.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
    }
}
