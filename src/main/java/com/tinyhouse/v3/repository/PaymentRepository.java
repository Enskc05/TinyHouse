package com.tinyhouse.v3.repository;

import com.tinyhouse.v3.dto.model.Payment;
import com.tinyhouse.v3.dto.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;


@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByReservationIn(List<Reservation> reservations);}
