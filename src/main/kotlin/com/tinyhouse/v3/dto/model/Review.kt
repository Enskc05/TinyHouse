package com.tinyhouse.v3.dto.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "reviews")
data class Review(

    @Id
    var id: UUID? = UUID.randomUUID(),

    var rating: Int,
    var comment: String,
    var createdAt: LocalDateTime,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id")
    @JsonIgnore
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "house_id")
    @JsonIgnore
    var house: House
){
    constructor():this(
        id = null,
        rating = 0,
        comment = "",
        createdAt = LocalDateTime.now(),
        user = User(),
        House()
    )
}
