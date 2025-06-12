package com.tinyhouse.v3.dto

import com.tinyhouse.v3.model.ReservationStatus
import java.time.LocalDateTime
import java.util.UUID

data class ReservationList(
    val ownerId: UUID,
    val renterId: UUID,
    val status: ReservationStatus,
    val createdAt: LocalDateTime
)
