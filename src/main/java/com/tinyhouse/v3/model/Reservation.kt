package com.tinyhouse.v3.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "reservations")
data class Reservation(
    @Id
    val id: UUID? = UUID.randomUUID(),

    val startDate: LocalDate,
    val endDate: LocalDate,

    @Enumerated(EnumType.STRING)
    var status: ReservationStatus = ReservationStatus.PENDING,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "renter_id")
    @JsonIgnore
    val renter: User,

    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "house_id")
    @JsonIgnore
    val house: House,

    @OneToOne(mappedBy = "reservation",fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JsonIgnore
    var payment: Payment? = null
){
    constructor():this(
        UUID.randomUUID(),
        LocalDate.now(),
        LocalDate.now(),
        ReservationStatus.PENDING,
        LocalDateTime.now(),
        User(),
        House(),
        null
    )
}

enum class ReservationStatus {
    PENDING, CONFIRMED, CANCELLED
}

