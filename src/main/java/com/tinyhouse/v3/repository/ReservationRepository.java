package com.tinyhouse.v3.repository;

import com.tinyhouse.v3.dto.model.House;
import com.tinyhouse.v3.dto.model.Reservation;
import com.tinyhouse.v3.dto.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findByRenter(User renter);
    List<Reservation> findByHouse(House houses);

    @Query("SELECT r FROM Reservation r WHERE r.house.owner = :owner")
    List<Reservation> findByHouseOwner(@Param("owner") User owner);
}
