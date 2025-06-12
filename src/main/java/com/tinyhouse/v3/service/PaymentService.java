package com.tinyhouse.v3.service;

import com.tinyhouse.v3.dto.PaymentListResponse;
import com.tinyhouse.v3.dto.PaymentRequestDto;
import com.tinyhouse.v3.model.*;
import com.tinyhouse.v3.repository.HouseRepository;
import com.tinyhouse.v3.repository.PaymentRepository;
import com.tinyhouse.v3.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final HouseRepository houseRepository;
    private final UserService userService;

    public PaymentService(PaymentRepository paymentRepository,
                          ReservationRepository reservationRepository,
                          HouseRepository houseRepository,
                          UserService userService) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.houseRepository = houseRepository;
        this.userService = userService;
    }

    @Transactional
    public Payment processPayment(PaymentRequestDto request, UUID renterId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(request.getReservationId());
        if (!reservationOpt.isPresent()) {
            throw new IllegalArgumentException("Rezervasyon bulunamadı: " + request.getReservationId());
        }
        Reservation reservation = reservationOpt.get();

        if (!reservation.getRenter().getId().equals(renterId)) {
            throw new org.springframework.security.access.AccessDeniedException("Erişim yetkisi");
        }

        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setReservation(reservation);
        payment.setUser(reservation.getRenter());

        Payment savedPayment = paymentRepository.save(payment);
        reservation.setPayment(savedPayment);
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);

        return savedPayment;
    }

    @Transactional
    public Payment getPaymentById(UUID paymentId) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (!paymentOpt.isPresent()) {
            throw new IllegalArgumentException("Ödeme bulunamadı: " + paymentId);
        }
        return paymentOpt.get();
    }

    @Transactional
    public Payment refundPayment(UUID paymentId) {
        Payment payment = getPaymentById(paymentId);

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Sadece başarılı ödemeler iade edilebilir");
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment.getReservation().setStatus(ReservationStatus.CANCELLED);

        return paymentRepository.save(payment);
    }

    @Transactional
    public List<PaymentListResponse> getPaymentByOwnerId(String ownerEmail) {
        User owner = userService.findByEmail(ownerEmail);
        if (owner == null) {
            throw new IllegalArgumentException("Ev sahibi bulunamadı: " + ownerEmail);
        }

        List<Reservation> reservations = reservationRepository.findByHouseOwner(owner);
        List<Payment> payments = paymentRepository.findByReservationIn(reservations);

        return payments.stream()
                .map(this::convertToPaymentListResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<PaymentListResponse> getPaymentsByOwnerAndProperty(UUID houseId, UUID ownerId) {
        Optional<House> houseOpt = houseRepository.findById(houseId);
        if (!houseOpt.isPresent()) {
            throw new IllegalArgumentException("Ev bulunamadı: " + houseId);
        }
        House house = houseOpt.get();

        if (!house.getOwner().getId().equals(ownerId)) {
            throw new org.springframework.security.access.AccessDeniedException("Erişim yetkisi");
        }

        List<Reservation> reservations = reservationRepository.findByHouse(house);
        List<Payment> payments = paymentRepository.findByReservationIn(reservations);

        return payments.stream()
                .map(this::convertToPaymentListResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BigDecimal getTotalEarningsByOwner(UUID ownerId) {
        User owner = userService.getUserByIdOwner(ownerId);
        if (owner == null) {
            throw new IllegalArgumentException("Ev sahibi bulunamadı: " + ownerId);
        }

        List<Reservation> reservations = reservationRepository.findByHouseOwner(owner);
        List<Payment> payments = paymentRepository.findByReservationIn(reservations);

        return payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    @Transactional
    public List<PaymentListResponse> getAllPaymentsForAdmin() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(this::convertToPaymentListResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<PaymentListResponse> getPaymentsByUserId(UUID userId) {
        User user = userService.getUserById(userId);
        List<Payment> payments = paymentRepository.findByUser(user);

        return payments.stream()
                .map(this::convertToPaymentListResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BigDecimal getTotalEarnings() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private PaymentListResponse convertToPaymentListResponse(Payment payment) {
        PaymentListResponse response = new PaymentListResponse(
                payment.getId(),
                payment.getPaymentDate(),
                payment.getAmount(),
                payment.getPaymentMethod()
        );
        return response;
    }
    public void deleteAllByUser(User user){
        paymentRepository.deleteAllByUser(user);
    }

}
