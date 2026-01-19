package com.flab.license.domain.license

import com.flab.license.domain.license.exception.LicenseErrorCode
import com.flab.license.domain.license.exception.LicenseException

@ConsistentCopyVisibility
data class Owner private constructor(
    val type: Type,
    val id: String
) {
    enum class Type {
        USER,
        ORGANIZATION
    }

    companion object {
        fun user(id: String): Owner = of(Type.USER, id)

        fun organization(id: String): Owner = of(Type.ORGANIZATION, id)

        fun reconstitute(type: Type, id: String) = Owner(type, id)

        private fun of(type: Type, id: String): Owner {
            if (id.isBlank()) {
                throw LicenseException(LicenseErrorCode.INVALID_OWNER_ID)
            }
            return Owner(type, id)
        }
    }
}
