package com.flab.license.domain.license

enum class LicenseStatus {
    ACTIVE,
    EXPIRED;

    fun isActive(): Boolean = this == ACTIVE
    fun isNotActive(): Boolean = this != ACTIVE
    fun isExpired(): Boolean = this == EXPIRED
}
