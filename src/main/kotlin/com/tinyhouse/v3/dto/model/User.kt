package com.tinyhouse.v3.dto.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "users")
data class User(
    @Id
    val id: UUID? = UUID.randomUUID(),
    val name: String,
    val surname: String,

    @Column(unique = true, nullable = false)
    val email: String,

    private val password: String,

    @Enumerated(EnumType.STRING)
    val role: UserRole,

    val status: Boolean,
    val createdAt: LocalDateTime,

    @OneToMany(mappedBy = "owner",fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JsonIgnore
    val houses: List<House>,

    @OneToMany(mappedBy = "renter",fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JsonIgnore
    val reservations: List<Reservation>,

    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JsonIgnore
    val reviews: List<Review>,

    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JsonIgnore
    val payments: List<Payment>

) : UserDetails {
    constructor() : this(
        id = null,
        name = "",
        surname = "",
        email = "",
        password = "",
        role = UserRole.RENTER,
        status = true,
        createdAt = LocalDateTime.now(),
        houses = emptyList(),
        reservations = emptyList(),
        reviews = emptyList(),
        payments = emptyList()
    )

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(role)
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return email
    }

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = status
}
