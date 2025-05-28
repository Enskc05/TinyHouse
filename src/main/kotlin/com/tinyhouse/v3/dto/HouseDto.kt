package com.tinyhouse.v3.dto

import java.math.BigDecimal
import java.time.LocalDate

data class HouseDto(
    val title: String,
    val description: String,
    val location: String,
    val pricePerNight: BigDecimal,
    val isActive: Boolean,
    val availableFrom: LocalDate,
    val availableTo: LocalDate,
    val ownerEmail: String,
    val imageUrls: List<String>
)
