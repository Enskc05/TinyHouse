package com.tinyhouse.v3.dto

import com.tinyhouse.v3.dto.model.UserRole
import java.time.LocalDateTime

data class UserInfoResponseDto(
    val name: String,
    val surname: String,
    val mail: String,
    val role: UserRole,
    val status: Boolean,
    val createdAt: LocalDateTime
)
