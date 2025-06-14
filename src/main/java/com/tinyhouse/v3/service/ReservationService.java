package com.tinyhouse.v3.service;

import com.tinyhouse.v3.config.ReservationCancellationException;
import com.tinyhouse.v3.config.ReservationNotFoundException;
import com.tinyhouse.v3.dto.ReservationList;
import com.tinyhouse.v3.dto.ReservationRequestDto;
import com.tinyhouse.v3.dto.ReservationResponseDto;
import com.tinyhouse.v3.model.*;
import com.tinyhouse.v3.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final HouseService houseService;

    public ReservationService(ReservationRepository reservationRepository, UserService userService, HouseService houseService) {
        this.reservationRepository = reservationRepository;
        this.userService = userService;
        this.houseService = houseService;
    }

    // Rezervasyon oluşturma
    public ReservationResponseDto createReservation(ReservationRequestDto request) {
        User renter = userService.getUserById(request.getRenterId());
        House house = houseService.getHouseById(request.getHouseId());

        boolean existsOverlap = reservationRepository.existsOverlappingReservation(
                renter.getId(),
                house.getId(),
                request.getStartDate(),
                request.getEndDate()
        );

        if (existsOverlap) {
            throw new IllegalStateException("Aynı tarih aralığında zaten bir rezervasyonunuz var.");
        }

        Reservation reservation = new Reservation(
                UUID.randomUUID(),
                request.getStartDate(),
                request.getEndDate(),
                ReservationStatus.PENDING,
                LocalDateTime.now(),
                renter,
                house,
                null
        );

        Reservation saved = reservationRepository.save(reservation);

        return new ReservationResponseDto(
                saved.getId(),
                saved.getStartDate(),
                saved.getEndDate(),
                saved.getStatus(),
                saved.getHouse().getTitle(),
                saved.getRenter().getName() + " " + saved.getRenter().getSurname()
        );
    }


    @Transactional
    public ReservationResponseDto approveReservation(UUID reservationId, UUID approverId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        User approver = userService.findById(approverId);
        User owner = reservation.getHouse().getOwner();

        boolean isAuthorized = approver.getId().equals(owner.getId()) ||
                approver.getRole() == UserRole.ADMIN;

        if (!isAuthorized) {
            throw new SecurityException("Bu işlem için yetkiniz yok. Sadece ev sahibi veya admin onaylayabilir.");
        }

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Sadece bekleyen rezervasyonlar onaylanabilir.");
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        Reservation approved = reservationRepository.save(reservation);
        houseService.updateHouseAvailability(reservation.getHouse());

        return convertToDto(approved);
    }

    @Transactional
    public ReservationResponseDto cancelReservation(UUID reservationId, UUID userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        User user = userService.findById(userId);
        User owner = reservation.getHouse().getOwner();

        // Yetki kontrolü: Kiracı VEYA ev sahibi VEYA admin
        boolean isAuthorized = reservation.getRenter().getId().equals(userId) ||
                user.getId().equals(owner.getId()) ||
                user.getRole() == UserRole.ADMIN;

        if (!isAuthorized) {
            throw new SecurityException("Bu işlem için yetkiniz yok.");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Rezervasyon zaten iptal edilmiş.");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation cancelled = reservationRepository.save(reservation);
        houseService.updateHouseAvailability(reservation.getHouse());

        return convertToDto(cancelled);
    }

    // Kullanıcıya ait rezervasyonları getir
    public List<ReservationResponseDto> getReservationsByRenter(UUID renterId) {
        User renter = userService.getUserById(renterId);
        List<Reservation> reservations = reservationRepository.findByRenter(renter);

        return reservations.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // Ev sahibine ait rezervasyonları getir
    public List<ReservationResponseDto> getReservationsByOwner(UUID ownerId) {
        User owner = userService.getUserByIdOwner(ownerId);
        List<Reservation> reservations = reservationRepository.findByHouseOwner(owner);

        return reservations.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // Admin için rezervasyonları getir
    public List<ReservationList> getAllReservationsForAdmin() {
        return reservationRepository.findAll().stream()
                .map(reservation -> new ReservationList(
                        reservation.getHouse().getOwner().getId(),
                        reservation.getRenter().getId(),
                        reservation.getStatus(),
                        reservation.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    public Reservation findById(@NotNull UUID reservationID) {
        return reservationRepository.findById(reservationID)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with ID: " + reservationID));
    }

    public void deleteAllByRenter(User user) {
        reservationRepository.deleteAllByRenter(user);
    }

    // ---------------------------- PRIVATE METHODS ----------------------------

    private void validateCancellation(Reservation reservation, UUID userId) {
        User requester = userService.getUserById(userId);

        boolean isRenter = reservation.getRenter().getId().equals(userId);
        boolean isOwner = reservation.getHouse().getOwner().getId().equals(userId);
        boolean isAdmin = requester.getRole() == UserRole.ADMIN;

        if (!isRenter && !isOwner && !isAdmin) {
            throw new ReservationCancellationException("Sadece kiracı, ev sahibi veya admin iptal edebilir.");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new ReservationCancellationException("Rezervasyon zaten iptal edilmiş.");
        }

        if (reservation.getStartDate().isBefore(LocalDate.now())) {
            throw new ReservationCancellationException("Başlamış rezervasyon iptal edilemez.");
        }
    }

    private ReservationResponseDto convertToDto(Reservation reservation) {
        return new ReservationResponseDto(
                reservation.getId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getStatus(),
                reservation.getHouse().getTitle(),
                reservation.getRenter().getName() + " " + reservation.getRenter().getSurname()
        );
    }

    private ReservationResponseDto mapToResponseDto(Reservation reservation) {
        return new ReservationResponseDto(
                reservation.getId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getStatus(),
                reservation.getHouse().getTitle(),
                reservation.getRenter().getName() + " " + reservation.getRenter().getSurname()
        );
    }
}
