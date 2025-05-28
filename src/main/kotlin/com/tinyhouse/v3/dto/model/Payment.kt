package com.tinyhouse.v3.dto.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "payments")
data class Payment(
    @Id
    var id: UUID,

    var paymentDate: LocalDateTime,
    var amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    var paymentMethod: PaymentMethod,

    @Enumerated(EnumType.STRING)
    var status: PaymentStatus,

    @OneToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "reservation_id")
    @JsonIgnore
    var reservation: Reservation,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id")
    @JsonIgnore
    var user: User
){
    constructor():this(
        id = UUID.randomUUID(),
        paymentDate = LocalDateTime.now(),
        amount = BigDecimal.ZERO,
        paymentMethod = PaymentMethod.CREDIT_CARD,
        status = PaymentStatus.PENDING,
        reservation = Reservation(),
        user = User()

    )
}

enum class PaymentMethod {
    CREDIT_CARD, BANK_TRANSFER, PAYPAL
}

enum class PaymentStatus {
    SUCCESS, FAILED, PENDING
}
