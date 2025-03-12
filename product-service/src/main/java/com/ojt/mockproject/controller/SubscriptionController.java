package com.ojt.mockproject.controller;
import com.ojt.mockproject.dto.Account.Requests.SubscriptionRequestDTO;
import com.ojt.mockproject.dto.Account.Responses.SubscriptionResponseDTO;
import com.ojt.mockproject.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/subscriptions")
@CrossOrigin("*")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT')")
    @PostMapping("/view-sub")
    public ResponseEntity<SubscriptionResponseDTO> getUserSubscriptions(@RequestBody @Valid SubscriptionRequestDTO request) {
        SubscriptionResponseDTO subscriptions = subscriptionService.getUserSubscriptions(request.getAccountId());
        return ResponseEntity.ok(subscriptions);
    }
}
