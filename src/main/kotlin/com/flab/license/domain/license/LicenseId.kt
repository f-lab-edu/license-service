package com.flab.license.domain.license

import java.util.*

@JvmInline
value class LicenseId(val value: UUID) {

    companion object {
        fun generate(): LicenseId = LicenseId(UUID.randomUUID())
    }
}
