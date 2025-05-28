package com.tinyhouse.v3.dto

import java.util.UUID

data class AuthResponseDto(
    val token: String,
    val id: UUID
)
