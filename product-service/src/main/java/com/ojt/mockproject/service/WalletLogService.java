package com.ojt.mockproject.service;
import com.ojt.mockproject.dto.WalletLog.Requests.WalletLogRequestDTO;
import com.ojt.mockproject.dto.WalletLog.Response.WalletLogAccountResponseDTO;
import com.ojt.mockproject.dto.WalletLog.Response.WalletLogResponseDTO;
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
import com.ojt.mockproject.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class WalletLogService {

    @Autowired
    private WalletLogRepository walletLogRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    //View all wallet log
    public List<WalletLogResponseDTO> findAll() {
        try {
            List<WalletLog> walletLogs = walletLogRepository.findAll();
            return walletLogs.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new WalletLogException("Failed to retrieve wallet log", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private WalletLogResponseDTO convertToDTO(WalletLog walletLog) {
        WalletLogResponseDTO dto = new WalletLogResponseDTO();
        dto.setId(walletLog.getId());

        Wallet wallet = walletLog.getWallet();
        if (wallet != null) {
            dto.setWalletId(wallet.getId());
        } else {
            dto.setWalletId(null);
        }

        dto.setType(walletLog.getType());
        dto.setAmount(walletLog.getAmount());
        dto.setCreateAt(walletLog.getCreateAt());
        dto.setIsDeleted(false);
        return dto;

    }

    //Create wallet log
    public WalletLogResponseDTO save(WalletLogRequestDTO walletLogRequestDTO) {
        try {
            Wallet wallet = walletRepository.findById(walletLogRequestDTO.getWalletId())
                    .orElseThrow(() -> new WalletException("Invalid walletId: " + walletLogRequestDTO.getWalletId(), ErrorCode.WALLET_NOT_FOUND));
            Transaction transaction = transactionRepository.findById(walletLogRequestDTO.getTransactionId())
                    .orElseThrow(() -> new WalletException("Invalid transactionId: " + walletLogRequestDTO.getTransactionId(), ErrorCode.TRANSACTION_NOT_FOUND));

            ValidationUtils.validatePriceWallet(walletLogRequestDTO.getAmount());
            WalletLog walletLog = new WalletLog();
            walletLog.setWallet(wallet);
            walletLog.setTransaction(transaction);
            walletLog.setType(walletLogRequestDTO.getType());
            walletLog.setAmount(walletLogRequestDTO.getAmount());
            walletLog.setCreateAt(LocalDateTime.now());
            walletLog.setIsDeleted(walletLogRequestDTO.getIsDeleted());

            WalletLog savedWalletLog = walletLogRepository.save(walletLog);

            return convertToDTO(savedWalletLog);
        } catch (ValidationException e){
            throw e;
        } catch (WalletException e ) {
            throw new WalletException("Failed to create wallet log due to WalletException: " + e.getMessage(), ErrorCode.WALLET_NOT_FOUND);
        } catch (Exception e) {
            throw new WalletLogException("Failed to create wallet log", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    //Update wallet log
    public WalletLogResponseDTO update(Integer id, WalletLogRequestDTO walletLogRequestDTO) {
        try {
            ValidationUtils.validatePriceWallet(walletLogRequestDTO.getAmount());
            WalletLog walletLog = walletLogRepository.findById(id)
                    .orElseThrow(() -> new WalletLogException("Invalid walletLogId: " + id, ErrorCode.WALLET_LOG_NOT_FOUND));

            Wallet wallet = walletRepository.findById(walletLogRequestDTO.getWalletId())
                    .orElseThrow(() -> new WalletException("Invalid walletId: " + walletLogRequestDTO.getWalletId(), ErrorCode.WALLET_NOT_FOUND));

            walletLog.setWallet(wallet);
            walletLog.setType(walletLogRequestDTO.getType());
            walletLog.setAmount(walletLogRequestDTO.getAmount());
            walletLog.setCreateAt(LocalDateTime.now());
            walletLog.setIsDeleted(walletLogRequestDTO.getIsDeleted());

            WalletLog savedWalletLog = walletLogRepository.save(walletLog);
            return convertToDTO(savedWalletLog);
        } catch (ValidationException | WalletLogException | WalletException e){
            throw e;
        } catch (Exception e) {
            throw new WalletLogException("Failed to update wallet log", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    //Delete wallet log
    public void deleteById(Integer id) {
        try {
            walletLogRepository.findById(id)
                    .orElseThrow(() -> new WalletLogException("Invalid walletLogId: " + id, ErrorCode.WALLET_LOG_NOT_FOUND));
            walletLogRepository.deleteById(id);
        } catch (WalletLogException e) {
            throw e;
        } catch (Exception e) {
            throw new WalletLogException("Failed to delete wallet log", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    //View wallet log by walletLogId
    public WalletLogResponseDTO getWalletLogById(Integer id) {
        try {
            WalletLog walletLog = walletLogRepository.findById(id)
                    .orElseThrow(() -> new WalletLogException("WalletLog not found with id: " + id, ErrorCode.WALLET_LOG_NOT_FOUND));
            ValidationUtils.validateWalletLogIsDeleted(walletLog);
            return convertToDTO(walletLog);
        } catch (WalletLogException e) {
            throw e;
        } catch (Exception e) {
            throw new WalletLogException("Failed to retrieve WalletLog with id " + id, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    //view wallet log by walletId
    public List<WalletLogResponseDTO> getWalletLogByWalletId(Integer walletId) {
        try {
            List<WalletLog> walletLogs = walletLogRepository.findByWalletIdAndIsDeleted(walletId,false);
            if (walletLogs.isEmpty()) {
                throw new WalletException("Wallet not found with id: " + walletId, ErrorCode.WALLET_NOT_FOUND);
            }
            return walletLogs.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (WalletException e) {
            throw e;
        } catch (Exception e) {
            throw new CourseException("Failed to retrieve WalletLog with Wallet id " + walletId, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    //view wallet log by accountID
    public List<WalletLogAccountResponseDTO> getAllWalletLogsByAccountId(Integer accountId) {
        try {
            List<WalletLogAccountResponseDTO> walletLogs = walletLogRepository.findAllByAccountId1(accountId);
            if (walletLogs.isEmpty()) {
                throw new WalletException("Wallet logs not found for Account id: " + accountId, ErrorCode.WALLET_NOT_FOUND);
            }
            return walletLogs;
        } catch (WalletException e) {
            throw e;
        } catch (Exception e) {
            throw new WalletLogException("Failed to retrieve Wallet logs with Account id " + accountId, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    //View wallet log by month
    public List<WalletLogResponseDTO> getAllWalletLogsByMonth(int month, int year) {
        try {
            List<WalletLog> walletLogs = walletLogRepository.findByMonthAndYear(month, year);
            if(walletLogs.isEmpty()){
                throw new WalletLogException("Wallet logs not found with month: " + month + "/" + year, ErrorCode.MONTH_WITHOUT_WALLET_LOG);
            }
            return walletLogs.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (WalletLogException e) {
            throw e;
        }
        catch (Exception e) {
            throw new WalletLogException("Failed to retrieve wallet logs for month: " + month + "/" + year, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    //view wallet log by date
    public List<WalletLogResponseDTO> getWalletLogsByDate(int day, int month, int year) {
        try {
            List<WalletLog> walletLogs = walletLogRepository.findWalletLogsByDate(day, month, year);
            if(walletLogs.isEmpty()){
                throw new WalletLogException("Wallet logs not found with date: " + day + "/" + month + "/" + year, ErrorCode.MONTH_WITHOUT_WALLET_LOG);
            }
            return walletLogs.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (WalletLogException e) {
            throw e;
        }
        catch (Exception e) {
            throw new WalletLogException("Failed to retrieve wallet logs for date: " + day + "/" + month + "/" + year, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    //View total sale, total amount last 12 days
    public List<Map<String, Object>> getWalletLogsForLast12Days() {
        try {
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.minusDays(12);

            List<Object[]> results = walletLogRepository.findLast12Days(startDate, endDate);
            if (results.isEmpty()) {
                throw new WalletLogException("No wallet logs found for the last 12 days", ErrorCode.WALLET_LOG_NOT_FOUND);
            }

            return results.stream()
                    .map(result -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("date", result[0]);
                        map.put("totalSaleInDay", result[1]);
                        map.put("totalAmount", result[2]);
                        return map;
                    })
                    .collect(Collectors.toList());
        } catch (WalletLogException e) {
            throw e;
        } catch (Exception e) {
            throw new WalletLogException("Failed to retrieve wallet logs for the last 12 days", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
