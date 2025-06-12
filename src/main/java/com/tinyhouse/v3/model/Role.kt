package com.tinyhouse.v3.model

import org.springframework.security.core.GrantedAuthority

enum class UserRole : GrantedAuthority {
    RENTER,
    OWNER,
    ADMIN;

    companion object {
        private const val ROLE_PREFIX = "ROLE_"

        fun fromAuthority(authority: String): UserRole {
            val roleName = authority.removePrefix(ROLE_PREFIX)
            return valueOf(roleName)
        }
    }

    override fun getAuthority(): String {
        return ROLE_PREFIX + name
    }

    fun isAdmin(): Boolean = this == ADMIN
    fun isOwner(): Boolean = this == OWNER
    fun isRenter(): Boolean = this == RENTER

}
