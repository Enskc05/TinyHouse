package com.tinyhouse.v3.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "house_images")
data class HouseImage(
    @Id
    val id: UUID? = UUID.randomUUID(),

    @Lob
    @Column(columnDefinition = "TEXT")
    var imageUrl: String,
    var description: String? = null,

    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "house_id")
    @JsonIgnore
    val house: House
)