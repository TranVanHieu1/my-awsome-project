package com.ojt.mockproject.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ojt.mockproject.dto.Order.Requests.OrderRequestDTO;
import com.ojt.mockproject.dto.Order.Requests.OrderRequestUpdateDTO;
import com.ojt.mockproject.dto.Order.Requests.ReturnOrderPaymentDTO;
import com.ojt.mockproject.dto.Order.Responses.DailyEarningsResponse;
import com.ojt.mockproject.dto.Order.Responses.OrderResponseDTO;
import com.ojt.mockproject.dto.Order.Responses.GenericResponse;
import com.ojt.mockproject.dto.Transaction.TransactionRequestDTO;
import com.ojt.mockproject.dto.Transaction.TransactionResponseDTO;
import com.ojt.mockproject.dto.Wallet.Responses.BuyCourseResponse;
import com.ojt.mockproject.entity.*;
import com.ojt.mockproject.entity.Enum.*;
import com.ojt.mockproject.exceptionhandler.ErrorCode;
import com.ojt.mockproject.exceptionhandler.ValidationException;
import com.ojt.mockproject.exceptionhandler.Wallet.WalletAppException;
import com.ojt.mockproject.exceptionhandler.account.AccountException;
import com.ojt.mockproject.exceptionhandler.course.CourseAppException;
import com.ojt.mockproject.exceptionhandler.course.CourseException;
import com.ojt.mockproject.exceptionhandler.order.FailedToPayException;
import com.ojt.mockproject.exceptionhandler.order.OrderException;
import com.ojt.mockproject.exceptionhandler.order.UnableToGetInformationFromExtractedOrder;
import com.ojt.mockproject.exceptionhandler.token.UnableToExtractTokenForReturnOrder;
import com.ojt.mockproject.repository.AccountRepository;
import com.ojt.mockproject.repository.CourseRepository;
import com.ojt.mockproject.repository.OrderRepository;
import com.ojt.mockproject.repository.TransactionRepository;
import com.ojt.mockproject.utils.AccountUtils;
import com.ojt.mockproject.utils.StringUtil;
import com.ojt.mockproject.utils.ValidationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.anyInt;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private JWTService jwtService;

    @Mock
    private StringUtil stringUtils;

    @Mock
    private CourseService courseService;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private WalletService walletService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private AccountUtils accountUtils;
    @Mock
    private ValidationUtils validationUtils;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private KafkaTemplate<String, OrderResponseDTO> orderKafkaTemplate;

    @InjectMocks
    private OrderService orderService;

    private OrderRequestDTO orderRequestDTO;
    private OrderRequestUpdateDTO orderRequestUpdateDTO;
    private Orderr order1;
    private Orderr order2;
    private Account account;
    private String token = "valid_token_here";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Setup OrderRequestDTO
        orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setTotalPrice(new BigDecimal("100.00"));
        orderRequestDTO.setCourses(Arrays.asList(1, 2, 3));
        orderRequestDTO.setPaymentMethod(PaymentMethodEnum.PAYOS);

        // Setup OrderRequestUpdateDTO
        orderRequestUpdateDTO = new OrderRequestUpdateDTO();
        orderRequestUpdateDTO.setCourses(Arrays.asList(1, 2, 3));
        orderRequestUpdateDTO.setStatus(OrderStatusEnum.SUCCESS);
        orderRequestUpdateDTO.setIsDeleted(true);
        orderRequestUpdateDTO.setPaymentMethod(PaymentMethodEnum.PAYOS);


        // Setup Account
        account = Account.builder()
                .id(1)
                .name("John Doe")
                .email("johndoe@example.com")
                .password("password")
                .phone("1234567890")
                .gender(AccountGenderEnum.MALE)
                .avatar("avatar.png")
                .role(AccountRoleEnum.STUDENT)
                .wishlist("wishlist")
                .purchasedCourse("1,2,3")
                .provider(AccountProviderEnum.LOCAL)
                .status(AccountStatusEnum.APPROVED)
                .createAt(LocalDateTime.now())
                .isDeleted(false)
                .isInstructorVerify(false)
                .build();

        // Setup Order1
        order1 = new Orderr();
        order1.setId(1);
        order1.setAccount(account);
        order1.setTotalPrice(orderRequestDTO.getTotalPrice());
        order1.setCreateAt(LocalDateTime.now());
        order1.setCoursesList(orderRequestDTO.getCourses());
        order1.setPaymentMethod(orderRequestDTO.getPaymentMethod());

        // Setup Order2
        order2 = new Orderr();
        order2.setId(2);
        order2.setAccount(account);
        order2.setTotalPrice(orderRequestDTO.getTotalPrice());
        order2.setCreateAt(LocalDateTime.now());
        order2.setCoursesList(orderRequestDTO.getCourses());
        order2.setPaymentMethod(orderRequestDTO.getPaymentMethod());

        // Setup OrderResponseDTO
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setId(1);

        orderResponseDTO.setTotalPrice(orderRequestDTO.getTotalPrice());
        orderResponseDTO.setCreateAt(LocalDateTime.now());
        orderResponseDTO.setCourses("1,2,3");
        orderResponseDTO.setPaymentMethod(PaymentMethodEnum.WALLET);

// Mock the behavior of jwtService.extractSubject(token)
        when(jwtService.extractSubject(token)).thenReturn("mocked_token_string");
        // Setup GenericResponse
        GenericResponse<OrderResponseDTO> genericResponse = new GenericResponse<>("Order created successfully", null, 201, orderResponseDTO);


        ReturnOrderPaymentDTO mockReturnOrderPaymentDTO = new ReturnOrderPaymentDTO();
        mockReturnOrderPaymentDTO.setAccount("test@example.com");
        mockReturnOrderPaymentDTO.setCourseId("1,2,3");
        mockReturnOrderPaymentDTO.setOrderId(123);

        // Mock courseService.getCourseById to return a valid Course object
        Course course = new Course();
        course.setId(1);
        course.setPrice(new BigDecimal("50.00"));
        when(courseService.getCourseById(1)).thenReturn(course);
        when(courseService.getCourseById(2)).thenReturn(course);
        when(courseService.getCourseById(3)).thenReturn(course);
        // Mock các phương thức khác tương tự như đã làm
        // Mock courseService.getCourseById để ném ra CourseException cho Course id 5 và 6
        Mockito.when(courseService.getCourseById(5)).thenThrow(new CourseException("The course with id 5 doesn't exist", ErrorCode.COURSE_NOT_FOUND));
        Mockito.when(courseService.getCourseById(6)).thenThrow(new CourseException("The course with id 6 doesn't exist", ErrorCode.COURSE_NOT_FOUND));

//
        //Mocking account repository to return the account
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        // Mock KafkaTemplate to return null when send is called
        when(orderKafkaTemplate.send(anyString(), any(OrderResponseDTO.class))).thenReturn(null);


        // Mock order repository to save and return the order
        when(orderRepository.save(any(Orderr.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }
//////
    //  ------------ Create Order --------------


    @Test
    public void testCreateOrder_Exception() {
        // Mock SecurityContext and Authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(account);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mocking the scenario where an unexpected exception occurs
        when(accountRepository.findById(anyInt())).thenThrow(new RuntimeException("Unexpected error"));

        // Calling the method under test
        GenericResponse<OrderResponseDTO> response = orderService.createOrder(orderRequestDTO);

        // Assertions to verify the response
        assertEquals("Failed to create order", response.getMessage());
        assertEquals("Unexpected error", response.getError());
        assertEquals(1008, response.getCode());
        // assertNull(response.getData()); // Ensuring data is null as expected
    }

    @Test
    public void testCreateOrder_InvalidPaymentMethod() {
        // Mock SecurityContext and Authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(account);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mocking account repository to return the account
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        // Mocking courseService to throw OrderException when payment method is null
        when(courseService.getCourseById(anyInt())).thenThrow(new OrderException("Invalid payment method", ErrorCode.INVALID_INPUT));

        // Calling the method under test
        GenericResponse<OrderResponseDTO> response = orderService.createOrder(orderRequestDTO);

        // Assertions to verify the response
        assertEquals("Failed to create order", response.getMessage());
        assertEquals("Invalid payment method", response.getError());
        assertEquals(ErrorCode.INVALID_INPUT.getCode(), response.getCode());
        assertNull(response.getData()); // Ensuring data is null as expected
    }


    @Test
    public void testCreateOrder_CourseNotFound() {
        // Given: Thiết lập dữ liệu đầu vào cho OrderRequestDTO
        // Mock SecurityContext and Authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(account);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        orderRequestDTO = new OrderRequestDTO();

        orderRequestDTO.setCourses(Arrays.asList(5, 6));
        orderRequestDTO.setPaymentMethod(PaymentMethodEnum.PAYOS);


        // Mock accountRepository.findById để trả về account
        Mockito.when(accountRepository.findById(1)).thenReturn(Optional.of(account));

        // When: Gọi hàm createOrder
        GenericResponse<OrderResponseDTO> response = orderService.createOrder(orderRequestDTO);

        // Then: Kiểm tra phản hồi của createOrder
        assertNotNull(response);
        assertEquals("Failed to create order", response.getMessage());
        assertEquals("The course with id 5 doesn't exist", response.getError());
        assertEquals(ErrorCode.INVALID_INPUT.getCode(), response.getCode());
    }

    @Test
    public void testCreateOrder_Success() {
        // Mock SecurityContext and Authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(account);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Gọi hàm createOrder
        GenericResponse<OrderResponseDTO> response = orderService.createOrder(orderRequestDTO);

        // Kiểm tra phản hồi của createOrder
        assertNotNull(response);
        assertEquals("Order created successfully", response.getMessage());
        assertNull(response.getError());
        assertEquals(201, response.getCode());

        // Kiểm tra giá trị totalPrice
        OrderResponseDTO responseDTO = response.getData();
        assertNotNull(responseDTO);
        assertEquals(new BigDecimal("150.00"), responseDTO.getTotalPrice());  // 3 courses * 50.00 each
        assertEquals(OrderStatusEnum.PENDING, responseDTO.getStatus());

        assertEquals("1,2,3", responseDTO.getCourses());
        assertEquals(PaymentMethodEnum.PAYOS, responseDTO.getPaymentMethod());
    }


    @Test
    public void testCreateOrder_CalculateTotalPrice() {
        // Mock SecurityContext and Authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(account);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Gọi hàm createOrder
        GenericResponse<OrderResponseDTO> response = orderService.createOrder(orderRequestDTO);

        // Kiểm tra phản hồi của createOrder
        assertNotNull(response);
        assertEquals("Order created successfully", response.getMessage());
        assertNull(response.getError());
        assertEquals(201, response.getCode());

        // Kiểm tra giá trị totalPrice
        OrderResponseDTO responseDTO = response.getData();
        assertNotNull(responseDTO);
        assertEquals(new BigDecimal("150.00"), responseDTO.getTotalPrice());  // 3 courses * 50.00 each

    }

    //
    @Test
    public void testCreateOrder_WithWalletPaymentMethod_Success() {
        // Given: Thiết lập dữ liệu đầu vào cho OrderRequestDTO và các mock cần thiết
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(account);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);


        // Mock walletService.buyCourseByWallet để trả về một response thành công
        BuyCourseResponse buyCourseResponse = new BuyCourseResponse("Course purchased successfully!", null, "1,2,3", new BigDecimal("150.00"), new BigDecimal("50.00"));
        ResponseEntity<BuyCourseResponse> buyResponseEntity = ResponseEntity.ok(buyCourseResponse);
        Mockito.when(walletService.buyCourseByWallet(anyInt(), anyString(), any(BigDecimal.class)))
                .thenReturn(buyResponseEntity);

        // Mock các phương thức khác như đã làm trước đó (courseService.getCourseById, accountRepository.findById, ...)

        // When: Gọi hàm createOrder
        GenericResponse<OrderResponseDTO> response = orderService.createOrder(orderRequestDTO);

        // Then: Kiểm tra phản hồi của createOrder
        assertNotNull(response);
        assertEquals("Order created successfully", response.getMessage());
        assertNull(response.getError());
        assertEquals(201, response.getCode());

        // Kiểm tra các giá trị trả về của OrderResponseDTO
        OrderResponseDTO responseDTO = response.getData();
        assertNotNull(responseDTO);
        assertEquals(new BigDecimal("150.00"), responseDTO.getTotalPrice());
        assertEquals(OrderStatusEnum.PENDING, responseDTO.getStatus());
        assertEquals("1,2,3", responseDTO.getCourses());
        assertEquals(PaymentMethodEnum.PAYOS, responseDTO.getPaymentMethod());
    }

    @Test
    public void testCreateOrder_WalletPaymentMethod_Failed() {
        // Given: Thiết lập dữ liệu đầu vào cho OrderRequestDTO và các mock cần thiết
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(account);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Thiết lập OrderRequestDTO

        orderRequestDTO.setTotalPrice(new BigDecimal("250.00"));
        orderRequestDTO.setCourses(Arrays.asList(1, 2, 3));
        orderRequestDTO.setPaymentMethod(PaymentMethodEnum.WALLET);

        // Mock walletService.buyCourseByWallet để trả về một response không thành công
        BuyCourseResponse buyCourseResponse = new BuyCourseResponse("Course purchase failed", "Insufficient funds", "1,2,3", new BigDecimal("250.00"), null);
        ResponseEntity<BuyCourseResponse> buyResponseEntity = new ResponseEntity<>(buyCourseResponse, HttpStatus.BAD_REQUEST);
        Mockito.when(walletService.buyCourseByWallet(anyInt(), anyString(), any(BigDecimal.class)))
                .thenReturn(buyResponseEntity);

        // Mock các phương thức khác như đã làm trước đó (courseService.getCourseById, accountRepository.findById, ...)

        // When: Gọi hàm createOrder
        GenericResponse<OrderResponseDTO> response = orderService.createOrder(orderRequestDTO);

        // Then: Kiểm tra phản hồi của createOrder
        assertNotNull(response);
        assertEquals("Failed to create order", response.getMessage());
        assertEquals("Insufficient funds", response.getError());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getCode());
        assertNull(response.getData());  // Không có dữ liệu trả về khi thất bại

        // Kiểm tra các giá trị còn lại có phù hợp không?
    }


    // -------------------- Get Order ------------------
    @Test
    public void testGetAllOrders_Success() {
        List<Orderr> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);
        when(orderRepository.findAll()).thenReturn(orders);

        List<OrderResponseDTO> result = orderService.getAllOrders();

        assertEquals(2, result.size());
        assertEquals(order1.getId(), result.get(0).getId());
        assertEquals(order2.getId(), result.get(1).getId());
    }

    @Test
    void testGetAllOrders_ExceptionThrown() {
        // Given
        when(orderRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        OrderException thrown = assertThrows(OrderException.class, () -> {
            orderService.getAllOrders();
        });

        // Assert
        assertEquals("Failed to retrieve orders", thrown.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, thrown.getErrorCode());
    }

    @Test
    public void testGetAllOrders_Empty() {
        List<Orderr> orders = new ArrayList<>();
        when(orderRepository.findAll()).thenReturn(orders);

        List<OrderResponseDTO> result = orderService.getAllOrders();

        assertEquals(0, result.size());
    }


    //-------------------------- Get Order By Id -----------------------------
    @Test
    public void testGetOrderById_ExceptionThrown() {
        // Given
        Integer orderId = 1;
        when(orderRepository.findById(orderId)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        OrderException thrown = assertThrows(OrderException.class, () -> {
            orderService.getOrderById(orderId);
        });

        // Assert
        assertEquals("Failed to retrieve order", thrown.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, thrown.getErrorCode());
    }


    @Test
    public void testGetOrderById_NotFound() {
        when(orderRepository.findById(999)).thenReturn(Optional.empty());

        OrderException exception = assertThrows(OrderException.class, () -> orderService.getOrderById(999));

        assertEquals(ErrorCode.COURSE_NOT_FOUND, exception.getErrorCode());
        assertEquals("Order not found with id: 999", exception.getMessage());
    }

    @Test
    public void testGetOrderById_OrderIsDeleted() {
        // Given
        Integer orderId = 1;
        Orderr order = new Orderr();
        order.setId(orderId);
        order.setIsDeleted(true);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // When & Then
        OrderException thrown = assertThrows(OrderException.class, () -> {
            orderService.getOrderById(orderId);
        });

        // Assert
        assertEquals("This order has been deleted", thrown.getMessage());
        assertEquals(ErrorCode.ORDER_IS_DELETED, thrown.getErrorCode());
    }

    // -------------------- Validate Purchased Courses ------------------
    @Test
    public void testHasPurchasedCoursesBefore_NoPurchasedCourses() {
        List<Integer> courses = List.of(4, 5);

        when(orderRepository.findByAccountIdAndStatus(1, OrderStatusEnum.SUCCESS)).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> orderService.hasPurchasedCoursesBefore(1, courses));
    }

    @Test
    public void testHasPurchasedCoursesBefore_CourseAlreadyPurchased() {
        List<Integer> courses = List.of(1, 2, 3);

        // Giả sử order1 chứa các khóa học đã mua với ID là 1 và 2
        order1.setCourses("1,2");

        List<Orderr> orders = new ArrayList<>();
        orders.add(order1);
        when(orderRepository.findByAccountIdAndStatus(1, OrderStatusEnum.SUCCESS)).thenReturn(orders);

        // Khi truy vấn với accountId 1 và trạng thái SUCCESS, trả về danh sách đơn hàng đã mua
        when(orderRepository.findByAccountIdAndStatusAndIsDeleted(1, OrderStatusEnum.SUCCESS, false)).thenReturn(orders);

        // Gọi phương thức và kiểm tra ngoại lệ
        ValidationException exception = assertThrows(ValidationException.class, () -> orderService.hasPurchasedCoursesBefore(1, courses));

        // Kiểm tra mã lỗi và thông báo ngoại lệ
        assertEquals(ErrorCode.INVALID_INPUT, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("The course 1 has already been purchased."));
    }


    // ------------ Update Order --------------
    @Test
    public void testUpdateOrder_Success() {

        // Given: Thiết lập dữ liệu đầu vào cho OrderRequestDTO và các mock cần thiết
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(account);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(orderRepository.findById(1)).thenReturn(Optional.of(order1));
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(orderRepository.save(any(Orderr.class))).thenReturn(order1);

        OrderResponseDTO result = orderService.updateOrder(1, orderRequestUpdateDTO);

        assertNotNull(result);
        assertEquals(order1.getId(), result.getId());

        assertEquals(order1.getTotalPrice(), result.getTotalPrice());
        assertEquals(order1.getStatus(), result.getStatus());
    }

    @Test
    public void testUpdateOrder_OrderNotFound() {

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(account);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(orderRepository.findById(999)).thenReturn(Optional.empty());
        OrderException exception = assertThrows(OrderException.class, () -> orderService.updateOrder(999, orderRequestUpdateDTO));

        assertEquals(ErrorCode.ORDER_NOT_FOUND, exception.getErrorCode());
        assertEquals("Order not found with id: 999", exception.getMessage());
    }

    @Test
    public void testUpdateOrder_InternalServerError() {

        // Given: Thiết lập dữ liệu đầu vào cho OrderRequestDTO và các mock cần thiết
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(account);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(orderRepository.findById(1)).thenReturn(Optional.of(order1));
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        doThrow(new RuntimeException("Unexpected error")).when(orderRepository).save(any(Orderr.class));

        OrderException exception = assertThrows(OrderException.class, () -> orderService.updateOrder(1, orderRequestUpdateDTO));

        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
        assertEquals("Failed to update order", exception.getMessage());
    }


    // ------------ Delete Order --------------
    @Test
    public void testDeleteOrder_Success() {
        when(orderRepository.existsById(1)).thenReturn(true);

        assertDoesNotThrow(() -> orderService.deleteOrder(1));
    }

    @Test
    public void testDeleteOrder_OrderNotFound() {
        when(orderRepository.existsById(999)).thenReturn(false);

        OrderException exception = assertThrows(OrderException.class, () -> orderService.deleteOrder(999));

        assertEquals(ErrorCode.ORDER_NOT_FOUND, exception.getErrorCode());
        assertEquals("Order not found with id: 999", exception.getMessage());
    }


    @Test
    public void testDeleteOrder_InternalServerError() {
        when(orderRepository.existsById(1)).thenReturn(true);
        doThrow(new RuntimeException("Unexpected error")).when(orderRepository).deleteById(1);

        OrderException exception = assertThrows(OrderException.class, () -> orderService.deleteOrder(1));

        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
        assertEquals("Failed to delete order", exception.getMessage());
    }


    // ------------ Change IsDeleted Order --------------
    @Test
    public void testChangeIsDeleteOrder_Success() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(order1));

        assertDoesNotThrow(() -> orderService.changeIsDeleteOrder(1, true));

        assertTrue(order1.getIsDeleted());
    }

    @Test
    public void testChangeIsDeleteOrder_OrderNotFound() {
        when(orderRepository.findById(999)).thenReturn(Optional.empty());

        OrderException exception = assertThrows(OrderException.class, () -> orderService.changeIsDeleteOrder(999, true));

        assertEquals(ErrorCode.ORDER_NOT_FOUND, exception.getErrorCode());
        assertEquals("Order not found with id: 999", exception.getMessage());
    }

    @Test
    public void testChangeIsDeleteOrder_InternalServerError() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(order1));
        doThrow(new RuntimeException("Unexpected error")).when(orderRepository).save(any(Orderr.class));

        OrderException exception = assertThrows(OrderException.class, () -> orderService.changeIsDeleteOrder(1, true));

        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
        assertEquals("Failed to delete order", exception.getMessage());
    }

    // ------------ Get Orders By Status --------------
//    @Test
//    public void testGetOrdersByStatus_Success() {
//        List<Orderr> orders = Arrays.asList(order1, order2);
//        when(orderRepository.findByStatus(OrderStatusEnum.PENDING)).thenReturn(orders);
//
//        List<OrderResponseDTO> result = orderService.getOrdersByStatus(OrderStatusEnum.PENDING);
//
//        assertEquals(0, result.size());
//        assertEquals(order1.getId(), result.get(0).getId());
//        assertEquals(order2.getId(), result.get(1).getId());
//    }

    @Test
    public void testGetOrdersByStatus_Empty() {
        List<Orderr> orders = new ArrayList<>();
        when(orderRepository.findByStatus(OrderStatusEnum.PENDING)).thenReturn(orders);

        List<OrderResponseDTO> result = orderService.getOrdersByStatus(OrderStatusEnum.PENDING);

        assertEquals(0, result.size());
    }

    @Test
    void testGetOrdersByStatus_ExceptionThrown() {
        // Given
        OrderStatusEnum status = OrderStatusEnum.SUCCESS;
        when(orderRepository.findByStatusAndIsDeleted(status, false)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        AccountException thrown = assertThrows(AccountException.class, () -> {
            orderService.getOrdersByStatus(status);
        });

        // Assert
        assertEquals("Failed to retrieve order by status", thrown.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, thrown.getErrorCode());
    }


    // ------------ Get Orders By Account Id --------------
    @Test
    public void testGetOrdersByAccountId_Success() {
        List<Orderr> orders = Arrays.asList(order1, order2);
        when(orderRepository.findByAccountIdAndIsDeleted(1, false)).thenReturn(orders);

        List<OrderResponseDTO> result = orderService.getOrdersByAccountId(1);

        assertEquals(2, result.size());
        assertEquals(order1.getId(), result.get(0).getId());
        assertEquals(order2.getId(), result.get(1).getId());
    }


    @Test
    public void testGetOrdersByAccountId_Empty() {
        List<Orderr> orders = new ArrayList<>();
        when(orderRepository.findByAccountId(1)).thenReturn(orders);

        List<OrderResponseDTO> result = orderService.getOrdersByAccountId(1);

        assertEquals(0, result.size());
    }

    @Test
    void testGetOrdersByAccountId_ExceptionThrown() {
        // Given
        Integer accountId = 1;
        when(orderRepository.findByAccountIdAndIsDeleted(accountId, false)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        AccountException thrown = assertThrows(AccountException.class, () -> {
            orderService.getOrdersByAccountId(accountId);
        });

        // Assert
        assertEquals("Failed to retrieve order by AccountId", thrown.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, thrown.getErrorCode());
    }


    // ------------ Get Orders By Account Id And Status --------------
    @Test
    public void testGetOrdersByAccountIdAndStatus_Success() {
        List<Orderr> orders = Arrays.asList(order1, order2);
        when(orderRepository.findByAccountIdAndStatusAndIsDeleted(1, OrderStatusEnum.PENDING, false)).thenReturn(orders);

        List<OrderResponseDTO> result = orderService.getOrdersByAccountIdAndStatus(1, OrderStatusEnum.PENDING);

        assertEquals(2, result.size());
        assertEquals(order1.getId(), result.get(0).getId());
        assertEquals(order2.getId(), result.get(1).getId());
    }


    @Test
    public void testGetOrdersByAccountIdAndStatus_Empty() {
        List<Orderr> orders = new ArrayList<>();
        when(orderRepository.findByAccountIdAndStatus(1, OrderStatusEnum.PENDING)).thenReturn(orders);

        List<OrderResponseDTO> result = orderService.getOrdersByAccountIdAndStatus(1, OrderStatusEnum.PENDING);

        assertEquals(0, result.size());
    }

    @Test
    void testGetOrdersByAccountIdAndStatus_ExceptionThrown() {
        // Given
        Integer accountId = 1;
        OrderStatusEnum status = OrderStatusEnum.SUCCESS;
        when(orderRepository.findByAccountIdAndStatusAndIsDeleted(accountId, status, false)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        AccountException thrown = assertThrows(AccountException.class, () -> {
            orderService.getOrdersByAccountIdAndStatus(accountId, status);
        });

        // Assert
        assertEquals("Failed to retrieve order by AccountId", thrown.getMessage());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, thrown.getErrorCode());
    }


    // ------------ Get All Unique Courses --------------
    @Test
    public void testGetAllUniqueCourses_Success() {
        when(orderRepository.findByAccountIdAndStatusAndIsDeleted(1, OrderStatusEnum.SUCCESS, false)).thenReturn(Arrays.asList(order1, order2));

        Set<String> result = orderService.getAllUniqueCourses(1);

        assertEquals(3, result.size());
        assertTrue(result.contains("1"));
        assertTrue(result.contains("2"));
        assertTrue(result.contains("3"));
    }


    @Test
    void testGetAllUniqueCourses_ExceptionThrown() {
        // Given
        Integer accountId = 1;
        when(orderService.getOrdersByAccountIdAndStatus(accountId, OrderStatusEnum.SUCCESS))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        CourseAppException thrown = assertThrows(CourseAppException.class, () -> {
            orderService.getAllUniqueCourses(accountId);
        });

        // Assert
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, thrown.getErrorCode());
    }

    @Test
    public void testGetAllUniqueCourses_Empty() {
        when(orderRepository.findByAccountIdAndStatus(1, OrderStatusEnum.SUCCESS)).thenReturn(new ArrayList<>());

        Set<String> result = orderService.getAllUniqueCourses(1);

        assertEquals(0, result.size());
    }

    // ------------ Get Total Orders By Account --------------
    @Test
    public void testGetTotalOrdersByAccount_Success() {
        when(orderRepository.findByAccountIdAndStatusAndIsDeleted(1, OrderStatusEnum.SUCCESS, false)).thenReturn(Arrays.asList(order1, order2));

        BigDecimal result = orderService.getTotalOrdersByAccount(1);

        assertEquals(new BigDecimal("200.00"), result);
    }


    @Test
    public void testGetTotalOrdersByAccount_Empty() {
        when(orderRepository.findByAccountIdAndStatus(1, OrderStatusEnum.SUCCESS)).thenReturn(new ArrayList<>());

        BigDecimal result = orderService.getTotalOrdersByAccount(1);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testGetTotalOrdersByAccount_ExceptionThrown() {
        // Given
        Integer accountId = 1;
        when(orderService.getOrdersByAccountIdAndStatus(accountId, OrderStatusEnum.SUCCESS))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        CourseAppException thrown = assertThrows(CourseAppException.class, () -> {
            orderService.getTotalOrdersByAccount(accountId);
        });

        // Assert
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, thrown.getErrorCode());
    }


    // ------------ Handle Return Order --------------

    @Test
    public void testHandleReturnOrder_UnableToExtractToken() {
        String invalidToken = "invalid_token_here";

        // Expecting UnableToExtractTokenForReturnOrder exception to be thrown
        assertThrows(UnableToExtractTokenForReturnOrder.class, () -> {
            orderService.handleReturnOrder(invalidToken, false, "PAID");
        });
    }

    @Test
    public void testHandleReturnOrder_FailedToPay() {
        // Replace with a valid token that can be successfully extracted
        String validToken = "valid_token_here";

        // Expecting FailedToPayException exception to be thrown
        assertThrows(FailedToPayException.class, () -> {
            orderService.handleReturnOrder(validToken, true, "CANCELLED");
        });
    }

    @Test
    public void testHandleReturnOrder_UnableToGetInformationFromExtractedOrder() throws JsonProcessingException {


        String tokenString = "{\"account\":\"test@example.com\",\"courseId\":\"1,2,3\",\"orderId\":123}";
        Mockito.lenient().when(jwtService.extractSubject(token)).thenReturn(tokenString);

        // Mock objectMapper behavior
        ReturnOrderPaymentDTO returnOrderPaymentDTO = new ReturnOrderPaymentDTO();
        returnOrderPaymentDTO.setAccount("test@example.com");
        returnOrderPaymentDTO.setCourseId("1,2,3");
        returnOrderPaymentDTO.setOrderId(123);
        Mockito.lenient().when(objectMapper.readValue(eq(tokenString), eq(ReturnOrderPaymentDTO.class)))
                .thenReturn(returnOrderPaymentDTO);

        // Mock account retrieval to throw an exception
        when(accountService.getAccountByEmail(returnOrderPaymentDTO.getAccount()))
                .thenThrow(new RuntimeException("Account retrieval failed"));

        // Mock courseIds retrieval
        List<Integer> mockCourseIds = List.of(1, 2, 3);
        Mockito.lenient().when(stringUtils.stringToList(returnOrderPaymentDTO.getCourseId()))
                .thenReturn(mockCourseIds);

        // Mock order retrieval
        Orderr mockOrder = new Orderr();
        Mockito.lenient().when(orderRepository.findById(returnOrderPaymentDTO.getOrderId()))
                .thenReturn(java.util.Optional.of(mockOrder));

        // Verify exception handling for UnableToGetInformationFromExtractedOrder
        assertThrows(UnableToGetInformationFromExtractedOrder.class, () -> {
            orderService.handleReturnOrder(token, false, "PAID");
        });
    }

    @Test
    public void testHandleReturnOrder_UnableToGetInformationFromExtractedOrder_DueToOrderNotFound() throws Exception {
        // Arrange
        String token = "some_token";
        ReturnOrderPaymentDTO mockReturnOrderPaymentDTO = new ReturnOrderPaymentDTO();
        mockReturnOrderPaymentDTO.setAccount("test@example.com");
        mockReturnOrderPaymentDTO.setCourseId("1,2,3");
        mockReturnOrderPaymentDTO.setOrderId(123);

        // Mock JWTService behavior
        when(jwtService.extractSubject(token)).thenReturn("{\"account\": \"test@example.com\", \"courseId\": \"1,2,3\", \"orderId\": 123}");

        // Mock ObjectMapper behavior
        String tokenString = "{\"account\": \"test@example.com\", \"courseId\": \"1,2,3\", \"orderId\": 123}";
        when(objectMapper.readValue(anyString(), eq(ReturnOrderPaymentDTO.class))).thenReturn(mockReturnOrderPaymentDTO);

        // Mock account retrieval
        Account mockAccount = new Account();
        when(accountService.getAccountByEmail(mockReturnOrderPaymentDTO.getAccount())).thenReturn(mockAccount);

        // Mock courseIds retrieval
        List<Integer> mockCourseIds = List.of(1, 2, 3);
        when(stringUtils.stringToList(mockReturnOrderPaymentDTO.getCourseId())).thenReturn(mockCourseIds);

        // Mock order retrieval to throw OrderException
        when(orderRepository.findById(mockReturnOrderPaymentDTO.getOrderId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UnableToGetInformationFromExtractedOrder.class, () -> {
            orderService.handleReturnOrder(token, false, "PAID");
        });

        // Verify
        verify(orderRepository, times(1)).findById(mockReturnOrderPaymentDTO.getOrderId());
    }

    // ------------------ Add User To Course And Vice Versa ---------------
    @Test
    void testAddUserToCourseAndViceVersa() throws Exception {
        List<Integer> courseIds = Arrays.asList(1, 2, 3);
        Orderr savedOrder = order1;

        // Mock Account
        Account instructorAccount = Account.builder()
                .id(2)
                .name("Instructor Name")
                .email("instructor@example.com")
                .build();

        // Mock Course with Account
        Course course1 = new Course();
        course1.setId(1);
        course1.setPrice(new BigDecimal("50.00"));
        course1.setAccount(instructorAccount);

        Course course2 = new Course();
        course2.setId(2);
        course2.setPrice(new BigDecimal("50.00"));
        course2.setAccount(instructorAccount);

        Course course3 = new Course();
        course3.setId(3);
        course3.setPrice(new BigDecimal("50.00"));
        course3.setAccount(instructorAccount);

        when(courseService.getCourseById(1)).thenReturn(course1);
        when(courseService.getCourseById(2)).thenReturn(course2);
        when(courseService.getCourseById(3)).thenReturn(course3);

        // Mock wallet service addPriceToWallet
        doAnswer(invocation -> null).when(walletService).addPriceToWallet(anyInt(), any(BigDecimal.class));

        // Mock save course and account
        doAnswer(invocation -> null).when(courseService).saveCourse(any(Course.class));
        doAnswer(invocation -> null).when(accountService).saveAccountChanges(any(Account.class));

        // Mock transaction service
        TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO();
        transactionResponseDTO.setId(1);
        when(transactionService.createTransaction(any(TransactionRequestDTO.class))).thenReturn(transactionResponseDTO);

        // Mock wallet service getWalletByAccountId
        Wallet wallet = new Wallet();
        wallet.setId(1);
        when(walletService.getWalletByAccountId(anyInt())).thenReturn(wallet);

        // Mock wallet service createWalletLog
        doAnswer(invocation -> null).when(walletService).createWalletLog(any(Wallet.class), any(BigDecimal.class), anyInt(), any(WalletLogTypeEnum.class));

        // Call the method to test
        orderService.addUserToCourseAndViceVersa(account, courseIds, savedOrder);

        // Verify interactions
        verify(courseService, times(1)).getCourseById(1);
        verify(courseService, times(1)).getCourseById(2);
        verify(courseService, times(1)).getCourseById(3);

        verify(walletService, times(3)).addPriceToWallet(eq(2), eq(new BigDecimal("50.00"))); // Instructor ID is 2

        verify(courseService, times(3)).saveCourse(any(Course.class));
        verify(accountService, times(3)).saveAccountChanges(any(Account.class));

        // Adjust this line based on actual calls
        verify(transactionService, times(6)).createTransaction(any(TransactionRequestDTO.class));

        // Verify getWalletByAccountId based on actual number of calls
        verify(walletService, times(6)).getWalletByAccountId(anyInt());

        // Verify createWalletLog based on actual number of calls
        verify(walletService, times(6)).createWalletLog(any(Wallet.class), any(BigDecimal.class), anyInt(), any(WalletLogTypeEnum.class));
    }

    @Test
    void testAddUserToCourseAndViceVersa_walletStudentNotFound() throws Exception {
        // Setup data
        Account account = Account.builder()
                .id(1)
                .name("John Doe")
                .email("johndoe@example.com")
                .password("password")
                .phone("1234567890")
                .gender(AccountGenderEnum.MALE)
                .avatar("avatar.png")
                .role(AccountRoleEnum.STUDENT)
                .wishlist("wishlist")
                .purchasedCourse("1,2,3")
                .provider(AccountProviderEnum.LOCAL)
                .status(AccountStatusEnum.APPROVED)
                .createAt(LocalDateTime.now())
                .isDeleted(false)
                .isInstructorVerify(false)
                .build();

        List<Integer> courseIds = Arrays.asList(1, 2, 3);

        Orderr savedOrder = new Orderr();
        savedOrder.setId(1);
        savedOrder.setAccount(account);
        savedOrder.setTotalPrice(new BigDecimal("100.00"));
        savedOrder.setCreateAt(LocalDateTime.now());
        savedOrder.setCoursesList(courseIds);
        savedOrder.setPaymentMethod(PaymentMethodEnum.PAYOS);

        // Mock course
        Course course = new Course();
        course.setId(1);
        course.setPrice(new BigDecimal("50.00"));
        course.setAccount(account);

        // Mock walletService behavior
        Wallet walletStudent = new Wallet();
        walletStudent.setId(null);  // Set ID to null to trigger the exception

        // Mocking
        when(walletService.getWalletByAccountId(account.getId())).thenReturn(walletStudent);
        when(courseService.getCourseById(1)).thenReturn(course);
        when(courseService.getCourseById(2)).thenReturn(course);
        when(courseService.getCourseById(3)).thenReturn(course);

        // Test exception
        assertThrows(WalletAppException.class, () -> {
            orderService.addUserToCourseAndViceVersa(account, courseIds, savedOrder);
        });
    }

    @Test
    public void testGetTransactionRequestDTO() {
        // Given: Thiết lập các đối tượng Account và Orderr
        Account account = new Account();
        account.setId(1);

        Orderr order = new Orderr();
        order.setId(100);
        order.setTotalPrice(new BigDecimal("200.00"));
        order.setPaymentMethod(PaymentMethodEnum.WALLET);
        order.setStatus(OrderStatusEnum.SUCCESS);

        // When: Gọi hàm getTransactionRequestDTO
        TransactionRequestDTO transactionRequestDTO = OrderService.getTransactionRequestDTO(account, order);

        // Then: Kiểm tra các thuộc tính của TransactionRequestDTO
        assertEquals(account.getId(), transactionRequestDTO.getAccountId());
        assertEquals(order.getId(), transactionRequestDTO.getOrderId());
        assertEquals(order.getTotalPrice(), transactionRequestDTO.getTotalPrice());
        assertEquals(order.getPaymentMethod(), transactionRequestDTO.getPaymentMethod());
        assertEquals(TransactionStatusEnum.SUCCESS, transactionRequestDTO.getStatus());

    }
    //------------------------------- Get Daily Earnings By AccountId --------------------

    @Test
    void testGetDailyEarningsByAccountId_AccountNotFound() {
        // Setup data
        when(accountUtils.getCurrentAccount()).thenReturn(new Account());
        when(accountRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Test exception
        assertThrows(AccountException.class, () -> {
            orderService.getDailyEarningsByAccountId();
        });
    }

    @Test
    void testGetDailyEarningsByAccountId_NotInstructor() {
        // Setup data
        Account nonInstructor = new Account();
        nonInstructor.setId(1);
        nonInstructor.setRole(AccountRoleEnum.STUDENT);  // Not an instructor

        when(accountUtils.getCurrentAccount()).thenReturn(nonInstructor);
        when(accountRepository.findById(nonInstructor.getId())).thenReturn(Optional.of(nonInstructor));

        // Test exception
        assertThrows(AccountException.class, () -> {
            orderService.getDailyEarningsByAccountId();
        });
    }

    @Test
    void testGetDailyEarningsByAccountId_AccountIsDeleted() {
        // Setup data
        Account instructor = new Account();
        instructor.setId(1);
        instructor.setRole(AccountRoleEnum.INSTRUCTOR);
        instructor.setIsDeleted(true);  // Tài khoản bị xóa

        when(accountUtils.getCurrentAccount()).thenReturn(instructor);
        when(accountRepository.findById(instructor.getId())).thenReturn(Optional.of(instructor));

        // Test exception
        assertThrows(AccountException.class, () -> {
            orderService.getDailyEarningsByAccountId();
        });
    }

    @Test
    void testGetDailyEarningsByAccountId_NoCoursesFoundForOrder() {
        // Setup data
        Account instructor = new Account();
        instructor.setId(1);
        instructor.setRole(AccountRoleEnum.INSTRUCTOR);
        instructor.setIsDeleted(false);

        Orderr order = new Orderr();
        order.setId(1);
        order.setCoursesList(Collections.emptyList());  // Danh sách rỗng

        when(accountUtils.getCurrentAccount()).thenReturn(instructor);
        when(accountRepository.findById(instructor.getId())).thenReturn(Optional.of(instructor));
        when(orderRepository.findAllByStatusAndCreateAtBetweenAndIsDeleted(any(), any(), any(), anyBoolean())).thenReturn(Collections.singletonList(order));

        // Test exception
        assertThrows(CourseException.class, () -> {
            orderService.getDailyEarningsByAccountId();
        });
    }

    @Test
    @WithMockUser(username = "instructor", roles = {"INSTRUCTOR"})
    void testGetDailyEarningsByAccountId_ReturnsDailyEarnings() {
        // Setup data
        Account instructor = new Account();
        instructor.setId(1);
        instructor.setRole(AccountRoleEnum.INSTRUCTOR);
        instructor.setIsDeleted(false);

        Orderr order = new Orderr();
        order.setId(1);
        order.setCreateAt(LocalDateTime.now().minusDays(1));
        order.setCoursesList(Arrays.asList(1, 2));  // Danh sách các khóa học

        Course course1 = new Course();
        course1.setId(1);
        course1.setPrice(BigDecimal.valueOf(100));
        course1.setAccount(instructor);

        Course course2 = new Course();
        course2.setId(2);
        course2.setPrice(BigDecimal.valueOf(200));
        course2.setAccount(instructor);

        // Mock Authentication and SecurityContext
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(instructor);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Thiết lập mock cho các repository
        when(accountUtils.getCurrentAccount()).thenReturn(instructor);
        when(accountRepository.findById(instructor.getId())).thenReturn(Optional.of(instructor));
        when(orderRepository.findAllByStatusAndCreateAtBetweenAndIsDeleted(any(), any(), any(), anyBoolean())).thenReturn(Collections.singletonList(order));
        when(courseRepository.findByIdAndIsDeleted(1, false)).thenReturn(Optional.of(course1));
        when(courseRepository.findByIdAndIsDeleted(2, false)).thenReturn(Optional.of(course2));

        // Test method
        List<DailyEarningsResponse> earnings = orderService.getDailyEarningsByAccountId();

        // Assertions
        assertNotNull(earnings);
        assertEquals(1, earnings.size());
        DailyEarningsResponse response = earnings.get(0);
        assertEquals(order.getCreateAt().toLocalDate(), response.getDate());
        assertEquals(BigDecimal.valueOf(300), response.getTotalAmount());
        assertEquals(2, response.getTotalSaleInDay());
    }
    @Test
    @WithMockUser(username = "instructor", roles = {"INSTRUCTOR"})
    void testGetDailyEarningsByAccountId_ExceptionHandling() {
        // Setup dữ liệu và các mock
        Account instructor = new Account();
        instructor.setId(1);
        instructor.setRole(AccountRoleEnum.INSTRUCTOR);
        instructor.setIsDeleted(false);

        // Thiết lập mock cho các repository
        when(accountUtils.getCurrentAccount()).thenReturn(instructor);
        when(accountRepository.findById(instructor.getId())).thenReturn(Optional.of(instructor));

        // Mock một phương thức nào đó để ném ngoại lệ không mong muốn
        when(orderRepository.findAllByStatusAndCreateAtBetweenAndIsDeleted(any(), any(), any(), anyBoolean()))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Mock Authentication and SecurityContext
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(instructor);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Test method
        assertThrows(OrderException.class, () -> {
            orderService.getDailyEarningsByAccountId();
        }, "Expected OrderException to be thrown");

        // Verify that the correct exception is thrown
        try {
            orderService.getDailyEarningsByAccountId();
        } catch (OrderException e) {
            assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, e.getErrorCode());
            assertEquals("Unexpected error", e.getMessage());
        }
    }

}




