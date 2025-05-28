package com.tinyhouse.v3.dto

import com.tinyhouse.v3.dto.model.UserRole

data class RegisterRequestDto(
    val name: String,
    val surname: String,
    val email: String,
    val password: String,
    val role: UserRole
)
