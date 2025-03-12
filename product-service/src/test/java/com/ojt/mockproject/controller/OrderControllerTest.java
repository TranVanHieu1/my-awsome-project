package com.ojt.mockproject.controller;

import com.ojt.mockproject.dto.Order.Requests.OrderRequestDTO;
import com.ojt.mockproject.dto.Order.Requests.OrderRequestUpdateDTO;
import com.ojt.mockproject.dto.Order.Responses.DailyEarningsResponse;
import com.ojt.mockproject.dto.Order.Responses.OrderResponseDTO;
import com.ojt.mockproject.dto.Order.Responses.GenericResponse;
import com.ojt.mockproject.entity.Enum.OrderStatusEnum;
import com.ojt.mockproject.entity.Enum.PaymentMethodEnum;
import com.ojt.mockproject.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private OrderRequestDTO orderRequestDTO;
    private OrderRequestUpdateDTO orderRequestUpdateDTO;
    private OrderResponseDTO orderResponseDTO;
    private GenericResponse<OrderResponseDTO> genericResponse;

    @BeforeEach
    public void setUp() {
        orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setTotalPrice(new BigDecimal("100.00"));
        orderRequestDTO.setCourses(Arrays.asList(1, 2, 3));
        orderRequestDTO.setPaymentMethod(PaymentMethodEnum.WALLET);

        orderRequestUpdateDTO = new OrderRequestUpdateDTO();
        orderRequestUpdateDTO.setCourses(Arrays.asList(1, 2, 3));
        orderRequestUpdateDTO.setIsDeleted(false);
        orderRequestUpdateDTO.setStatus(OrderStatusEnum.SUCCESS);
        orderRequestUpdateDTO.setPaymentMethod(PaymentMethodEnum.WALLET);

        orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setId(1);

        orderResponseDTO.setTotalPrice(orderRequestDTO.getTotalPrice());
        orderResponseDTO.setCreateAt(LocalDateTime.now());
       // orderResponseDTO.setAccountId(orderRequestDTO.getAccountId());
        orderResponseDTO.setCourses("1,2,3");
       // orderResponseDTO.setIsDeleted(orderRequestDTO.getIsDeleted());

        genericResponse = new GenericResponse<>("Success", null,201, orderResponseDTO);
    }

    @Test
    public void testGetAllOrders() {
        List<OrderResponseDTO> orders = Arrays.asList(orderResponseDTO);
        when(orderService.getAllOrders()).thenReturn(orders);

        ResponseEntity<List<OrderResponseDTO>> response = orderController.getAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orders, response.getBody());
    }

    @Test
    public void testGetOrderById() {
        Integer orderId = 1;
        when(orderService.getOrderById(orderId)).thenReturn(orderResponseDTO);

        ResponseEntity<OrderResponseDTO> response = orderController.getOrderById(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderResponseDTO, response.getBody());
    }
    @Test
    public void testCreateOrder() {
        when(orderService.createOrder(orderRequestDTO)).thenReturn(genericResponse);

        ResponseEntity<GenericResponse<OrderResponseDTO>> response = orderController.createOrder(orderRequestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(genericResponse, response.getBody());
        verify(orderService, times(1)).createOrder(orderRequestDTO);
    }
    @Test
    public void testUpdateOrder() {
        Integer orderId = 1;
        when(orderService.updateOrder(orderId, orderRequestUpdateDTO)).thenReturn(orderResponseDTO);

        ResponseEntity<OrderResponseDTO> response = orderController.updateOrder(orderId, orderRequestUpdateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderResponseDTO, response.getBody());
    }

    @Test
    public void testDeleteOrder() {
        Integer orderId = 1;
        ResponseEntity<String> response = orderController.deleteOrder(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Order with ID " + orderId + " has been deleted.", response.getBody());
        verify(orderService, times(1)).deleteOrder(orderId);
    }

    @Test
    public void testGetOrderByStatus() {
        OrderStatusEnum status = OrderStatusEnum.PENDING;
        List<OrderResponseDTO> orders = Arrays.asList(orderResponseDTO);
        when(orderService.getOrdersByStatus(status)).thenReturn(orders);

        ResponseEntity<List<OrderResponseDTO>> response = orderController.getOrderByStatus(status);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orders, response.getBody());
    }

    @Test
    public void testGetOrderByAccountId() {
        Integer accountId = 1;
        List<OrderResponseDTO> orders = Arrays.asList(orderResponseDTO);
        when(orderService.getOrdersByAccountId(accountId)).thenReturn(orders);

        ResponseEntity<List<OrderResponseDTO>> response = orderController.getOrderByAccountId(accountId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orders, response.getBody());
    }

    @Test
    public void testReturnOrder() throws Exception {
        String token = "token";
        String code = "code";
        boolean cancel = true;
        String status = "status";
        String orderCode = "orderCode";

        boolean result = orderController.returnOrder(token, code, cancel, status, orderCode);

        assertEquals(true, result);
        verify(orderService, times(1)).handleReturnOrder(token, cancel, status);
    }

    @Test
    public void testIsDeleteTrueOrder() {
        Integer orderId = 1;
        ResponseEntity<String> response = orderController.isDeleteTrueOrder(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Order with ID " + orderId + " has been deleted.", response.getBody());
        verify(orderService, times(1)).changeIsDeleteOrder(orderId, true);
    }

    @Test
    public void testIsDeleteFalseOrder() {
        Integer orderId = 1;
        ResponseEntity<String> response = orderController.isDeleteFalseOrder(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Order with ID " + orderId + " has been restored.", response.getBody());
        verify(orderService, times(1)).changeIsDeleteOrder(orderId, false);
    }

    @Test
    public void testGetAllUniqueCoursesByAccountID() {
        Integer accountId = 1;
        Set<String> uniqueCourses = Set.of("1", "2", "3");
        when(orderService.getAllUniqueCourses(accountId)).thenReturn(uniqueCourses);

        ResponseEntity<Set<String>> response = orderController.getAllUniqueCoursesByAccountID(accountId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(uniqueCourses, response.getBody());
    }

    @Test
    public void testGetTotalOrdersByAccount() {
        Integer accountId = 1;
        BigDecimal total = new BigDecimal("100.00");
        when(orderService.getTotalOrdersByAccount(accountId)).thenReturn(total);

        ResponseEntity<BigDecimal> response = orderController.getTotalOrdersByAccount(accountId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(total, response.getBody());
    }

    @Test
    public void testGetDailyEarnings() {
        // Thiết lập dữ liệu giả
        DailyEarningsResponse earningsResponse1 = new DailyEarningsResponse(
                LocalDate.now(),
                new BigDecimal("500.00"),
                10
        );

        DailyEarningsResponse earningsResponse2 = new DailyEarningsResponse(
                LocalDate.now().minusDays(1),
                new BigDecimal("300.00"),
                5
        );

        List<DailyEarningsResponse> dailyEarnings = Arrays.asList(earningsResponse1, earningsResponse2);

        // Mock phương thức getDailyEarningsByAccountId
        when(orderService.getDailyEarningsByAccountId()).thenReturn(dailyEarnings);

        // Gọi phương thức getDailyEarnings của controller
        List<DailyEarningsResponse> response = orderController.getDailyEarnings();

        // Xác nhận kết quả
        assertEquals(dailyEarnings, response);
    }



}
