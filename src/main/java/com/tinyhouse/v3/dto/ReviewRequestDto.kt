package com.tinyhouse.v3.dto

import java.util.UUID

data class ReviewRequestDto(
    val userId: UUID,
    val houseId: UUID,
    val rating: Int,
    val comment: String
)