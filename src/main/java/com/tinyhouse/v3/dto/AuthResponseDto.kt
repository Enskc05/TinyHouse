package com.tinyhouse.v3.dto

import com.tinyhouse.v3.model.UserRole
import java.util.UUID

data class AuthResponseDto(
    val token: String,
    val id: UUID,
    val role: UserRole
)