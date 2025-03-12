package com.ojt.mockproject.service;

import com.ojt.mockproject.dto.Order.Responses.OrderResponseDTO;
import com.ojt.mockproject.dto.Transaction.InvoiceCoursesResponseDTO;
import com.ojt.mockproject.dto.Transaction.Responses.GetCashoutTransactionResponse;
import com.ojt.mockproject.dto.Transaction.TransactionRequestDTO;
import com.ojt.mockproject.dto.Transaction.TransactionResponseDTO;
import com.ojt.mockproject.dto.Transaction.TransactionDetailsDTO;
import com.ojt.mockproject.entity.*;
import com.ojt.mockproject.entity.Enum.TransactionStatusEnum;
import com.ojt.mockproject.entity.Enum.TransactionTypeEnum;
import com.ojt.mockproject.exceptionhandler.ErrorCode;
import com.ojt.mockproject.exceptionhandler.Transaction.TransactionException;
import com.ojt.mockproject.exceptionhandler.TransactionExceptionHandler;
import com.ojt.mockproject.repository.*;
import com.ojt.mockproject.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    private WalletLogRepository walletLogRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CourseRepository courseRepository;

// nma no k co cai DTO
    // do lom

    //tiep tiep :)))
    @Autowired
    OrderRepository orderRepository;
    @Lazy
    @Autowired
    OrderService orderService;

    // sao no k import


    // Chức năng tạo giao dịch mới
    public TransactionResponseDTO createTransaction(TransactionRequestDTO transactionRequestDTO) {
        try {
//                // Lấy tài khoản hiện tại
//                Account curAcc = accountRepository.findById(transactionRequestDTO.getAccountId()).orElseThrow(() ->
//                        new TransactionExceptionHandler("No Account founded", ErrorCode.ACCOUNT_NOT_FOUND));

            // Tìm kiếm tài khoản
            Account account = accountRepository.findById(transactionRequestDTO.getAccountId()).orElseThrow(() ->
                    new TransactionExceptionHandler("No Account founded", ErrorCode.ACCOUNT_NOT_FOUND));

            // Tìm kiếm đơn hàng
            Orderr order = orderRepository.findById(transactionRequestDTO.getOrderId()).orElseThrow(() ->
                    new TransactionExceptionHandler("No Order founded", ErrorCode.ORDER_NOT_FOUND));

            // Kiểm tra đầu vào
            if (transactionRequestDTO.getTotalPrice() == null || transactionRequestDTO.getTotalPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new TransactionExceptionHandler("Total price must be greater than zero", ErrorCode.INVALID_INPUT);
            }

            if (transactionRequestDTO.getPaymentMethod() == null) {
                throw new TransactionExceptionHandler("Payment method can't be NULL", ErrorCode.INVALID_INPUT);
            }

            // Tạo đối tượng Transaction
            Transaction transaction = new Transaction();
            transaction.setAccount(account);
            transaction.setOrderr(order);
            transaction.setTotalPrice(transactionRequestDTO.getTotalPrice());
            transaction.setCreateAt(LocalDateTime.now());
            transaction.setPaymentMethod(transactionRequestDTO.getPaymentMethod());
            transaction.setStatus(TransactionStatusEnum.SUCCESS); // hoặc trạng thái ban đầu mong muốn
            transaction.setType(transactionRequestDTO.getTransactionTypeEnum());
            transaction.setIsDeleted(false);
            System.out.println("tuandatgdfgdfg");
            // Lưu giao dịch vào cơ sở dữ liệu
            transactionRepository.save(transaction);
            System.out.println("tuandat2gfdfgdgd");
            // Chuyển đổi đối tượng Transaction thành DTO và trả về
            return convertToTransactionResponseDTO(transaction);
        } catch (TransactionExceptionHandler e) {
            throw e;
        } catch (ClassCastException e) {
            throw new TransactionExceptionHandler("Login to create transaction", ErrorCode.NOT_LOGIN);
        } catch (Exception e) {
            throw new TransactionExceptionHandler("Error creating transaction", ErrorCode.INTERNAL_SERVER_ERROR);
        }


    }

    private TransactionResponseDTO convertToTransactionResponseDTO(Transaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setAccountId(transaction.getAccount().getId());
        dto.setId(transaction.getId());
        dto.setOrderId(transaction.getOrderr().getId());
        dto.setTotalPrice(transaction.getTotalPrice());
        dto.setPaymentMethod(transaction.getPaymentMethod());

        return dto;

    }

    public List<TransactionResponseDTO> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        return transactions.stream()
                .map(this::convertToTransactionResponseDTO)
                .collect(Collectors.toList());

        // Phương thức chuyển đổi từ Transaction sang TransactionResponseDTO


    }

    public ResponseEntity<TransactionResponseDTO> getTransactionByOrderID(Account acccount, Integer orderId) {
        OrderResponseDTO orderResponseDTO = orderService.getOrderById(orderId);
        TransactionResponseDTO transactionResponseDTO  = new TransactionResponseDTO();
        transactionResponseDTO.setId(orderResponseDTO.getId());
        transactionResponseDTO.setOrderId(orderId);
        transactionResponseDTO.setAccountId(acccount.getId());
        transactionResponseDTO.setTotalPrice(orderResponseDTO.getTotalPrice());
        transactionResponseDTO.setPaymentMethod(orderResponseDTO.getPaymentMethod());
        return ResponseEntity.ok(transactionResponseDTO);
    }


    public TransactionDetailsDTO getTransactionByWalletLogId(Integer walletLogId) {
        try {
            WalletLog walletLog = walletLogRepository.findById(walletLogId)
                    .orElseThrow(() -> new TransactionException("Wallet log not found with id: " + walletLogId, ErrorCode.WALLET_LOG_NOT_FOUND));
            ValidationUtils.validateWalletLogIsDeleted(walletLog);
            Transaction transaction = walletLog.getTransaction();
            Orderr order = transaction.getOrderr();
            Account account = transaction.getAccount();

            List<InvoiceCoursesResponseDTO> courses = order.getCoursesList().stream()
                    .map(courseId -> courseRepository.findByIdAndIsDeleted(courseId,false)
                            .map(course -> new InvoiceCoursesResponseDTO(course.getId(), course.getName(), course.getPrice()))
                            .orElse(new InvoiceCoursesResponseDTO(courseId, "Unknown Course", BigDecimal.ZERO)))
                    .collect(Collectors.toList());

            int numberOfItems = courses.size();

            return new TransactionDetailsDTO(
                    account.getName(),
                    account.getPhone(),
                    account.getEmail(),
                    order.getId(),
                    order.getCreateAt(),
                    transaction.getId(),
                    transaction.getTotalPrice(),
                    transaction.getPaymentMethod().toString(),
                    walletLog.getCreateAt(),
                    numberOfItems,
                    courses
            );
        } catch (TransactionException e) {
            throw e;
        } catch (Exception e) {
            throw new TransactionException("Internal server error", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public List<GetCashoutTransactionResponse> getCashoutTransactions(Account account, TransactionTypeEnum type) {
        List<Transaction> transactions =
                transactionRepository.findTransactionByAccountIdAndType(account.getId(), type);
        List<GetCashoutTransactionResponse> responses = new ArrayList<>();
        return transactions.stream()
                .map(response -> new GetCashoutTransactionResponse(
                        response.getId(),
                        response.getTotalPrice(),
                        response.getPaymentMethod(),
                        response.getCreateAt()
                ))
                .collect(Collectors.toList());
    }
}
