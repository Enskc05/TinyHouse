package com.tinyhouse.v3.dto.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "houses")
data class House(
    @Id
    var id: UUID? = UUID.randomUUID(),

    var title: String,
    var description: String,
    var location: String,
    var pricePerNight: BigDecimal,
    var isActive: Boolean,
    var availableFrom: LocalDate,
    var availableTo: LocalDate,
    var createdAt: LocalDateTime,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    var owner: User,

    @OneToMany(mappedBy = "house", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore
    var reservations: List<Reservation>,

    @OneToMany(mappedBy = "house", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore
    var reviews: List<Review>,

    @OneToMany(mappedBy = "house", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore
    var images: List<HouseImage>
){
    constructor() : this(
        id = null,
        title = "",
        description = "",
        location = "",
        pricePerNight = BigDecimal.ZERO,
        isActive = true,
        availableFrom = LocalDate.now(),
        availableTo = LocalDate.now(),
        createdAt = LocalDateTime.now(),
        owner = User(),
        reservations = emptyList(),
        reviews = emptyList(),
        images = emptyList()
    )
}
