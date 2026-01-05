package com.flab.license.domain.plan

import java.util.*

@JvmInline
value class PlanId(val value: UUID) {
    companion object {
        fun generate(): PlanId = PlanId(UUID.randomUUID())
    }
}
