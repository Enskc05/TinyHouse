package com.tinyhouse.v3.service;

import com.tinyhouse.v3.dto.*;
import com.tinyhouse.v3.model.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AdminService {

    private final UserService userService;
    private final AuthService authService;
    private final ReservationService reservationService;
    private final HouseService houseService;
    private final PaymentService paymentService;
    private final ReviewService reviewService;

    public AdminService(UserService userService,
                        AuthService authService,
                        ReservationService reservationService,
                        HouseService houseService,
                        PaymentService paymentService,
                        ReviewService reviewService) {
        this.userService = userService;
        this.authService = authService;
        this.reservationService = reservationService;
        this.houseService = houseService;
        this.paymentService = paymentService;
        this.reviewService = reviewService;
    }

    // Kullanıcı güncelleme
    public void updateUser(UUID id, UpdateUserDto requestDto){
        userService.update(id, requestDto);
    }

    // Kullanıcı silme
    public void deleteUser(UUID id){
        User user = userService.findById(id);

        reservationService.deleteAllByRenter(user);
        houseService.deleteAllByOwner(user);
        reviewService.deleteAllByUser(user);
        paymentService.deleteAllByUser(user);
        userService.delete(user);
    }

    // Kullanıcı ekleme
    public void register(RegisterRequestDto registerRequestDto) {
        authService.register(registerRequestDto);
    }

    // Kullanıcı listeleme
    public List<UserInfoResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    // Rezervasyon listeleme
    public List<ReservationList> getAllReservationsForAdmin() {
        return reservationService.getAllReservationsForAdmin();
    }

    // Rezervasyon iptal etme
    public ReservationResponseDto adminCancelReservation(UUID reservationId, UUID adminId) {
        return reservationService.cancelReservation(reservationId, adminId);
    }

    // Rezervasyon onaylama
    public ReservationResponseDto adminApproveReservation(UUID reservationId, UUID adminId) {
        return reservationService.approveReservation(reservationId, adminId);
    }

    // Tüm evleri listele
    public List<HouseListResponse> listAllHouses() {
        return houseService.getAllHouses();
    }

    // Ev güncelle
    public void updateHouse(UUID houseId, HouseDto houseDto) {
        houseService.update(houseId, houseDto);
    }

    // Ev sil
    public void deleteHouse(UUID houseId) {
        houseService.delete(houseId);
    }

    // Tüm ödemeleri getir
    public List<PaymentListResponse> getAllPayments() {
        return paymentService.getAllPaymentsForAdmin();
    }

    // Belirli bir kullanıcının ödemeleri
    public List<PaymentListResponse> getPaymentsByUser(UUID userId) {
        return paymentService.getPaymentsByUserId(userId);
    }

    // Sistemdeki toplam gelir
    public BigDecimal getTotalSystemEarnings() {
        return paymentService.getTotalEarnings();
    }


}
