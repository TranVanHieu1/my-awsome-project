package com.ojt.mockproject.service;

import com.ojt.mockproject.dto.WalletLog.Requests.WalletLogRequestDTO;
import com.ojt.mockproject.dto.WalletLog.Response.WalletLogAccountResponseDTO;
import com.ojt.mockproject.dto.WalletLog.Response.WalletLogResponseDTO;
import com.ojt.mockproject.entity.Enum.WalletLogTypeEnum;
import com.ojt.mockproject.entity.Transaction;
import com.ojt.mockproject.entity.Wallet;
import com.ojt.mockproject.entity.WalletLog;
import com.ojt.mockproject.exceptionhandler.ErrorCode;
import com.ojt.mockproject.exceptionhandler.ValidationException;
import com.ojt.mockproject.exceptionhandler.Wallet.WalletException;
import com.ojt.mockproject.exceptionhandler.Wallet.WalletLogException;
import com.ojt.mockproject.exceptionhandler.course.CourseException;
import com.ojt.mockproject.repository.TransactionRepository;
import com.ojt.mockproject.repository.WalletLogRepository;
import com.ojt.mockproject.repository.WalletRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class WalletLogServiceTest {

    @Mock
    private WalletLogRepository walletLogRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private WalletLogService walletLogService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAll() {
        // Given
        List<WalletLog> mockWalletLogs = new ArrayList<>();
        mockWalletLogs.add(createMockWalletLog(1, WalletLogTypeEnum.ADD, new BigDecimal("100.00")));
        mockWalletLogs.add(createMockWalletLog(2, WalletLogTypeEnum.SUBTRACT, new BigDecimal("50.00")));

        when(walletLogRepository.findAll()).thenReturn(mockWalletLogs);

        // When
        List<WalletLogResponseDTO> result = walletLogService.findAll();

        // Then
        assertEquals(2, result.size());
        assertEquals(WalletLogTypeEnum.ADD, result.get(0).getType());
        assertEquals(WalletLogTypeEnum.SUBTRACT, result.get(1).getType());

        // Kiểm tra walletId của WalletLogResponseDTO khi wallet là null
        Assertions.assertNull(result.get(1).getWalletId());
    }

    @Test
    public void testFindAll_Exception() {
        // Mock behavior to throw an exception
        when(walletLogRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When
        Exception exception = assertThrows(WalletLogException.class, () -> walletLogService.findAll());

        // Then
        assertEquals("Failed to retrieve wallet log", exception.getMessage());

        // Verify repository method called
        verify(walletLogRepository).findAll();
    }

    @Test
    public void testGetWalletLogById_Success() {
        // Given
        Integer walletLogId = 1;
        WalletLog walletLog = createMockWalletLog(walletLogId, WalletLogTypeEnum.ADD, new BigDecimal("100.00"));
        when(walletLogRepository.findById(walletLogId)).thenReturn(Optional.of(walletLog));

        // When
        WalletLogResponseDTO result = walletLogService.getWalletLogById(walletLogId);

        // Then
        assertNotNull(result);
        assertEquals(walletLogId, result.getId());
        assertEquals(WalletLogTypeEnum.ADD, result.getType());
        assertEquals(new BigDecimal("100.00"), result.getAmount());

        // Verify interactions
        verify(walletLogRepository, times(1)).findById(walletLogId);
    }

    @Test
    public void testGetWalletLogById_NotFound() {
        // Given
        Integer walletLogId = 1;
        when(walletLogRepository.findById(walletLogId)).thenReturn(Optional.empty());

        // When
        WalletLogException exception = assertThrows(WalletLogException.class, () -> walletLogService.getWalletLogById(walletLogId));

        // Then
        assertEquals("WalletLog not found with id: " + walletLogId, exception.getMessage());
        assertEquals(ErrorCode.WALLET_LOG_NOT_FOUND, exception.getErrorCode());

        // Verify interactions
        verify(walletLogRepository, times(1)).findById(walletLogId);
    }

    @Test
    public void testGetWalletLogById_Exception() {
        // Given
        Integer walletLogId = 1;
        when(walletLogRepository.findById(walletLogId)).thenThrow(new RuntimeException("Database error"));

        // When
        WalletLogException exception = assertThrows(WalletLogException.class, () -> walletLogService.getWalletLogById(walletLogId));

        // Then
        assertEquals("Failed to retrieve WalletLog with id 1", exception.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());

        // Verify interactions
        verify(walletLogRepository, times(1)).findById(walletLogId);
    }

    @Test
    public void testGetWalletLogByWalletId_Success() {
        // Given
        Integer walletId = 1;
        List<WalletLog> mockWalletLogs = new ArrayList<>();
        mockWalletLogs.add(createMockWalletLog(1, WalletLogTypeEnum.ADD, new BigDecimal("100.00")));
        mockWalletLogs.add(createMockWalletLog(2, WalletLogTypeEnum.SUBTRACT, new BigDecimal("50.00")));

        when(walletLogRepository.findByWalletIdAndIsDeleted(walletId,false)).thenReturn(mockWalletLogs);

        // When
        List<WalletLogResponseDTO> result = walletLogService.getWalletLogByWalletId(walletId);

        // Then
        assertEquals(mockWalletLogs.size(), result.size());
//        assertEquals(createMockWalletLog(1, WalletLogTypeEnum.ADD, new BigDecimal("100.00")).getWallet(), result.get(0).getWalletId());
//        assertEquals(createMockWalletLog(2, WalletLogTypeEnum.SUBTRACT, new BigDecimal("50.00")).getWallet(), result.get(1).getWalletId());

        // Verify interactions
        verify(walletLogRepository, times(1)).findByWalletIdAndIsDeleted(walletId,false);
    }


    @Test
    public void testGetWalletLogByWalletId_WalletNotFound() {
        // Given
        Integer walletId = 1;
        when(walletLogRepository.findByWalletIdAndIsDeleted(walletId,false)).thenReturn(new ArrayList<>());

        // When
        WalletException exception = assertThrows(WalletException.class, () -> walletLogService.getWalletLogByWalletId(walletId));

        // Then
        assertEquals("Wallet not found with id: " + walletId, exception.getMessage());
        assertEquals(ErrorCode.WALLET_NOT_FOUND, exception.getErrorCode());

        // Verify interactions
        verify(walletLogRepository, times(1)).findByWalletIdAndIsDeleted(walletId,false);
    }

    @Test
    public void testGetWalletLogByWalletId_Exception() {
        // Given
        Integer walletId = 1;
        when(walletLogRepository.findByWalletIdAndIsDeleted(walletId,false)).thenThrow(new RuntimeException("Database error"));

        // When
        CourseException exception = assertThrows(CourseException.class, () -> walletLogService.getWalletLogByWalletId(walletId));

        // Then
        assertEquals("Failed to retrieve WalletLog with Wallet id " + walletId, exception.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());

        // Verify interactions
        verify(walletLogRepository, times(1)).findByWalletIdAndIsDeleted(walletId,false);
    }

    @Test
    public void testGetAllWalletLogsByAccountId_Success() {
        // Given
        Integer accountId = 1;
        List<WalletLogAccountResponseDTO> walletLogs = Arrays.asList(
                new WalletLogAccountResponseDTO(1, 1,1, 1,WalletLogTypeEnum.ADD, new BigDecimal("100.00"), LocalDateTime.now(), false),
                new WalletLogAccountResponseDTO(2, 1,1, 1,WalletLogTypeEnum.SUBTRACT, new BigDecimal("50.00"), LocalDateTime.now(), false)
        );
        when(walletLogRepository.findAllByAccountId1(accountId)).thenReturn(walletLogs);

        // When
        List<WalletLogAccountResponseDTO> result = walletLogService.getAllWalletLogsByAccountId(accountId);

        // Then
        verify(walletLogRepository, times(1)).findAllByAccountId1(accountId);
        assertEquals(walletLogs, result);
    }

    @Test
    public void testGetAllWalletLogsByAccountId_AccountNotFound() {
        // Given
        Integer accountId = 1;
        when(walletLogRepository.findAllByAccountId1(accountId)).thenReturn(new ArrayList<>());

        // When & Then
        WalletException exception = assertThrows(WalletException.class, () -> walletLogService.getAllWalletLogsByAccountId(accountId));

        assertEquals("Wallet logs not found for Account id: " + accountId, exception.getMessage());
        verify(walletLogRepository, times(1)).findAllByAccountId1(accountId);
    }

    @Test
    public void testGetAllWalletLogsByAccountId_Exception() {
        // Given
        Integer accountId = 1;
        when(walletLogRepository.findAllByAccountId1(accountId)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        WalletLogException exception = assertThrows(WalletLogException.class, () -> walletLogService.getAllWalletLogsByAccountId(accountId));

        assertEquals("Failed to retrieve Wallet logs with Account id " + accountId, exception.getMessage());
        verify(walletLogRepository, times(1)).findAllByAccountId1(accountId);
    }

    @Test
    public void testCreate_Success() {
        // Given
        WalletLogRequestDTO requestDTO = new WalletLogRequestDTO();
        requestDTO.setWalletId(1);
        requestDTO.setTransactionId(1);
        requestDTO.setType(WalletLogTypeEnum.ADD);
        requestDTO.setAmount(new BigDecimal("100.00"));
        requestDTO.setIsDeleted(false);

        Wallet wallet = new Wallet();
        wallet.setId(1);
        Transaction transaction = new Transaction();
        transaction.setId(1);

        when(walletRepository.findById(1)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findById(1)).thenReturn(Optional.of(transaction));

        WalletLog savedWalletLog = new WalletLog();
        savedWalletLog.setId(1);
        savedWalletLog.setWallet(wallet);
        savedWalletLog.setTransaction(transaction);
        savedWalletLog.setType(requestDTO.getType());
        savedWalletLog.setAmount(requestDTO.getAmount());
        savedWalletLog.setCreateAt(LocalDateTime.now());
        savedWalletLog.setIsDeleted(requestDTO.getIsDeleted());

        when(walletLogRepository.save(any(WalletLog.class))).thenReturn(savedWalletLog);

        // When
        WalletLogResponseDTO result = walletLogService.save(requestDTO);

        // Then
        assertEquals(1, result.getId());
        assertEquals(WalletLogTypeEnum.ADD, result.getType());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
    }

    @Test
    public void testCreate_InvalidAmount() {
        // Given
        WalletLogRequestDTO requestDTO = new WalletLogRequestDTO();
        requestDTO.setWalletId(1);
        requestDTO.setTransactionId(1);
        requestDTO.setType(WalletLogTypeEnum.ADD);
        requestDTO.setAmount(new BigDecimal("-10.00"));
        requestDTO.setIsDeleted(false);

        Wallet wallet = new Wallet();
        wallet.setId(1);
        Transaction transaction = new Transaction();
        transaction.setId(1);

        when(walletRepository.findById(1)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findById(1)).thenReturn(Optional.of(transaction));

        WalletLog savedWalletLog = new WalletLog();
        savedWalletLog.setId(1);
        savedWalletLog.setWallet(wallet);
        savedWalletLog.setTransaction(transaction);
        savedWalletLog.setType(requestDTO.getType());
        savedWalletLog.setAmount(requestDTO.getAmount());
        savedWalletLog.setCreateAt(LocalDateTime.now());
        savedWalletLog.setIsDeleted(requestDTO.getIsDeleted());
        // When
        ValidationException exception = assertThrows(ValidationException.class, () -> walletLogService.save(requestDTO));

        // Then
        assertEquals("Invalid price: -10.00", exception.getMessage());
        assertEquals(ErrorCode.INVALID_INPUT, exception.getErrorCode());

        // Verify no interaction with walletLogRepository.save
        verify(walletLogRepository, never()).save(any(WalletLog.class));
    }

    @Test
    public void testCreate_WalletIdNotFound() {
        // Given
        WalletLogRequestDTO requestDTO = new WalletLogRequestDTO();
        requestDTO.setWalletId(1);
        requestDTO.setTransactionId(1);
        requestDTO.setType(WalletLogTypeEnum.ADD);
        requestDTO.setAmount(new BigDecimal("100.00"));
        requestDTO.setIsDeleted(false);

        Wallet wallet = new Wallet();
        wallet.setId(1);
        Transaction transaction = new Transaction();
        transaction.setId(1);

        when(transactionRepository.findById(1)).thenReturn(Optional.of(transaction));
        when(walletRepository.findById(2)).thenReturn(Optional.of(wallet));

        // When
        WalletException exception = assertThrows(WalletException.class, () -> walletLogService.save(requestDTO));

        // Then
        assertEquals("Failed to create wallet log due to WalletException: Invalid walletId: 1", exception.getMessage());
        assertEquals(ErrorCode.WALLET_NOT_FOUND, exception.getErrorCode());

        // Verify no interaction with walletLogRepository.save
        verify(walletLogRepository, never()).save(any(WalletLog.class));
    }

    @Test
    public void testCreate_Exception() {
        // Tạo dữ liệu mẫu cho request
        WalletLogRequestDTO requestDTO = new WalletLogRequestDTO();
        requestDTO.setWalletId(1);
        requestDTO.setTransactionId(1);
        requestDTO.setType(WalletLogTypeEnum.ADD);
        requestDTO.setAmount(new BigDecimal("100.00"));
        requestDTO.setIsDeleted(false);

        // Tạo dữ liệu mẫu cho wallet
        Wallet wallet = new Wallet();
        wallet.setId(1);
        wallet.setBalance(new BigDecimal("1000.00"));

        Transaction transaction = new Transaction();
        transaction.setId(1);

        // Mock behavior
        when(walletRepository.findById(1)).thenReturn(Optional.of(wallet));
        when(walletLogRepository.save(any(WalletLog.class))).thenThrow(new RuntimeException("Database error"));
        when(transactionRepository.findById(1)).thenReturn(Optional.of(transaction));

        // Gọi phương thức service và kiểm tra ngoại lệ
        WalletLogException exception = assertThrows(WalletLogException.class, () -> walletLogService.save(requestDTO));

        // Xác minh kết quả
        assertEquals("Failed to create wallet log", exception.getMessage());

        // Xác minh tương tác với mock
        verify(walletRepository).findById(1);
        verify(walletLogRepository).save(any(WalletLog.class));
    }

    @Test
    public void testUpdate_Success() {
        // Given
        Integer walletLogId = 1;
        WalletLogRequestDTO requestDTO = new WalletLogRequestDTO();
        requestDTO.setWalletId(1);
        requestDTO.setType(WalletLogTypeEnum.SUBTRACT);
        requestDTO.setAmount(new BigDecimal("50.00"));
        requestDTO.setIsDeleted(false);

        WalletLog existingWalletLog = createMockWalletLog(walletLogId, WalletLogTypeEnum.ADD, new BigDecimal("100.00"));
        Wallet updatedWallet = new Wallet();
        updatedWallet.setId(1);

        when(walletLogRepository.findById(walletLogId)).thenReturn(Optional.of(existingWalletLog));
        when(walletRepository.findById(requestDTO.getWalletId())).thenReturn(Optional.of(updatedWallet));

        existingWalletLog.setWallet(updatedWallet);
        existingWalletLog.setType(requestDTO.getType());
        existingWalletLog.setAmount(requestDTO.getAmount());
        existingWalletLog.setCreateAt(LocalDateTime.now());
        existingWalletLog.setIsDeleted(requestDTO.getIsDeleted());

        when(walletLogRepository.save(existingWalletLog)).thenReturn(existingWalletLog);

        // When
        WalletLogResponseDTO result = walletLogService.update(walletLogId, requestDTO);

        // Then
        assertEquals(walletLogId, result.getId());
        assertEquals(WalletLogTypeEnum.SUBTRACT, result.getType());
        assertEquals(new BigDecimal("50.00"), result.getAmount());
    }

    @Test
    public void testUpdate_WalletLogIdNotFound() {
        // Given
        Integer walletLogId = 1;
        WalletLogRequestDTO requestDTO = new WalletLogRequestDTO();
        requestDTO.setWalletId(1);
        requestDTO.setType(WalletLogTypeEnum.SUBTRACT);
        requestDTO.setAmount(new BigDecimal("50.00"));
        requestDTO.setIsDeleted(false);

        when(walletLogRepository.findById(walletLogId)).thenReturn(Optional.empty());

        // When
        WalletLogException exception = assertThrows(WalletLogException.class, () -> walletLogService.update(walletLogId, requestDTO));

        // Then
        assertEquals("Invalid walletLogId: 1", exception.getMessage());
        assertEquals(ErrorCode.WALLET_LOG_NOT_FOUND, exception.getErrorCode());

        // Verify that the repository's save method was never called
        verify(walletLogRepository, never()).save(any(WalletLog.class));
    }

    @Test
    public void testUpdate_InvalidAmount() {
        // Given
        Integer walletLogId = 1;
        WalletLogRequestDTO requestDTO = new WalletLogRequestDTO();
        requestDTO.setWalletId(1);
        requestDTO.setType(WalletLogTypeEnum.SUBTRACT);
        requestDTO.setAmount(new BigDecimal("-50.00"));
        requestDTO.setIsDeleted(false);

        WalletLog existingWalletLog = createMockWalletLog(walletLogId, WalletLogTypeEnum.ADD, new BigDecimal("100.00"));
        Wallet updatedWallet = new Wallet();
        updatedWallet.setId(1);

        when(walletLogRepository.findById(walletLogId)).thenReturn(Optional.of(existingWalletLog));
        when(walletRepository.findById(requestDTO.getWalletId())).thenReturn(Optional.of(updatedWallet));

        // When
        ValidationException exception = assertThrows(ValidationException.class, () -> walletLogService.update(walletLogId, requestDTO));

        // Then
        assertEquals("Invalid price: -50.00", exception.getMessage());
        assertEquals(ErrorCode.INVALID_INPUT, exception.getErrorCode());

        // Verify that the repository's save method was never called
        verify(walletLogRepository, never()).save(any(WalletLog.class));
    }

    @Test
    public void testUpdate_Exception() {
        // Tạo dữ liệu mẫu cho request
        WalletLogRequestDTO requestDTO = new WalletLogRequestDTO();
        requestDTO.setWalletId(1);
        requestDTO.setType(WalletLogTypeEnum.ADD);
        requestDTO.setAmount(new BigDecimal("100.00"));
        requestDTO.setIsDeleted(false);

        // Tạo dữ liệu mẫu cho wallet log
        WalletLog walletLog = new WalletLog();
        walletLog.setId(1);

        // Mock behavior
        when(walletLogRepository.findById(1)).thenReturn(Optional.of(walletLog));
        when(walletRepository.findById(1)).thenReturn(Optional.of(new Wallet()));
        when(walletLogRepository.save(any(WalletLog.class))).thenThrow(new RuntimeException("Database error"));

        // Gọi phương thức service và kiểm tra ngoại lệ
        WalletLogException exception = assertThrows(WalletLogException.class, () -> walletLogService.update(1, requestDTO));

        // Xác minh kết quả
        assertEquals("Failed to update wallet log", exception.getMessage());

        // Xác minh tương tác với mock
        verify(walletLogRepository).findById(1);
        verify(walletRepository).findById(1);
        verify(walletLogRepository).save(any(WalletLog.class));
    }

    @Test
    public void testDeleteById_Success() {
        // Given
        Integer walletLogId = 1;
        WalletLog existingWalletLog = createMockWalletLog(walletLogId, WalletLogTypeEnum.ADD, new BigDecimal("100.00"));

        when(walletLogRepository.findById(walletLogId)).thenReturn(Optional.of(existingWalletLog));

        // When
        walletLogService.deleteById(walletLogId);

        // Then
        verify(walletLogRepository, times(1)).deleteById(walletLogId);
    }

    @Test
    public void testDeleteById_IdNotFound() {
        // Given
        Integer walletLogId = 1;

        when(walletLogRepository.findById(walletLogId)).thenReturn(Optional.empty());

        // When
        WalletLogException exception = assertThrows(WalletLogException.class, () -> walletLogService.deleteById(walletLogId));

        // Then
        assertEquals("Invalid walletLogId: 1", exception.getMessage());
        assertEquals(ErrorCode.WALLET_LOG_NOT_FOUND, exception.getErrorCode());

        // Verify that deleteById is never called
        verify(walletLogRepository, never()).deleteById(walletLogId);
    }

    @Test
    public void testDeleteById_Exception() {
        // Tạo dữ liệu mẫu cho wallet log
        WalletLog walletLog = new WalletLog();
        walletLog.setId(1);

        // Mock behavior
        when(walletLogRepository.findById(1)).thenReturn(Optional.of(walletLog));
        doThrow(new RuntimeException("Database error")).when(walletLogRepository).deleteById(1);

        // Gọi phương thức service và kiểm tra ngoại lệ
        WalletLogException exception = assertThrows(WalletLogException.class, () -> walletLogService.deleteById(1));

        // Xác minh kết quả
        assertEquals("Failed to delete wallet log", exception.getMessage());

        // Xác minh tương tác với mock
        verify(walletLogRepository).findById(1);
        verify(walletLogRepository).deleteById(1);
    }

    @Test
    public void testGetAllWalletLogsByMonth_Success() {
        // Given
        int month = 7;
        int year = 2023;
        List<WalletLog> mockWalletLogs = new ArrayList<>();
        mockWalletLogs.add(createMockWalletLog(1, WalletLogTypeEnum.ADD, new BigDecimal("100.00")));
        mockWalletLogs.add(createMockWalletLog(2, WalletLogTypeEnum.SUBTRACT, new BigDecimal("50.00")));

        when(walletLogRepository.findByMonthAndYear(month, year)).thenReturn(mockWalletLogs);

        // When
        List<WalletLogResponseDTO> result = walletLogService.getAllWalletLogsByMonth(month, year);

        // Then
        assertEquals(2, result.size());
        assertEquals(WalletLogTypeEnum.ADD, result.get(0).getType());
        assertEquals(WalletLogTypeEnum.SUBTRACT, result.get(1).getType());

        // Verify repository method called
        verify(walletLogRepository, times(1)).findByMonthAndYear(month, year);
    }

    @Test
    public void testGetAllWalletLogsByMonth_NotFound() {
        // Given
        int month = 7;
        int year = 2023;
        when(walletLogRepository.findByMonthAndYear(month, year)).thenReturn(new ArrayList<>());

        // When
        WalletLogException exception = assertThrows(WalletLogException.class, () -> walletLogService.getAllWalletLogsByMonth(month, year));

        // Then
        assertEquals("Wallet logs not found with month: 7/2023", exception.getMessage());
        assertEquals(ErrorCode.MONTH_WITHOUT_WALLET_LOG, exception.getErrorCode());

        // Verify repository method called
        verify(walletLogRepository, times(1)).findByMonthAndYear(month, year);
    }

    @Test
    public void testGetAllWalletLogsByMonth_Exception() {
        // Given
        int month = 7;
        int year = 2023;
        when(walletLogRepository.findByMonthAndYear(month, year)).thenThrow(new RuntimeException("Database error"));

        // When
        WalletLogException exception = assertThrows(WalletLogException.class, () -> walletLogService.getAllWalletLogsByMonth(month, year));

        // Then
        assertEquals("Failed to retrieve wallet logs for month: 7/2023", exception.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());

        // Verify repository method called
        verify(walletLogRepository, times(1)).findByMonthAndYear(month, year);
    }

    @Test
    public void testGetAllWalletLogsByDate_Success() {
        // Given
        int day = 29;
        int month = 7;
        int year = 2023;
        List<WalletLog> mockWalletLogs = new ArrayList<>();
        mockWalletLogs.add(createMockWalletLog(1, WalletLogTypeEnum.ADD, new BigDecimal("100.00")));
        mockWalletLogs.add(createMockWalletLog(2, WalletLogTypeEnum.SUBTRACT, new BigDecimal("50.00")));

        when(walletLogRepository.findWalletLogsByDate(day, month, year)).thenReturn(mockWalletLogs);

        // When
        List<WalletLogResponseDTO> result = walletLogService.getWalletLogsByDate(day, month, year);

        // Then
        assertEquals(2, result.size());
        assertEquals(WalletLogTypeEnum.ADD, result.get(0).getType());
        assertEquals(WalletLogTypeEnum.SUBTRACT, result.get(1).getType());

        // Verify repository method called
        verify(walletLogRepository, times(1)).findWalletLogsByDate(day, month, year);
    }

    @Test
    public void testGetAllWalletLogsByDate_NotFound() {
        // Given
        int day = 29;
        int month = 7;
        int year = 2023;
        when(walletLogRepository.findWalletLogsByDate(day, month, year)).thenReturn(new ArrayList<>());

        // When
        WalletLogException exception = assertThrows(WalletLogException.class, () -> walletLogService.getWalletLogsByDate(day, month, year));

        // Then
        assertEquals("Wallet logs not found with date: 29/7/2023", exception.getMessage());
        assertEquals(ErrorCode.MONTH_WITHOUT_WALLET_LOG, exception.getErrorCode());

        // Verify repository method called
        verify(walletLogRepository, times(1)).findWalletLogsByDate(day, month, year);
    }

    @Test
    public void testGetAllWalletLogsByDate_Exception() {
        // Given
        int day = 29;
        int month = 7;
        int year = 2023;
        when(walletLogRepository.findWalletLogsByDate(day, month, year)).thenThrow(new RuntimeException("Database error"));

        // When
        WalletLogException exception = assertThrows(WalletLogException.class, () -> walletLogService.getWalletLogsByDate(day, month, year));

        // Then
        assertEquals("Failed to retrieve wallet logs for date: 29/7/2023", exception.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());

        // Verify repository method called
        verify(walletLogRepository, times(1)).findWalletLogsByDate(day, month, year);
    }

    @Test
    public void testGetWalletLogsForLast12Days_NoResults() {
        // Given
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(12);

        when(walletLogRepository.findLast12Days(startDate, endDate)).thenReturn(new ArrayList<>());

        // When
        WalletLogException exception = assertThrows(WalletLogException.class, () -> walletLogService.getWalletLogsForLast12Days());

        // Then
        assertEquals("No wallet logs found for the last 12 days", exception.getMessage());
        assertEquals(ErrorCode.WALLET_LOG_NOT_FOUND, exception.getErrorCode());
    }

    private WalletLog createMockWalletLog(Integer id, WalletLogTypeEnum type, BigDecimal amount) {
        WalletLog walletLog = new WalletLog();
        walletLog.setId(id);
        walletLog.setType(type);
        walletLog.setAmount(amount);
        walletLog.setCreateAt(LocalDateTime.now());
        walletLog.setIsDeleted(false);
        return walletLog;
    }


}
