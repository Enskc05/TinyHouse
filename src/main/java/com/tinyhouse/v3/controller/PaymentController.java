package com.tinyhouse.v3.controller;


import com.tinyhouse.v3.dto.PaymentListResponse;
import com.tinyhouse.v3.dto.PaymentRequestDto;
import com.tinyhouse.v3.dto.model.Payment;
import com.tinyhouse.v3.security.CustomUserDetails;
import com.tinyhouse.v3.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    @PostMapping
    @PreAuthorize("hasRole('RENTER')")
    public ResponseEntity<Payment> processPayment(@RequestBody PaymentRequestDto request,
                                                  Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Payment payment = paymentService.processPayment(request, userDetails.getId());
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/owner")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<PaymentListResponse>> getCurrentUsersPayment(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<PaymentListResponse> payments = paymentService.getPaymentByOwnerId(userDetails.getUsername());
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/total")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<BigDecimal> getTotalEarningsByOwner(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        BigDecimal total = paymentService.getTotalEarningsByOwner(userDetails.getId());
        return ResponseEntity.ok(total);
    }
}