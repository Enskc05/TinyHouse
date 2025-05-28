package com.tinyhouse.v3.dto

import java.time.LocalDate
import java.util.UUID

data class ReservationResponseDto(
    val id: UUID,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: String,
    val houseTitle: String,
    val renterName: String
)
