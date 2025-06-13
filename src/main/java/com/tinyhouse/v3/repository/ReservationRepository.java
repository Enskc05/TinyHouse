package com.tinyhouse.v3.repository;

import com.tinyhouse.v3.model.House;
import com.tinyhouse.v3.model.Reservation;
import com.tinyhouse.v3.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findByRenter(User renter);
    List<Reservation> findByHouse(House houses);
    void deleteAllByRenter(User renter);

    @Query("SELECT r FROM Reservation r WHERE r.house.owner = :owner")
    List<Reservation> findByHouseOwner(@Param("owner") User owner);
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reservation r " +
            "WHERE r.renter.id = :renterId " +
            "AND r.house.id = :houseId " +
            "AND (" +
            "(:startDate BETWEEN r.startDate AND r.endDate) OR " +
            "(:endDate BETWEEN r.startDate AND r.endDate) OR " +
            "(r.startDate BETWEEN :startDate AND :endDate)" +
            ")")
    boolean existsOverlappingReservation(
            @Param("renterId") UUID renterId,
            @Param("houseId") UUID houseId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
