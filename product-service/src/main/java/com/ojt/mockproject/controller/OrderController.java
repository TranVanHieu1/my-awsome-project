package com.ojt.mockproject.controller;

import com.ojt.mockproject.dto.Order.Requests.OrderRequestDTO;
import com.ojt.mockproject.dto.Order.Requests.OrderRequestUpdateDTO;
import com.ojt.mockproject.dto.Order.Responses.DailyEarningsResponse;
import com.ojt.mockproject.dto.Order.Responses.GenericResponse;
import com.ojt.mockproject.dto.Order.Responses.OrderResponseDTO;
import com.ojt.mockproject.entity.Enum.OrderStatusEnum;
import com.ojt.mockproject.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin("*")
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<OrderResponseDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Integer orderId) {
        OrderResponseDTO order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT')")
    @PostMapping
    public ResponseEntity<GenericResponse<OrderResponseDTO>> createOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        GenericResponse<OrderResponseDTO> response = orderService.createOrder(orderRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT', 'ADMIN')")
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> updateOrder(@PathVariable Integer orderId, @RequestBody OrderRequestUpdateDTO orderRequestDTO) {
        OrderResponseDTO updatedOrder = orderService.updateOrder(orderId, orderRequestDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Integer orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok("Order with ID " + orderId + " has been deleted.");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponseDTO>> getOrderByStatus(@PathVariable OrderStatusEnum status) {
        List<OrderResponseDTO> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT', 'ADMIN')")

    @GetMapping("/accountId/{accountId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrderByAccountId(@PathVariable Integer accountId) {
        List<OrderResponseDTO> orders = orderService.getOrdersByAccountId(accountId);
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT', 'ADMIN')")
    @GetMapping("/return")
    public boolean returnOrder(
            @RequestParam String token,
            @RequestParam String code,
            @RequestParam boolean cancel,
            @RequestParam String status,
            @RequestParam String orderCode) throws Exception {
        orderService.handleReturnOrder(token, cancel, status);
        return true;
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT', 'ADMIN')")
    @PostMapping("is-delete-true/{orderId}")
    public ResponseEntity<String> isDeleteTrueOrder(@PathVariable Integer orderId) {
        orderService.changeIsDeleteOrder(orderId,true);
        return ResponseEntity.ok("Order with ID " + orderId + " has been deleted.");
    }
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT', 'ADMIN')")
    @PostMapping("is-delete-false/{orderId}")
    public ResponseEntity<String> isDeleteFalseOrder(@PathVariable Integer orderId) {
        orderService.changeIsDeleteOrder(orderId,false);
        return ResponseEntity.ok("Order with ID " + orderId + " has been restored.");
    }

    //List of courses purchased for an account
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT', 'ADMIN')")
    @GetMapping("courses-by-account/{accountId}")
    public ResponseEntity<Set<String>> getAllUniqueCoursesByAccountID(@PathVariable Integer accountId) {
        Set<String> uniqueCourses = orderService.getAllUniqueCourses(accountId);
        return ResponseEntity.ok(uniqueCourses);
    }

    //Total amount of money that person has successfully ordered.
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT', 'ADMIN')")
    @GetMapping("/total/{accountId}")
    public ResponseEntity<BigDecimal> getTotalOrdersByAccount(@PathVariable Integer accountId) {
         BigDecimal total = orderService.getTotalOrdersByAccount(accountId);
            return ResponseEntity.ok(total);

    }
    @GetMapping("/daily-earnings")
    public List<DailyEarningsResponse> getDailyEarnings() {

        return orderService.getDailyEarningsByAccountId();
    }


}
