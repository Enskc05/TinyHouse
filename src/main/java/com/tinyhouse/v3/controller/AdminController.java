package com.tinyhouse.v3.controller;

import com.tinyhouse.v3.dto.*;
import com.tinyhouse.v3.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

//-------------------------KULLANICI----------------------------

    //Kullanıcı bilgileri düzenleme
    @PutMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> userUpdate(@PathVariable UUID id, @RequestBody UpdateUserDto requestDto){
        adminService.updateUser(id,requestDto);
        return ResponseEntity.ok().build();
    }

    //Kullanıcıyı silme
    @DeleteMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id){
        adminService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    //Kullanıcı ekleme
    @PostMapping(path = "/user/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> signup(@RequestBody RegisterRequestDto registerRequestDto){
        adminService.register(registerRequestDto);
        return ResponseEntity.ok().build();
    }

    //Kullanıcıları listeler
    @GetMapping("/user/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserInfoResponseDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

//------------------------REZERVASYON-----------------------------

    //Rezervasyonları listeleme
    @GetMapping("/reservation/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservationList>> getAllReservations(){
        return ResponseEntity.ok(adminService.getAllReservationsForAdmin());
    }
    //Rezervasyon iptal
    @PutMapping("/reservation/cancel/{reservationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReservationResponseDto> cancelReservation(
            @PathVariable UUID reservationId,
            @AuthenticationPrincipal(expression = "id") UUID adminId) {
        return ResponseEntity.ok(adminService.adminCancelReservation(reservationId, adminId));
    }
    //Rezervasyon Onaylama
    @PutMapping("/reservation/approve/{reservationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReservationResponseDto> approveReservation(
            @PathVariable UUID reservationId,
            @AuthenticationPrincipal(expression = "id") UUID adminId) {
        return ResponseEntity.ok(adminService.adminApproveReservation(reservationId, adminId));
    }

 //------------------------EV (HOUSE)-----------------------------

    // Tüm evleri listeler
    @GetMapping("/house/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<HouseListResponse>> listAllHouses() {
        return ResponseEntity.ok(adminService.listAllHouses());
    }

    // Evi günceller
    @PutMapping("/house/{houseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateHouse(
            @PathVariable UUID houseId,
            @RequestBody HouseDto houseDto) {
        adminService.updateHouse(houseId, houseDto);
        return ResponseEntity.ok().build();
    }

    // Evi siler
    @DeleteMapping("/house/{houseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteHouse(@PathVariable UUID houseId) {
        adminService.deleteHouse(houseId);
        return ResponseEntity.ok().build();
    }

//------------------------ÖDEME (PAYMENT)-----------------------------

    // Tüm ödemeleri getirir
    @GetMapping("/payment/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentListResponse>> getAllPayments() {
        return ResponseEntity.ok(adminService.getAllPayments());
    }

    // Belirli bir kullanıcının ödemelerini getirir
    @GetMapping("/payment/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentListResponse>> getPaymentsByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(adminService.getPaymentsByUser(userId));
    }

    // Sistemdeki toplam geliri getirir
    @GetMapping("/payment/total")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BigDecimal> getTotalSystemEarnings() {
        return ResponseEntity.ok(adminService.getTotalSystemEarnings());
    }

}
