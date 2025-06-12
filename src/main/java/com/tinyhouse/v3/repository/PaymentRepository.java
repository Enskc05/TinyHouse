package com.tinyhouse.v3.repository;

import com.tinyhouse.v3.model.Payment;
import com.tinyhouse.v3.model.Reservation;
import com.tinyhouse.v3.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;


@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByReservationIn(List<Reservation> reservations);
    void deleteAllByUser(User user);
    List<Payment> findByUser(User user);

}

