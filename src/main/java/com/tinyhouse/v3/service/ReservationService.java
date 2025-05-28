package com.tinyhouse.v3.service;

import com.tinyhouse.v3.config.ReservationCancellationException;
import com.tinyhouse.v3.config.ReservationNotFoundException;
import com.tinyhouse.v3.dto.ReservationRequestDto;
import com.tinyhouse.v3.dto.ReservationResponseDto;
import com.tinyhouse.v3.dto.model.House;
import com.tinyhouse.v3.dto.model.Reservation;
import com.tinyhouse.v3.dto.model.ReservationStatus;
import com.tinyhouse.v3.dto.model.User;
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

    public ReservationResponseDto createReservation(ReservationRequestDto request) {
        User renter = userService.getUserById(request.getRenterId());
        House house = houseService.getHouseById(request.getHouseId());

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
                saved.getStatus().name(),
                saved.getHouse().getTitle(),
                saved.getRenter().getName() + " " + saved.getRenter().getSurname()
        );
    }
    @Transactional
    public ReservationResponseDto cancelReservation(UUID reservationId, UUID userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        validateCancellation(reservation, userId);


        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation cancelledReservation = reservationRepository.save(reservation);

        houseService.updateHouseAvailability(reservation.getHouse());

        return convertToDto(cancelledReservation);
    }

    private void validateCancellation(Reservation reservation, UUID userId) {
        // Sadece rezervasyon sahibi veya ev sahibi iptal edebilir
        if (!reservation.getRenter().getId().equals(userId) &&
                !reservation.getHouse().getOwner().getId().equals(userId)) {
            throw new ReservationCancellationException("Only renter or owner can cancel this reservation");
        }

        // Zaten iptal edilmiş rezervasyon
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new ReservationCancellationException("Reservation is already cancelled");
        }

        // Başlamış rezervasyon iptal edilemez
        if (reservation.getStartDate().isBefore(LocalDate.now())) {
            throw new ReservationCancellationException("Cannot cancel started reservation");
        }
    }

    private ReservationResponseDto convertToDto(Reservation reservation) {
        return new ReservationResponseDto(
                reservation.getId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getStatus().name(),
                reservation.getHouse().getTitle(),
                reservation.getRenter().getName() + " " + reservation.getRenter().getSurname()
        );
    }
    // Kullanıcıya ait rezervasyonları getir
    public List<ReservationResponseDto> getReservationsByRenter(UUID renterId) {
        User renter = userService.getUserById(renterId);
        List<Reservation> reservations = reservationRepository.findByRenter(renter);

        return reservations.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<ReservationResponseDto> getReservationsByOwner(UUID ownerId) {
        User owner = userService.getUserByIdOwner(ownerId);
        List<Reservation> reservations = reservationRepository.findByHouseOwner(owner);

        return reservations.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private ReservationResponseDto mapToResponseDto(Reservation reservation) {
        return new ReservationResponseDto(
                reservation.getId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getStatus().name(),
                reservation.getHouse().getTitle(),
                reservation.getRenter().getName() + " " + reservation.getRenter().getSurname()
        );
    }
    public Reservation getReservationById(@NotNull UUID reservationID){
        return reservationRepository.findById(reservationID)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with ID: " + reservationID));
    }
}
