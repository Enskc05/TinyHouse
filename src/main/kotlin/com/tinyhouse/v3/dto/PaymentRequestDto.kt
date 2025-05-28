package com.tinyhouse.v3.dto

import com.tinyhouse.v3.dto.model.PaymentMethod
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class PaymentRequestDto(
    val reservationId: UUID,
    val paymentDate: LocalDateTime,
    val amount: BigDecimal,
    val paymentMethod: PaymentMethod
)