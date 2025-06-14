package com.tinyhouse.v3.dto

import com.tinyhouse.v3.model.PaymentMethod
import com.tinyhouse.v3.model.PaymentStatus
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class PaymentListResponse(
    val renterId: UUID,
    val paymentDate: LocalDateTime,
    val amount: BigDecimal,
    val paymentMethod: PaymentMethod,
    val status: PaymentStatus
)
