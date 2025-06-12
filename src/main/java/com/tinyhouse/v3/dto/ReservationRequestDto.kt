package com.tinyhouse.v3.dto

import java.time.LocalDate
import java.util.UUID

data class ReservationRequestDto(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val renterId: UUID,
    val houseId: UUID
)