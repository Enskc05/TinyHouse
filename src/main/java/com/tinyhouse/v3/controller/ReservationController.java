package com.tinyhouse.v3.controller;

import com.tinyhouse.v3.dto.ReservationRequestDto;
import com.tinyhouse.v3.dto.ReservationResponseDto;
import com.tinyhouse.v3.security.CustomUserDetails;
import com.tinyhouse.v3.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reservation")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }
    @PreAuthorize("hasRole('RENTER')")
    @PostMapping(path = "/create")
    public ResponseEntity<ReservationResponseDto> create(@RequestBody ReservationRequestDto requestDto){
        return ResponseEntity.ok(reservationService.createReservation(requestDto));
    }

    @PreAuthorize("hasRole('RENTER')")
    @DeleteMapping("/cancel/{reservationId}")
    public ResponseEntity<ReservationResponseDto> cancelReservation(
            @PathVariable UUID reservationId,
            Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getId();
        ReservationResponseDto response = reservationService.cancelReservation(reservationId, userId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/list")
    @PreAuthorize("hasRole('RENTER')")
    public ResponseEntity<List<ReservationResponseDto>> getMyReservations(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        List<ReservationResponseDto> reservations = reservationService.getReservationsByRenter(userId);
        return ResponseEntity.ok(reservations);
    }
    @GetMapping("/owner-list")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<ReservationResponseDto>> getOwnerReservations(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUser().getId();

        List<ReservationResponseDto> reservations = reservationService.getReservationsByOwner(userId);
        return ResponseEntity.ok(reservations);
    }
}
