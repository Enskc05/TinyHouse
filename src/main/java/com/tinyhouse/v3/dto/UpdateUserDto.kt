package com.tinyhouse.v3.dto

data class UpdateUserDto(
    val name: String,
    val surname: String,
    val email: String,
    val status: Boolean
)
