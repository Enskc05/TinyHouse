package com.tinyhouse.v3.dto

import com.tinyhouse.v3.model.UserRole
import java.time.LocalDateTime
import java.util.UUID

data class UserInfoResponseDto(
    val id: UUID,
    val name: String,
    val surname: String,
    val mail: String,
    val role: UserRole,
    val status: Boolean,
    val createdAt: LocalDateTime
)