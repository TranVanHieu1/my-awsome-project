package com.ojt.mockproject.controller;

import com.ojt.mockproject.dto.Wallet.CreateWalletRequest;
import com.ojt.mockproject.dto.Wallet.Requests.AddPriceToWalletRequest;
import com.ojt.mockproject.dto.Wallet.Responses.AddPriceWalletResponse;
import com.ojt.mockproject.dto.Wallet.Responses.GetWalletResponseDTO;
import com.ojt.mockproject.dto.Wallet.UpdateWalletRequest;
import com.ojt.mockproject.dto.Wallet.WalletResponseDTO;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.WalletLog;
import com.ojt.mockproject.exceptionhandler.ErrorCode;
import com.ojt.mockproject.exceptionhandler.NotFoundException;
import com.ojt.mockproject.exceptionhandler.Wallet.WalletException;
import com.ojt.mockproject.service.WalletService;
import com.ojt.mockproject.utils.AccountUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletControllerTest {

    @InjectMocks
    private WalletController walletController;

    @Mock
    private WalletService walletService;

    @Mock
    private AccountUtils accountUtils;

    private WalletResponseDTO walletResponseDTO;

    private GetWalletResponseDTO getWalletResponseDTO;

    private CreateWalletRequest createWalletRequest;

    private UpdateWalletRequest updateWalletRequest;

    private List<WalletResponseDTO> walletResponseDTOList;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        getWalletResponseDTO = new GetWalletResponseDTO(1, "Minh Chau", 1, BigDecimal.ZERO, "Bidv", "12345");
        walletResponseDTO = new WalletResponseDTO(1, "Minh Chau", 1, BigDecimal.ZERO, "Bidv", "12345");
        createWalletRequest = new CreateWalletRequest();
        createWalletRequest.setBankName("Bidv");
        createWalletRequest.setBankAccountNumber("12345");


        updateWalletRequest = new UpdateWalletRequest();
        updateWalletRequest.setBankName("Vietcombank");
        updateWalletRequest.setBankAccountNumber("67890");

        WalletResponseDTO wallet1 = new WalletResponseDTO(1, "Minh Chau", 1, BigDecimal.ZERO, "Bidv", "12345");
        WalletResponseDTO wallet2 = new WalletResponseDTO(2, "John Doe", 2, BigDecimal.valueOf(500), "ACB", "67890");

        walletResponseDTOList = Arrays.asList(wallet1, wallet2);
    }

    @Test
    public void testCreateWallet_Success() {
        // Setup
        when(walletService.createWallet(any(CreateWalletRequest.class))).thenReturn(getWalletResponseDTO);

        // Exercise
        ResponseEntity<GetWalletResponseDTO> responseEntity = walletController.createWallet(createWalletRequest);

        // Verify
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(getWalletResponseDTO, responseEntity.getBody());
    }

    @Test
    public void testGetWalletByAccountId_Success() {
        // Setup
        when(walletService.getWalletByAccountIdResponse(anyInt())).thenReturn(getWalletResponseDTO);

        // Exercise
        ResponseEntity<GetWalletResponseDTO> responseEntity = walletController.getWalletByAccountId(1);

        // Verify
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(getWalletResponseDTO, responseEntity.getBody());
    }

    @Test
    public void testGetWalletById() {
        when(walletService.getWalletById(1)).thenReturn(walletResponseDTO);

        ResponseEntity<WalletResponseDTO> response = walletController.getWalletById(1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(walletResponseDTO, response.getBody());
        verify(walletService, times(1)).getWalletById(1);
    }

    @Test
    public void testGetAllWallets() {
        List<WalletResponseDTO> walletResponseDTOs = new ArrayList<>();
        walletResponseDTOs.add(walletResponseDTO);

        when(walletService.getAllWallets()).thenReturn(walletResponseDTOs);

        ResponseEntity<List<WalletResponseDTO>> response = walletController.getAllWallets();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(walletResponseDTOs, response.getBody());
        verify(walletService, times(1)).getAllWallets();
    }

    @Test
    public void testUpdateWallet_Success() {
        // Setup
        int walletId = 1;
        when(walletService.updateWallet(walletId, updateWalletRequest)).thenReturn(getWalletResponseDTO);

        // Exercise
        ResponseEntity<GetWalletResponseDTO> responseEntity = walletController.updateWallet(walletId, updateWalletRequest);

        // Verify
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(getWalletResponseDTO, responseEntity.getBody());
    }

    @Test
    public void testUpdateWalletCurrent_Success() {
        int walletId = 1;
        when(walletService.updateWalletCurrenAccount(updateWalletRequest)).thenReturn(getWalletResponseDTO);

        // When
        ResponseEntity<GetWalletResponseDTO> responseEntity = walletController.updateWallet(updateWalletRequest);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(getWalletResponseDTO, responseEntity.getBody());
    }
    @Test
    public void testUpdateWallet_NotFound() {
        // Given
        when(walletService.updateWalletCurrenAccount(updateWalletRequest))
                .thenThrow(new NotFoundException("Wallet not found"));

        // When
        ResponseEntity<GetWalletResponseDTO> responseEntity = walletController.updateWallet(updateWalletRequest);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }
    @Test
    public void testUpdateWallet_InternalServerError() {
        // Given
        when(walletService.updateWalletCurrenAccount(updateWalletRequest))
                .thenThrow(new RuntimeException("Unexpected error"));

        // When
        ResponseEntity<GetWalletResponseDTO> responseEntity = walletController.updateWallet(updateWalletRequest);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void testDeleteWallet_success() {
        int walletId = 1;
        String expectedResponse = "Wallet with id: " + walletId + " has been successfully deleted.";

        when(walletService.deleteWallet(walletId)).thenReturn(expectedResponse);

        ResponseEntity<String> response = walletController.deleteWallet(walletId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(walletService, times(1)).deleteWallet(walletId);
    }

    @Test
    public void testDeleteWallet_walletNotFound() {
        int walletId = 1;
        String errorMessage = "Wallet not found with id: " + walletId;

        doThrow(new WalletException(errorMessage, ErrorCode.WALLET_NOT_FOUND)).when(walletService).deleteWallet(walletId);

        WalletException exception = assertThrows(WalletException.class, () -> {
            walletController.deleteWallet(walletId);
        });

        assertEquals(errorMessage, exception.getMessage());
        assertEquals(ErrorCode.WALLET_NOT_FOUND, exception.getErrorCode());
        verify(walletService, times(1)).deleteWallet(walletId);
    }

    @Test
    public void testGetWalletByCurrentAccount_Success() {

        walletResponseDTO.setWalletId(1);
        walletResponseDTO.setAccountName("Minh Chau");
        walletResponseDTO.setAccountId(1);
        walletResponseDTO.setBalance(BigDecimal.valueOf(1000.00));
        walletResponseDTO.setBankName("Bidv");
        walletResponseDTO.setBankAccountNumber("12345");
        walletResponseDTO.setTotalMoneyInLast30Days(BigDecimal.valueOf(500.00));
        walletResponseDTO.setTotalMoneyOutLast30Days(BigDecimal.valueOf(200.00));

        when(walletService.getWalletByCurrentAccount()).thenReturn(walletResponseDTO);

        ResponseEntity<WalletResponseDTO> responseEntity = walletController.getWalletByCurrentAccount("token");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(walletResponseDTO, responseEntity.getBody());
    }

    @Test
    public void testGetWalletByCurrentAccount_Failure() {
        // Given
        when(walletService.getWalletByCurrentAccount())
                .thenThrow(new WalletException("Error retrieving wallet", ErrorCode.INTERNAL_SERVER_ERROR));

        // When
        ResponseEntity<WalletResponseDTO> responseEntity = walletController.getWalletByCurrentAccount("token");

        // Then
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void testAddPriceToWallet_Success() {
        // Given
        Account account = new Account();
        account.setId(1); // Initialize the account ID

        AddPriceToWalletRequest request = new AddPriceToWalletRequest();
        request.setInitialBalance(new BigDecimal("100.00")); // Set the initial balance

        // Create mock response
        AddPriceWalletResponse.DataResponse dataResponse = new AddPriceWalletResponse.DataResponse(
                1, new BigDecimal("100.00"), new BigDecimal("50.00"),
                new WalletLog(), "someString1", "someString2"
        );

        AddPriceWalletResponse addPriceWalletResponse = new AddPriceWalletResponse(
                "Success", "Wallet updated", 1, dataResponse
        );

        ResponseEntity<AddPriceWalletResponse> responseEntity = new ResponseEntity<>(addPriceWalletResponse, HttpStatus.OK);

        // Mock the accountUtils.getCurrentAccount() method
        when(accountUtils.getCurrentAccount()).thenReturn(account);

        // Mock the walletService.addPriceToWallet() method
        when(walletService.addPriceToWallet(account.getId(), request.getInitialBalance())).thenReturn(responseEntity);

        // When
        ResponseEntity<AddPriceWalletResponse> actualResponseEntity = walletController.addPriceToWallet(request);

        // Then
        assertEquals(HttpStatus.OK, actualResponseEntity.getStatusCode());
        assertEquals(addPriceWalletResponse, actualResponseEntity.getBody());
    }

}
