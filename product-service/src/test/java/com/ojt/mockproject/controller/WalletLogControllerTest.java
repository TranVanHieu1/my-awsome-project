package com.ojt.mockproject.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import com.ojt.mockproject.dto.WalletLog.Requests.WalletLogRequestDTO;
import com.ojt.mockproject.dto.WalletLog.Response.WalletLogAccountResponseDTO;
import com.ojt.mockproject.dto.WalletLog.Response.WalletLogResponseDTO;
import com.ojt.mockproject.entity.Enum.WalletLogTypeEnum;
import com.ojt.mockproject.service.WalletLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class WalletLogControllerTest {

    @Mock
    private WalletLogService walletLogService;

    @InjectMocks
    private WalletLogController walletLogController;

    private static final Integer WALLETLOG_ID = 1;
    private static final Integer WALLET_ID = 1;
    private static final Integer ACCOUNT_ID = 1;

    @BeforeEach
    void setUp(){
    }

    @Test
    public void testGetAllWalletLog() {
        List<WalletLogResponseDTO> walletLogs = Arrays.asList(
                new WalletLogResponseDTO(), new WalletLogResponseDTO());
        when(walletLogService.findAll()).thenReturn(walletLogs);

        List<WalletLogResponseDTO> result = walletLogController.getAllWalletLogs();

        verify(walletLogService, times(1)).findAll();
        assertEquals(walletLogs, result);
    }

    @Test
    public void testWalletLogById() {
        WalletLogResponseDTO walletLog = new WalletLogResponseDTO();
        when(walletLogService.getWalletLogById(WALLETLOG_ID)).thenReturn(walletLog);

        ResponseEntity<WalletLogResponseDTO> result = walletLogController.getWalletLogById(WALLETLOG_ID);

        verify(walletLogService, times(1)).getWalletLogById(WALLETLOG_ID);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(walletLog, result.getBody());
    }

    @Test
    public void testGetWalletLogByAccountId() {
        List<WalletLogAccountResponseDTO> walletLogs = Arrays.asList(
                new WalletLogAccountResponseDTO(1, 1,1,1, WalletLogTypeEnum.ADD, new BigDecimal("100.00"), LocalDateTime.now(), false),
                new WalletLogAccountResponseDTO(2, 1,1,1, WalletLogTypeEnum.SUBTRACT, new BigDecimal("50.00"), LocalDateTime.now(), false)
        );

        when(walletLogService.getAllWalletLogsByAccountId(ACCOUNT_ID)).thenReturn(walletLogs);

        // When
        ResponseEntity<List<WalletLogAccountResponseDTO>> response = walletLogController.getAllWalletLogByAccountId(ACCOUNT_ID);

        // Then
        verify(walletLogService, times(1)).getAllWalletLogsByAccountId(ACCOUNT_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(walletLogs, response.getBody());
    }

    @Test
    public void testGetWalletLogByWalletId() {
        List<WalletLogResponseDTO> walletLogs = Arrays.asList(
                new WalletLogResponseDTO(), new WalletLogResponseDTO());
        when(walletLogService.getWalletLogByWalletId(WALLET_ID)).thenReturn(walletLogs);

        List<WalletLogResponseDTO> result = walletLogController.getWalletLogByWalletId(WALLET_ID);

        verify(walletLogService, times(1)).getWalletLogByWalletId(WALLET_ID);
        assertEquals(walletLogs, result);
    }

    @Test
    public void testCreateWalletLog() {
        // Tạo dữ liệu mẫu cho request
        WalletLogRequestDTO requestDTO = new WalletLogRequestDTO();
        requestDTO.setWalletId(1);
        requestDTO.setType(WalletLogTypeEnum.ADD);
        requestDTO.setAmount(new BigDecimal("100.00"));

        // Tạo dữ liệu mẫu cho response
        WalletLogResponseDTO responseDTO = new WalletLogResponseDTO();
        responseDTO.setId(1);
        responseDTO.setWalletId(1);
        responseDTO.setType(WalletLogTypeEnum.ADD);
        responseDTO.setAmount(new BigDecimal("100.00"));
        responseDTO.setCreateAt(LocalDateTime.now());
        responseDTO.setIsDeleted(true);

        // Cấu hình mock behavior
        when(walletLogService.save(any(WalletLogRequestDTO.class))).thenReturn(responseDTO);

        // Gọi phương thức controller
        ResponseEntity<WalletLogResponseDTO> response = walletLogController.createWalletLog(requestDTO);

        // Xác minh kết quả
        assert response.getStatusCode() == HttpStatus.CREATED;
        assert response.getBody() != null;
        assert response.getBody().getId() == 1;
        assert response.getBody().getType() == WalletLogTypeEnum.ADD;

        // Xác minh tương tác với mock
        verify(walletLogService).save(any(WalletLogRequestDTO.class));
    }

    @Test
    public void testUpdateWalletLog_Success() {
        // Given
        Integer walletLogId = 1;
        WalletLogRequestDTO requestDTO = new WalletLogRequestDTO();
        requestDTO.setWalletId(1);
        requestDTO.setType(WalletLogTypeEnum.ADD);
        requestDTO.setAmount(new BigDecimal("100.00"));

        WalletLogResponseDTO responseDTO = new WalletLogResponseDTO();
        responseDTO.setId(walletLogId);
        responseDTO.setWalletId(1);
        responseDTO.setType(WalletLogTypeEnum.ADD);
        responseDTO.setAmount(new BigDecimal("100.00"));

        // Mock behavior
        when(walletLogService.update(eq(walletLogId), any(WalletLogRequestDTO.class))).thenReturn(responseDTO);

        // When
        ResponseEntity<WalletLogResponseDTO> responseEntity = walletLogController.updateWalletLog(walletLogId, requestDTO);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(walletLogId, Objects.requireNonNull(responseEntity.getBody()).getId());
        assertEquals(WalletLogTypeEnum.ADD, responseEntity.getBody().getType());

        // Verify service method called
        verify(walletLogService).update(eq(walletLogId), any(WalletLogRequestDTO.class));
    }

    @Test
    public void testDeleteWalletLog_Success() {
        // Given
        Integer walletLogId = 1;

        // When
        ResponseEntity<String> responseEntity = walletLogController.deleteWalletLog(walletLogId);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Wallet log with id " + walletLogId + " deleted", responseEntity.getBody());

        // Verify service method called
        verify(walletLogService).deleteById(walletLogId);
    }

    @Test
    public void testGetAllWalletLogsByMonth() {
        // Given
        int month = 7;
        int year = 2024;

        List<WalletLogResponseDTO> walletLogs = Arrays.asList(
                new WalletLogResponseDTO(), new WalletLogResponseDTO());

        when(walletLogService.getAllWalletLogsByMonth(month, year)).thenReturn(walletLogs);

        // When
        List<WalletLogResponseDTO> result = walletLogController.getAllWalletLogsByMonth(month, year);

        // Then
        verify(walletLogService, times(1)).getAllWalletLogsByMonth(month, year);
        assertEquals(walletLogs, result);
    }

    @Test
    public void testGetAllWalletLogsByDate() {
        // Given
        int day = 29;
        int month = 7;
        int year = 2024;

        List<WalletLogResponseDTO> walletLogs = Arrays.asList(
                new WalletLogResponseDTO(), new WalletLogResponseDTO());

        when(walletLogService.getWalletLogsByDate(day, month, year)).thenReturn(walletLogs);

        // When
        List<WalletLogResponseDTO> result = walletLogController.getAllWalletLogsByDate(day, month, year);

        // Then
        verify(walletLogService, times(1)).getWalletLogsByDate(day, month, year);
        assertEquals(walletLogs, result);
    }

    @Test
    public void testGetWalletLogsForLast12Days() {
        // Given
        List<Map<String, Object>> walletLogs = Arrays.asList(
                new HashMap<String, Object>() {{
                    put("date", LocalDate.now().minusDays(1));
                    put("totalSales", 5);
                    put("totalAmount", new BigDecimal("100.00"));
                }},
                new HashMap<String, Object>() {{
                    put("date", LocalDate.now());
                    put("totalSales", 10);
                    put("totalAmount", new BigDecimal("200.00"));
                }}
        );

        when(walletLogService.getWalletLogsForLast12Days()).thenReturn(walletLogs);

        // When
        List<Map<String, Object>> result = walletLogController.getWalletLogsForLast12Days();

        // Then
        verify(walletLogService, times(1)).getWalletLogsForLast12Days();
        assertEquals(walletLogs, result);
    }


}

