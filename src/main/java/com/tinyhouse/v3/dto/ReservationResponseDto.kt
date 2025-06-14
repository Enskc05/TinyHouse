package com.tinyhouse.v3.dto

import com.tinyhouse.v3.model.ReservationStatus
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class ReservationResponseDto(
    val id: UUID,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: ReservationStatus,
    val houseTitle: String,
    val renterName: String,
    val amount: BigDecimal
)